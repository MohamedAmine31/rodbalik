"""
Vues de gestion des signalements
Endpoints: /api/signalement/*, /api/signalements/*
"""

import os
import requests
import base64
from tempfile import NamedTemporaryFile
from io import BytesIO
from rest_framework import generics, status, permissions
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from rest_framework.views import APIView
from django.shortcuts import get_object_or_404
from django.db.models import Count, Q
from django.utils import timezone
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework.filters import OrderingFilter, SearchFilter
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework import generics


from ..models import IASignalement
from ..models import Signalement
from ..serializers import (
    SignalementIASerializer,
    SignalementListSerializer,
    SignalementListWithIdSerializer,
    SignalementDetailSerializer,
    SignalementCreateSerializer,
    SignalementModerationSerializer,
)
from ..permissions import IsAdminUser, IsOwnerOrAdmin



BOT_TOKEN = os.environ.get('TELEGRAM_BOT_TOKEN', "8233311490:AAFYR7WjaNrGsv-_avUsvesis7USp-fPIRU")
CHAT_ID = os.environ.get('TELEGRAM_CHAT_ID', "@rodbalik")


def send_signalement_to_telegram(message, photo=None):
    """
    Envoie un signalement approuvé dans le canal Telegram.
    :param message: texte à envoyer
    :param photo: photo (peut être URL, chemin local, ou file_id Telegram)
    :return: True si succès, False sinon
    """
    if not BOT_TOKEN or not CHAT_ID:
        print("Erreur: BOT_TOKEN ou CHAT_ID non configuré.")
        return False

    try:
        if photo and str(photo).strip():
            url = f"https://api.telegram.org/bot{BOT_TOKEN}/sendPhoto"
            photo_str = str(photo).strip()

            # Si c'est un chemin local relatif ou absolu
            if not photo_str.startswith('http'):
                # Essayer comme chemin local d'abord
                if os.path.isfile(photo_str):
                    with open(photo_str, 'rb') as f:
                        files = {'photo': f}
                        data = {'chat_id': CHAT_ID, 'caption': message}
                        response = requests.post(url, files=files, data=data, timeout=10)
                        return response.status_code == 200
                else:
                    # Essayer comme chemin relatif au media root
                    from django.conf import settings
                    full_path = os.path.join(settings.MEDIA_ROOT, photo_str.lstrip('/'))
                    if os.path.isfile(full_path):
                        with open(full_path, 'rb') as f:
                            files = {'photo': f}
                            data = {'chat_id': CHAT_ID, 'caption': message}
                            response = requests.post(url, files=files, data=data, timeout=10)
                            return response.status_code == 200
                    print(f"Photo non trouvée: {photo_str}")
                    return False

            # Si c'est une URL publique
            else:
                payload = {"chat_id": CHAT_ID, "photo": photo_str, "caption": message}
                response = requests.post(url, data=payload, timeout=10)
                if response.status_code == 200:
                    return True
                print(f"Erreur Telegram: {response.status_code} - {response.text}")
                return False

        else:
            # Envoyer seulement le message texte
            url = f"https://api.telegram.org/bot{BOT_TOKEN}/sendMessage"
            payload = {"chat_id": CHAT_ID, "text": message}
            response = requests.post(url, data=payload, timeout=10)
            return response.status_code == 200

    except Exception as e:
        print(f"Erreur lors de l'envoi Telegram: {e}")
        return False


class SignalementListView(generics.ListAPIView):
    """
    API de liste des signalements avec filtres avancés

    GET /api/signalements/

    Paramètres de filtrage:
    - statut: Filtrer par statut (EN_COURS, APPROUVE, REJETE)
    - category: Filtrer par catégorie (ID)
    - localisation: Recherche par localisation
    - ordering: Trier par date
    - search: Recherche textuelle dans description et localisation
    """
    serializer_class = SignalementListSerializer
    filter_backends = [DjangoFilterBackend, OrderingFilter, SearchFilter]
    filterset_fields = ['statut', 'category']
    ordering_fields = ['date']
    ordering = ['-date']
    search_fields = ['description']
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]
    pagination_class = None

    def get_queryset(self):
        """Optimise les requêtes avec select_related"""
        queryset = Signalement.objects.select_related('author', 'category')
        return queryset
    
    
    
    
    
    
class SignalementIAListAPIView(generics.ListAPIView):
    serializer_class = SignalementIASerializer

    def get_queryset(self):
        return Signalement.objects.filter(statut='EN_COURS')





class SignalementListWithIdView(generics.ListAPIView):
    """
    API de liste des signalements avec ID inclus et filtres avancés

    GET /api/signalements/with-id/

    Paramètres de filtrage:
    - statut: Filtrer par statut (EN_COURS, APPROUVE, REJETE)
    - category: Filtrer par catégorie (ID)
    - localisation: Recherche par localisation
    - ordering: Trier par date
    - search: Recherche textuelle dans description et localisation
    """
    serializer_class = SignalementListWithIdSerializer
    filter_backends = [DjangoFilterBackend, OrderingFilter, SearchFilter]
    filterset_fields = ['statut', 'category']
    ordering_fields = ['date']
    ordering = ['-date']
    search_fields = ['description']
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]
    pagination_class = None

    def get_queryset(self):
        """Optimise les requêtes avec select_related"""
        queryset = Signalement.objects.select_related('author', 'category')
        return queryset

class SignalementCreateView(generics.CreateAPIView):
    """
    API de soumission d'un nouveau signalement
    
    POST /api/signalement/soumettre/
    
    Champs requis:
    - description: Description détaillée
    - category: ID de la catégorie
    - localisation: Adresse ou localisation
    - media: Fichier média (optionnel)
    
    Réservé aux utilisateurs authentifiés
    """
  
    queryset = Signalement.objects.all()
    serializer_class = SignalementCreateSerializer
    parser_classes = (MultiPartParser, FormParser) 
    def perform_create(self, serializer):
        """Associe l'utilisateur connecté au signalement"""
        signalement = serializer.save(author=self.request.user)
        IASignalement.objects.create(
            signalement=signalement,
            photo=signalement.photo.url if signalement.photo else None,
            category=signalement.category.name,  # ou id selon ce que tu veux
            statut=signalement.statut,
            precision=0.0
        )
        return signalement
    


class SignalementDetailView(generics.ListAPIView):
    """
    API de détail des signalements de l'utilisateur connecté

    GET /api/signalements/me/

    Retourne tous les signalements de l'utilisateur connecté
    """
    serializer_class = SignalementDetailSerializer
    permission_classes = [permissions.IsAuthenticated]
    pagination_class = None

    def get_queryset(self):
        """Retourne les signalements de l'utilisateur connecté"""
        if getattr(self, 'swagger_fake_view', False):
            return Signalement.objects.none()
        return Signalement.objects.filter(
            author=self.request.user
        ).select_related('author', 'category', 'moderator').prefetch_related('medias')


class SignalementUpdateView(generics.UpdateAPIView):
    """
    API de modification d'un signalement

    PUT /api/signalements/update/ - Modification complète
    PATCH /api/signalements/update/ - Modification partielle

    Seul le propriétaire peut modifier ses signalements
    L'ID du signalement doit être passé dans le corps de la requête
    """
    serializer_class = SignalementCreateSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        """Retourne uniquement les signalements de l'utilisateur connecté"""
        if getattr(self, 'swagger_fake_view', False):
            return Signalement.objects.none()
        return Signalement.objects.filter(author=self.request.user).select_related('author', 'category')

    def get_object(self):
        """Récupère le signalement par ID passé dans le corps de la requête"""
        signalement_id = self.request.data.get('id')
        if not signalement_id:
            from rest_framework.exceptions import ValidationError
            raise ValidationError({'error': 'ID du signalement requis dans le corps de la requête'})

        try:
            signalement = self.get_queryset().get(pk=signalement_id)
            return signalement
        except Signalement.DoesNotExist:
            from rest_framework.exceptions import NotFound
            raise NotFound({'error': 'Signalement non trouvé ou accès non autorisé'})



class SignalementDeleteByIdView(generics.DestroyAPIView):
    """
    API de suppression d'un signalement par ID dans l'URL

    DELETE /api/signalement/supprimer/<id>/

    Réservé au propriétaire du signalement ou aux administrateurs
    """
    permission_classes = [IsOwnerOrAdmin]
    lookup_url_kwarg = 'id'

    def get_queryset(self):
        """Retourne les signalements avec permissions appropriées"""
        if getattr(self, 'swagger_fake_view', False):
            return Signalement.objects.none()
        return Signalement.objects.select_related('author', 'category')



class SignalementRejectView(APIView):
    """
    API de rejet d'un signalement par les modérateurs

    POST /api/signalement/rejeter/{id}/

    Body JSON (optionnel):
    {
        "moderation_comment": "Commentaire optionnel"
    }

    Rejette directement le signalement.

    Réservé aux administrateurs
    """
    permission_classes = [IsAdminUser]

    def post(self, request, id):
        signalement = get_object_or_404(Signalement, pk=id)
        comment = request.data.get('moderation_comment', '')

        signalement.statut = 'REJETE'
        signalement.moderator = request.user
        signalement.moderation_comment = comment
        signalement.moderated_at = timezone.now()
        signalement.save()

        return Response({
            'message': 'Signalement rejeté',
            'signalement': SignalementDetailSerializer(signalement).data
        }, status=status.HTTP_200_OK)


class SignalementValidationView(APIView):
    """
    API de validation d'un signalement par les modérateurs

    POST /api/signalement/valider/{id}/

    Body JSON (optionnel):
    {
        "moderation_comment": "Commentaire optionnel"
    }

    Valide le signalement et publie automatiquement sur le canal Telegram.

    Réservé aux administrateurs
    """
    permission_classes = [IsAdminUser]

    def post(self, request, id):
        signalement = get_object_or_404(Signalement, pk=id)
        comment = request.data.get('moderation_comment', '')

        signalement.statut = 'APPROUVE'
        signalement.moderator = request.user
        signalement.moderation_comment = comment
        signalement.moderated_at = timezone.now()
        signalement.save()

        # Publier sur Telegram
        telegram_message = (
            f"⚠ Nouveau signalement approuvé :\n\n"
            f"Description: {signalement.description}\n\n"
            f"Catégorie: {signalement.category.name}\n\n"
            f"Auteur: {"anonyme"}\n\n"
            f"Date: {signalement.date}"
        )
        
        photo_to_send = signalement.photo if signalement.photo else None
        print(f"[Telegram Debug] Photo value: {photo_to_send}")
        telegram_success = send_signalement_to_telegram(telegram_message, photo_to_send)

        message = 'Signalement approuvé'
        if telegram_success:
            message += ' et publié sur Telegram'
        else:
            message += ' (publication Telegram échouée)'

        return Response({
            'message': message,
            'signalement': SignalementDetailSerializer(signalement).data
        }, status=status.HTTP_200_OK)



class SignalementRetrieveView(generics.RetrieveAPIView):
    """
    API pour afficher les détails d'un signalement par ID

    GET /api/signalement/{id}/

    Accessible à tous (public)
    """
    serializer_class = SignalementDetailSerializer
    permission_classes = [permissions.AllowAny]

    def get_queryset(self):
        return Signalement.objects.select_related('author', 'category', 'moderator').prefetch_related('medias')
