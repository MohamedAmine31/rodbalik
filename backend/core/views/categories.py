"""
Vues de gestion des catégories
Endpoints: /api/categories/, /api/categories/<id>/, /api/categories/<id>/toggle_active/
"""

from rest_framework import generics, permissions, status
from rest_framework.decorators import action
from rest_framework.response import Response
from ..models import Category
from ..serializers import CategorySerializer
from ..permissions import IsAdminUser


class CategoryListCreateView(generics.ListCreateAPIView):
    """
    API de liste et création de catégories
    
    GET /api/categories/ - Liste des catégories actives (public)
    POST /api/categories/ - Créer une nouvelle catégorie (admin uniquement)
    
    Filtres disponibles:
    - is_active: Afficher uniquement les catégories actives
    """
    queryset = Category.objects.filter(is_active=True)
    serializer_class = CategorySerializer
    
    def get_permissions(self):
        """
        POST réservé aux administrateurs
        GET accessible à tous
        """
        if self.request.method == 'POST':
            return [IsAdminUser()]
        return [permissions.AllowAny()]


class CategoryDetailView(generics.RetrieveUpdateDestroyAPIView):
    """
    API de détail, mise à jour et suppression de catégories

    GET /api/categories/<id>/ - Détails d'une catégorie (admin uniquement)
    PUT /api/categories/<id>/ - Modifier une catégorie (admin uniquement)
    PATCH /api/categories/<id>/ - Modifier partiellement une catégorie (admin uniquement)
    DELETE /api/categories/<id>/ - Supprimer une catégorie (admin uniquement)
    """
    queryset = Category.objects.all()
    serializer_class = CategorySerializer
    permission_classes = [IsAdminUser]
