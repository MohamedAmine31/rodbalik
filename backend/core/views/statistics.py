"""
Vues de gestion des statistiques et tableaux de bord
Endpoints: /api/statistiques/*
"""

from rest_framework import generics, permissions
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from django.db.models import Count, Q

from ..models import Signalement, Statistique
from ..serializers import StatistiqueSerializer
from ..permissions import IsAdminUser


class StatistiqueListView(generics.ListAPIView):
    """
    API de liste des statistiques
    
    GET /api/statistiques/
    
    Paramètres de filtrage:
    - category: Filtrer par catégorie
    - date: Filtrer par date
    
    Réservé aux administrateurs
    """
    serializer_class = StatistiqueSerializer
    permission_classes = [IsAdminUser]
    
    def get_queryset(self):
        return Statistique.objects.select_related('category').order_by('-date')


@api_view(['GET'])
@permission_classes([IsAdminUser])
def statistiques_dashboard(request):
    """
    API du tableau de bord avec statistiques agrégées
    
    GET /api/statistiques/dashboard/
    
    Retourne:
    - Statistiques globales (total, approuvés, en attente, rejetés)
    - Répartition par catégorie
    - Top 10 des zones les plus signalées
    - Métriques d'engagement (vues, partages)
    
    Réservé aux administrateurs
    """
    
    # Statistiques par catégorie
    stats_by_category = Signalement.objects.values(
        'category__name'
    ).annotate(
        total_count=Count('id'),
        approved_count=Count('id', filter=Q(statut='approved')),
        pending_count=Count('id', filter=Q(statut='pending')),
        rejected_count=Count('id', filter=Q(statut='rejected'))
    ).order_by('-total_count')
    
    # Statistiques par zone (top 10)
    stats_by_zone = Signalement.objects.values(
        'latitude', 'longitude'
    ).annotate(
        count=Count('id')
    ).order_by('-count')[:10]
    
    # Statistiques globales
    total_signalements = Signalement.objects.count()
    total_approved = Signalement.objects.filter(statut='approved').count()
    total_pending = Signalement.objects.filter(statut='pending').count()
    total_rejected = Signalement.objects.filter(statut='rejected').count()
    
    # Métriques d'engagement (commentées car les champs n'existent pas dans le modèle)
    # total_views = Signalement.objects.aggregate(
    #     total=Count('views_count')
    # )['total']
    # total_shares = Signalement.objects.aggregate(
    #     total=Count('shares_count')
    # )['total']
    total_views = 0
    total_shares = 0
    
    return Response({
        'global': {
            'total': total_signalements,
            'approved': total_approved,
            'pending': total_pending,
            'rejected': total_rejected,
            'approval_rate': round(
                (total_approved / total_signalements * 100) if total_signalements > 0 else 0,
                2
            )
        },
        'by_category': list(stats_by_category),
        'by_zone': list(stats_by_zone),
        'engagement': {
            'total_views': total_views,
            'total_shares': total_shares
        }
    })
