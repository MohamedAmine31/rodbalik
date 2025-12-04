"""
Configuration des URLs pour l'application Rod Balik
Tous les endpoints API du projet
"""

from django.urls import path
from rest_framework_simplejwt.views import TokenObtainPairView
from . import views
from .views import (
    UserRegistrationView,
    UserProfileView,
    CurrentUserView,
    UsernameAvailabilityView,
    CustomTokenObtainPairView,
    UserListView,
    CategoryListCreateView,
    CategoryDetailView,
    SignalementListView,
    SignalementListWithIdView,
    SignalementCreateView,
    SignalementDetailView,
    SignalementUpdateView,
    SignalementDeleteByIdView,
    SignalementRejectView,
    SignalementValidationView,
    SignalementRetrieveView,
    StatistiqueListView,
    statistiques_dashboard,
    send_verification_code,
    verify_code,
    change_password,
)
from .views.PasswordResetCode import PasswordResetRequestView


app_name = 'signalements'

urlpatterns = [
    # ==================== AUTHENTIFICATION ====================
    path('auth/register/', UserRegistrationView.as_view(), name='register'),
    path('auth/profile/', UserProfileView.as_view(), name='profile'),
    path('users/', UserListView.as_view(), name='user-list'),
    path('users/me/', CurrentUserView.as_view(), name='current-user'),
    path('users/suggest-username/', UsernameAvailabilityView.as_view(), name='suggest-username'),

    path('email/send-code/', send_verification_code, name='send-code'),
    path('email/verify-code/', verify_code, name='verify-code'),
    path('email/change-password/', change_password, name='change-password'),
    
    # ==================== CATÉGORIES ====================
    path('categories/', CategoryListCreateView.as_view(), name='category-list'),
    path('categories/<int:pk>/', CategoryDetailView.as_view(), name='category-detail'),
    
    # ==================== SIGNALEMENTS ====================
    # Soumission de signalement
    path('signalement/soumettre/', SignalementCreateView.as_view(), name='signalement-create'),

    # Liste et détails (utilisateur connecté uniquement)
    path('signalements/', SignalementListView.as_view(), name='signalement-list'),
    path('signalements/with-id/', SignalementListWithIdView.as_view(), name='signalement-list-with-id'),
    path('signalements/me/', SignalementDetailView.as_view(), name='signalement-detail-me'),
    path('signalement/<int:pk>/', SignalementRetrieveView.as_view(), name='signalement-detail'),
    path('signalements/update/', SignalementUpdateView.as_view(), name='signalement-update'),



    # ==================== ADMINISTRATION ====================
    # Validation/Rejet
    path('signalement/valider/<int:id>/', SignalementValidationView.as_view(), name='signalement-validate'),
    path('signalement/rejeter/<int:id>/', SignalementRejectView.as_view(), name='signalement-reject'),

    # Suppression
    path('signalement/supprimer/<int:id>/', SignalementDeleteByIdView.as_view(), name='signalement-delete-by-id'),


        
 

]
