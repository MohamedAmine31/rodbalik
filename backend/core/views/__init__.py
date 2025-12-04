"""
Package de vues pour l'application Rod Balik
Importe toutes les vues depuis les modules spécialisés
"""

from .authentication import UserRegistrationView, UserProfileView, CurrentUserView, UsernameAvailabilityView, CustomTokenObtainPairView, GetTokenUserView, UserListView
from .categories import CategoryListCreateView, CategoryDetailView
from .signalements import (
    SignalementListView,
    SignalementListWithIdView,
    SignalementCreateView,
    SignalementDetailView,
    SignalementUpdateView,
    SignalementDeleteByIdView,
    SignalementRejectView,
    SignalementValidationView,
    SignalementRetrieveView,
)
from .statistics import (
    StatistiqueListView,
    statistiques_dashboard,
)
from .email_verification import send_verification_code, verify_code, change_password

__all__ = [
    # Authentication
    'UserRegistrationView',
    'UserProfileView',
     'CurrentUserView',
    'UsernameAvailabilityView',
    'CustomTokenObtainPairView',
    'GetTokenUserView',
    'UserListView',
    # Categories
    'CategoryListCreateView',
    # Signalements
    'SignalementListView',
    'SignalementListWithIdView',
    'SignalementCreateView',
    'SignalementDetailView',
    'SignalementUpdateView',
    'SignalementDeleteByIdView',
    'SignalementRejectView',
    'SignalementValidationView',
    'SignalementRetrieveView',
    # Statistics
    'StatistiqueListView',
    'statistiques_dashboard',
    # Email verification
    'send_verification_code',
    'verify_code',
    'change_password',
]
