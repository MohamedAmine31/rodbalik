"""
Vues d'authentification et gestion des profils utilisateurs
Endpoints: /api/auth/register/, /api/auth/profile/, /api/users/me/, /api/users/suggest-username/
"""

from rest_framework import generics, permissions, status
from rest_framework.response import Response
from rest_framework_simplejwt.views import TokenObtainPairView
from django.contrib.auth import get_user_model
from django.db.models import Q
import re
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi

from ..serializers import UserSerializer, UserRegistrationSerializer, UsernameAvailabilitySerializer, UserIdSerializer, CustomTokenObtainPairSerializer
from ..permissions import IsAdminUser

User = get_user_model()


class UserRegistrationView(generics.CreateAPIView):
    """
    API d'inscription d'un nouvel utilisateur
    
    POST /api/auth/register/
    - Crée un nouvel utilisateur avec email et mot de passe
    - Validation complète des données d'entrée
    - Retourne des messages d'erreur clairs en cas de validation échouée
    - Accessible à tous (AllowAny)
    """
    queryset = User.objects.all()
    serializer_class = UserRegistrationSerializer
    permission_classes = [permissions.AllowAny]
    
    def create(self, request, *args, **kwargs):
        """
        Amélioration de la gestion des erreurs de validation
        Retourne des messages d'erreur clairs et structurés
        """
        serializer = self.get_serializer(data=request.data)
        
        if not serializer.is_valid():
            # Retourne les erreurs de validation avec messages clairs
            errors = {}
            for field, messages in serializer.errors.items():
                errors[field] = messages[0] if isinstance(messages, list) else str(messages)
            
            return Response(
                {
                    'success': False,
                    'message': 'Erreur de validation lors de l\'inscription',
                    'errors': errors
                },
                status=status.HTTP_400_BAD_REQUEST
            )
        
        self.perform_create(serializer)
        headers = self.get_success_headers(serializer.data)
        
        return Response(
            {
                'success': True,
                'message': 'Inscription réussie! Vous pouvez maintenant vous connecter.',
                'user': serializer.data
            },
            status=status.HTTP_201_CREATED,
            headers=headers
        )


class UserProfileView(generics.RetrieveUpdateAPIView):
    """
    API pour récupérer et modifier le profil utilisateur
    
    GET /api/auth/profile/ - Récupère le profil de l'utilisateur connecté
    PUT /api/auth/profile/ - Modifie le profil de l'utilisateur connecté
    PATCH /api/auth/profile/ - Modification partielle du profil
    
    Réservé aux utilisateurs authentifiés
    """
    serializer_class = UserSerializer
    permission_classes = [permissions.IsAuthenticated]
    
    def get_object(self):
        """Retourne l'utilisateur connecté"""
        return self.request.user


class CurrentUserView(generics.RetrieveAPIView):
    """
    API pour récupérer le profil de l'utilisateur actuel
    
    GET /api/users/me/
    - Retourne les informations de l'utilisateur connecté
    - Réservé aux utilisateurs authentifiés
    """
    serializer_class = UserSerializer
    permission_classes = [permissions.IsAuthenticated]
    
    def get_object(self):
        """Retourne l'utilisateur connecté"""
        return self.request.user


class UsernameAvailabilityView(generics.GenericAPIView):
    """
    API pour vérifier la disponibilité d'un nom d'utilisateur
    et obtenir des suggestions alternatives
    
    POST /api/users/suggest-username/
    
    
    Accessible à tous (AllowAny)
    """
    permission_classes = [permissions.AllowAny]
    serializer_class = UsernameAvailabilitySerializer
    
    def post(self, request, *args, **kwargs):
        """
        Vérifie la disponibilité du username et retourne des suggestions
        """
        serializer = self.get_serializer(data=request.data)
        print(request.user, request.auth)

        if not serializer.is_valid():
            # Retourne les erreurs de validation avec messages clairs
            errors = {}
            for field, messages in serializer.errors.items():
                errors[field] = messages[0] if isinstance(messages, list) else str(messages)
            
            return Response(
                {
                    'success': False,
                    'message': 'Erreur de validation du nom d\'utilisateur',
                    'errors': errors
                },
                status=status.HTTP_400_BAD_REQUEST
            )
        
        username = serializer.validated_data['username']
        
        # Vérification de la disponibilité
        is_available = not User.objects.filter(username__iexact=username).exists()
        
        if is_available:
            return Response(
                {
                    'success': True,
                    'username': username,
                    'available': True,
                    'suggestions': [],
                    'message': 'Ce nom d\'utilisateur est disponible!'
                },
                status=status.HTTP_200_OK
            )
        
        suggestions = self._generate_suggestions(username, max_suggestions=2)
        
        return Response(
            {
                'success': True,
                'username': username,
                'available': False,
                'suggestions': suggestions,
                'message': 'Ce nom d\'utilisateur est déjà pris. Voici quelques suggestions alternatives:'
            },
            status=status.HTTP_200_OK
        )
    
    def _generate_suggestions(self, username, max_suggestions=2):
        """
        Génère des suggestions de noms d'utilisateur alternatifs intelligentes
        Limité à max_suggestions (par défaut 2)
        
        Stratégie:
        1. Ajoute des suffixes numériques (1, 2, 3, etc.)
        2. Ajoute des underscores avec descripteurs (pro, official, dev, etc.)
        3. Ajoute des années (2024, 2025)
        4. Filtre les noms déjà existants
        5. Retourne jusqu'à max_suggestions suggestions disponibles
        """
        suggestions = []
        
        descriptors = ['pro', 'official', 'dev', 'real', 'true', 'user', 'admin', 'team']
        
        variants = []
        
        for i in range(1, 11):
            variants.append(f"{username}{i}")
            variants.append(f"{username}_{i}")
        
        for desc in descriptors:
            variants.append(f"{username}_{desc}")
            variants.append(f"{desc}_{username}")
        
        # Variantes avec années
        variants.append(f"{username}_2024")
        variants.append(f"{username}_2025")
        variants.append(f"{username}_2026")
        
        # Variantes avec underscores
        variants.append(f"{username}_")
        variants.append(f"_{username}")
        variants.append(f"_{username}_")
        
        # Filtre les noms existants et ajoute les suggestions disponibles
        existing_usernames = set(
            User.objects.filter(username__in=variants).values_list('username', flat=True)
        )
        
        for variant in variants:
            if len(suggestions) >= max_suggestions:
                break
            
            if (variant.lower() not in existing_usernames and 
                len(variant) <= 150): 
                suggestions.append(variant)
        
        return suggestions


class CustomTokenObtainPairView(TokenObtainPairView):
    """
    Vue personnalisée pour l'obtention des tokens JWT avec l'ID utilisateur.
    """
    serializer_class = CustomTokenObtainPairSerializer


class GetTokenUserView(generics.RetrieveAPIView):
    """
    API pour récupérer l'ID de l'utilisateur connecté via le token.
    """
    permission_classes = [permissions.IsAuthenticated]
    serializer_class = UserIdSerializer

    @swagger_auto_schema(
        operation_description="Retourne l'ID de l'utilisateur connecté.",
        responses={200: openapi.Response('ID utilisateur', schema=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'user_id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID de l’utilisateur connecté'),
            },
        ))}
    )
    def retrieve(self, request, *args, **kwargs):
        return Response(
            {'user_id': request.user.id},
            status=status.HTTP_200_OK
        )


class UserListView(generics.ListAPIView):
    """
    API pour consulter la liste de tous les utilisateurs

    GET /api/users/
    - Retourne la liste de tous les utilisateurs
    - Réservé aux administrateurs uniquement
    """
    queryset = User.objects.all()
    serializer_class = UserSerializer
    permission_classes = [IsAdminUser]
