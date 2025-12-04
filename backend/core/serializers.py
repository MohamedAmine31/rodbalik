from rest_framework import serializers
from django.contrib.auth import get_user_model
from django.db.models import Q
from .models import Category, Signalement, Statistique, Media
import re
from decimal import Decimal
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer

User = get_user_model()
from rest_framework import serializers



class UserIdSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id']
        read_only_fields = ['id']



class UserSerializer(serializers.ModelSerializer):
    """Serializer for User model."""

    class Meta:
        model = User
        fields = [
            'username', 'email', 'first_name', 'last_name',
            'role', 'phone', 'created_at'
        ]
        read_only_fields = ['created_at']

class UserRegistrationSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True, min_length=8)
    phone = serializers.CharField(write_only=True)

    class Meta:
        model = User
        fields = ['username', 'email', 'password', 'first_name', 'last_name', 'phone']
        extra_kwargs = {
            'id': {'read_only': True}
        }

    def validate(self, data):
        errors = []

        username = data.get('username', '').strip().lower()
        if len(username) < 3:
            errors.append("username doit contenir au moins 3 caractères.")
        if User.objects.filter(username__iexact=username).exists():
            errors.append("username est déjà pris.")

        email = data.get('email', '').strip().lower()
        if User.objects.filter(email__iexact=email).exists():
            errors.append("Un compte avec cet email existe déjà.")

        # Password
        password = data.get('password', '')

        if len(password) < 8:
            errors.append("Le mot de passe doit contenir au moins 8 caractères.")
      
        if not any(char.isdigit() for char in password):
             errors.append("Le mot de passe doit contenir au moins un chiffre.")

   
        # Phone
        phone = data.get('phone', '')
        if not re.fullmatch(r'\d{8}', phone):
            errors.append("Le numéro de téléphone doit contenir exactement 8 chiffres.")

        if errors:
            raise serializers.ValidationError({"errors": errors})

        return data

    def create(self, validated_data):
        return User.objects.create_user(**validated_data)


class UsernameAvailabilitySerializer(serializers.Serializer):
    """
    Serializer for username availability check.
    Validates username and returns availability status with suggestions.
    """
    username = serializers.CharField(min_length=3, max_length=150)
    id = serializers.IntegerField(read_only=True)
    
    def validate_username(self, value):
        """
        Validate that username contains only alphanumeric characters and underscores.
        Converts to lowercase for consistency.
        """
        value = value.strip().lower()
        
        if not re.match(r'^[a-z0-9_]+$', value):
            raise serializers.ValidationError(
                'Le nom d\'utilisateur doit contenir uniquement des lettres, chiffres et underscores.'
            )
        
        if value.startswith('_') or value.endswith('_'):
            raise serializers.ValidationError(
                'Le nom d\'utilisateur ne doit pas commencer ou finir par un underscore.'
            )
        
        if '__' in value:
            raise serializers.ValidationError(
                'Le nom d\'utilisateur ne doit pas contenir d\'underscores consécutifs.'
            )
        
        return value


class MediaSerializer(serializers.ModelSerializer):
    """Serializer for Media model."""

    class Meta:
        model = Media
        fields = ['id', 'NomMedia']


class UserSimpleSerializer(serializers.ModelSerializer):
    """Simple serializer for User model with basic info."""

    class Meta:
        model = User
        fields = ['id', 'username', 'email']


class CategorySimpleSerializer(serializers.ModelSerializer):
    """Simple serializer for Category model."""

    class Meta:
        model = Category
        fields = ['id', 'name']


class CategorySerializer(serializers.ModelSerializer):
    """Serializer for Category model."""
    signalements_count = serializers.IntegerField(read_only=True)

    class Meta:
        model = Category
        fields = [
            'id', 'name', 'description', 'icon', 'color',
            'is_active', 'signalements_count', 'created_at'
        ]
        read_only_fields = ['id', 'created_at']


class SignalementListSerializer(serializers.ModelSerializer):
    """Lightweight serializer for listing signalements."""
    category_name = serializers.CharField(source='category.name', read_only=True)
    category_color = serializers.CharField(source='category.color', read_only=True)
    author_name = serializers.CharField(source='author.username', read_only=True)

    class Meta:
        model = Signalement
        fields = [
            'description', 'category_name', 'category_color',
            'latitude', 'longitude', 'photo', 'statut',
            'author_name', 'date'
        ]


class SignalementListWithIdSerializer(serializers.ModelSerializer):
    """Serializer for listing signalements with ID."""
    category_name = serializers.CharField(source='category.name', read_only=True)
    category_color = serializers.CharField(source='category.color', read_only=True)
    author_name = serializers.CharField(source='author.username', read_only=True)

    class Meta:
        model = Signalement
        fields = [
            'id', 'description', 'category_name', 'category_color',
            'latitude', 'longitude', 'photo', 'statut',
            'author_name', 'date'
        ]


class SignalementDetailSerializer(serializers.ModelSerializer):
    """Detailed serializer for individual signalement."""
    citoyen = UserSimpleSerializer(source='author', read_only=True)
    category = CategorySimpleSerializer(read_only=True)
    media = MediaSerializer(source='medias', many=True, read_only=True)

    class Meta:
        model = Signalement
        fields = [
            'id', 'description', 'photo', 'latitude', 'longitude',
            'statut', 'date', 'citoyen', 'category', 'media'
        ]
        read_only_fields = ['id', 'date']

class SignalementIASerializer(serializers.ModelSerializer):
    category = serializers.CharField(source='category.name')

    class Meta:
        model = Signalement
        fields = ['id', 'photo', 'category']






class SignalementCreateSerializer(serializers.ModelSerializer):
    """Serializer for creating signalements with file upload."""

    Description = serializers.CharField(source='description')
    Categorie = serializers.SlugRelatedField(
        queryset=Category.objects.filter(is_active=True),
        slug_field='name',
        source='category'
    )
    Latitude = serializers.FloatField(source='latitude', required=True)
    Longitude = serializers.FloatField(source='longitude', required=True)
    Photo = serializers.ImageField(source='photo', required=False, allow_null=True)

    class Meta:
        model = Signalement
        fields = ['Description', 'Categorie', 'Latitude', 'Longitude', 'Photo']

    def validate_Categorie(self, value):
        if not value.is_active:
            raise serializers.ValidationError("Cette catégorie n'est pas active.")
        return value

    def validate(self, data):
        return data

class SignalementModerationSerializer(serializers.ModelSerializer):
    """Serializer for moderating signalements."""

    class Meta:
        model = Signalement
        fields = ['statut', 'moderation_comment']
        extra_kwargs = {
            'id': {'read_only': True}
        }
    
    def validate_statut(self, value):
        if value != 'approved':
            raise serializers.ValidationError(
                'Le statut doit être "approved".'
            )
        return value


class StatistiqueSerializer(serializers.ModelSerializer):
    """Serializer for Statistics model."""

    class Meta:
        model = Statistique
        fields = [
            'date', 'total_signalements', 'pending_signalements',
            'approved_signalements', 'rejected_signalements',
            'new_users', 'active_users', 'category_breakdown',
            'created_at', 'updated_at'
        ]
        read_only_fields = ['created_at', 'updated_at']
class SendCodeSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True, help_text="L'adresse e-mail de l'utilisateur à laquelle le code doit être envoyé.")
    id = serializers.IntegerField(read_only=True)


class VerifyCodeSerializer(serializers.Serializer):
    email = serializers.EmailField(required=True, help_text="L'adresse e-mail de l'utilisateur à vérifier.")
    code = serializers.CharField(required=True, help_text="Le code de vérification reçu par e-mail.")
    id = serializers.IntegerField(read_only=True)


class ChangePasswordSerializer(serializers.Serializer):
    email = serializers.EmailField()
    new_password = serializers.CharField(write_only=True, min_length=8)
    confirm_password = serializers.CharField(write_only=True)
    id = serializers.IntegerField(read_only=True)

    def validate(self, data):
        if data['new_password'] != data['confirm_password']:
            raise serializers.ValidationError("Les mots de passe ne correspondent pas.")

        try:
            user = User.objects.get(email=data['email'])
        except User.DoesNotExist:
            raise serializers.ValidationError("Aucun utilisateur trouvé avec cet email.")

        if user.check_password(data['new_password']):
            raise serializers.ValidationError("Le nouveau mot de passe ne peut pas être identique à l'ancien.")

        data['user'] = user
        return data


class CustomTokenObtainPairSerializer(TokenObtainPairSerializer):
    """
    Serializer personnalisé pour l'obtention de tokens JWT.
    Permet l'authentification avec username ou email.
    """

    def validate(self, attrs):
        # Récupérer les données d'entrée
        username_or_email = attrs.get('username')
        password = attrs.get('password')

        if not username_or_email or not password:
            raise serializers.ValidationError('Username/email et mot de passe sont requis.')

        # Trouver l'utilisateur par username ou email
        user = None
        try:
            # Essayer d'abord comme username
            user = User.objects.get(Q(username__iexact=username_or_email) | Q(email__iexact=username_or_email))
        except User.DoesNotExist:
            raise serializers.ValidationError('Identifiants invalides.')

        # Vérifier le mot de passe
        if not user.check_password(password):
            raise serializers.ValidationError('Identifiants invalides.')

        # Vérifier si l'utilisateur est actif
        if not user.is_active:
            raise serializers.ValidationError('Ce compte est désactivé.')

        # Générer les tokens
        refresh = self.get_token(user)

        # Retourner les données avec user_id ajouté (maintenu pour compatibilité)
        data = {
            'refresh': str(refresh),
            'access': str(refresh.access_token),
            'user_id': user.id,
        }

        return data


class SignalementModerationSerializer(serializers.Serializer):
    """
    Serializer for moderating signalements (approve/reject).
    """
    statut = serializers.ChoiceField(
        choices=[('approved', 'Approuvé'), ('rejected', 'Rejeté')],
        required=True
    )
    moderation_comment = serializers.CharField(
        required=False,
        allow_blank=True,
        max_length=500
    )
