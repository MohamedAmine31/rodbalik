"""
Permissions personnalisées pour le projet Rod Balik
"""

from rest_framework import permissions


class IsAdminUser(permissions.BasePermission):
    """
    Permission pour vérifier que l'utilisateur est un administrateur
    """
    def has_permission(self, request, view):
        return (
            request.user and
            request.user.is_authenticated and
            (request.user.role == 'admin' or request.user.is_superuser)
        )


class IsOwnerOrAdmin(permissions.BasePermission):
    """
    Permission pour vérifier que l'utilisateur est le propriétaire ou un admin
    """
    def has_object_permission(self, request, view, obj):
        # Vérifier que l'utilisateur est authentifié
        if not request.user or not request.user.is_authenticated:
            return False

        # Les admins ont tous les droits
        if request.user.role == 'admin':
            return True

        # Le propriétaire peut modifier son propre signalement
        return obj.author == request.user
