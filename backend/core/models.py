from django.db import models
from django.contrib.auth.models import AbstractUser
from django.core.validators import MinValueValidator, MaxValueValidator


class User(AbstractUser):
    """
    Custom User model extending Django's AbstractUser.
    Supports both citizens and administrators.
    """
    ROLE_CHOICES = [
        ('citizen', 'Citoyen'),
        ('admin', 'Administrateur'),
    ]
    
    role = models.CharField(
        max_length=10,
        choices=ROLE_CHOICES,
        default='citizen',
        verbose_name='Rôle'
    )
    phone = models.CharField(
        max_length=20,
        blank=True,
        null=True,
        verbose_name='Téléphone'
    )
    created_at = models.DateTimeField(auto_now_add=True, verbose_name='Date de création')
    updated_at = models.DateTimeField(auto_now=True, verbose_name='Date de modification')
    
    class Meta:
        verbose_name = 'Utilisateur'
        verbose_name_plural = 'Utilisateurs'
        ordering = ['-created_at']
    
    def __str__(self):
        return f"{self.username} ({self.get_role_display()})"


class Category(models.Model):
    """
    Category model for classifying signalements.
    Examples: Dépôt sauvage, Vandalisme, Éclairage public, etc.
    """
    name = models.CharField(
        max_length=100,
        unique=True,
        verbose_name='Nom'
    )
    description = models.TextField(
        blank=True,
        verbose_name='Description'
    )
    icon = models.CharField(
        max_length=50,
        blank=True,
        help_text='Nom de l\'icône (ex: trash, lightbulb, etc.)',
        verbose_name='Icône'
    )
    color = models.CharField(
        max_length=7,
        default='#3B82F6',
        help_text='Couleur hexadécimale (ex: #3B82F6)',
        verbose_name='Couleur'
    )
    is_active = models.BooleanField(
        default=True,
        verbose_name='Actif'
    )
    created_at = models.DateTimeField(auto_now_add=True, verbose_name='Date de création')
    
    class Meta:
        verbose_name = 'Catégorie'
        verbose_name_plural = 'Catégories'
        ordering = ['name']
    
    def __str__(self):
        return self.name


class Media(models.Model):
    """
    Media model for storing additional media files related to signalements.
    """
    signalement = models.ForeignKey(
        'Signalement',
        on_delete=models.CASCADE,
        related_name='medias',
        verbose_name='Signalement'
    )
    NomMedia = models.CharField(
        max_length=255,
        verbose_name='Nom du média'
    )
    created_at = models.DateTimeField(auto_now_add=True, verbose_name='Date de création')

    class Meta:
        verbose_name = 'Média'
        verbose_name_plural = 'Médias'
        ordering = ['created_at']

    def __str__(self):
        return f"{self.NomMedia} - {self.signalement}"


class Signalement(models.Model):
    """
    Main Signalement model for civic reports.
    Simplified model matching the diagram specification.

    Attributes (as per diagram):
    - id: Identifiant unique (clé primaire)
    - description: Description détaillée du signalement
    - media: Fichier média (photo )
    - localisation: Adresse ou coordonnées du lieu
    - statut: État du signalement (En attente, Approuvé, Rejeté)
    - date: Date de création du signalement
    """
    STATUS_CHOICES = [
        ('EN_COURS', 'En cours'),
        ('APPROUVE', 'Approuvé'),
        ('REJETE', 'Rejeté'),
    ]

    description = models.TextField(
        verbose_name='Description',
        help_text='Description détaillée du problème signalé'
    )

    photo = models.ImageField(
    upload_to='',
    blank=True,
    null=True,
    verbose_name='photo'
    )



    latitude = models.FloatField(
        null=True,
        blank=True,
        verbose_name='Latitude'
    )
    longitude = models.FloatField(
        null=True,
        blank=True,
        verbose_name='Longitude'
    )
    statut = models.CharField(
        max_length=10,
        choices=STATUS_CHOICES,
        default='EN_COURS',
        verbose_name='Statut'
    )

    date = models.DateField(
        auto_now_add=True,
        verbose_name='Date'
    )
    
    category = models.ForeignKey(
        Category,
        on_delete=models.PROTECT,
        related_name='signalements',
        verbose_name='Categorie'
    )
    
    author = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='PhotoSignalements',
        verbose_name='Auteur',
        default=1
    )
    
    moderator = models.ForeignKey(
        User,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='moderated_signalements',
        verbose_name='Modérateur'
    )
    
    moderation_comment = models.TextField(
        blank=True,
        verbose_name='Commentaire de modération'
    )
    
    moderated_at = models.DateTimeField(
        null=True,
        blank=True,
        verbose_name='Date de modération'
    )
    
    class Meta:
        verbose_name = 'Signalement'
        verbose_name_plural = 'Signalements'
        ordering = ['-date']
        indexes = [
            models.Index(fields=['-date']),
            models.Index(fields=['statut']),
            models.Index(fields=['category']),
        ]
    
    def __str__(self):
        return f"{self.description[:50]} - {self.get_statut_display()}"
    def process_iasignalement(ia_instance):
        from core.ml_model.sync_signalement_utils import validate_iasignalement
        validate_iasignalement(ia_instance)


class Statistique(models.Model):
    """
    Statistics model for tracking system metrics.
    Aggregates data for dashboard and reporting.
    """
    date = models.DateField(
        unique=True,
        verbose_name='Date'
    )
    
    # Signalement statistics
    total_signalements = models.PositiveIntegerField(
        default=0,
        verbose_name='Total signalements'
    )
    pending_signalements = models.PositiveIntegerField(
        default=0,
        verbose_name='Signalements en attente'
    )
    approved_signalements = models.PositiveIntegerField(
        default=0,
        verbose_name='Signalements approuvés'
    )
    rejected_signalements = models.PositiveIntegerField(
        default=0,
        verbose_name='Signalements rejetés'
    )
    
    # User statistics
    new_users = models.PositiveIntegerField(
        default=0,
        verbose_name='Nouveaux utilisateurs'
    )
    active_users = models.PositiveIntegerField(
        default=0,
        verbose_name='Utilisateurs actifs'
    )
    
    # Category breakdown (JSON field for flexibility)
    category_breakdown = models.JSONField(
        default=dict,
        blank=True,
        verbose_name='Répartition par catégorie'
    )
    
    # Timestamps
    created_at = models.DateTimeField(auto_now_add=True, verbose_name='Date de création')
    updated_at = models.DateTimeField(auto_now=True, verbose_name='Date de modification')
    
    class Meta:
        verbose_name = 'Statistique'
        verbose_name_plural = 'Statistiques'
        ordering = ['-date']
    
    def __str__(self):
        return f"Statistiques du {self.date}"


class EmailVerificationCode(models.Model):
    """
    Model for storing temporary email verification codes.
    These codes are used for email verification during registration or password reset.
    """
    email = models.EmailField(verbose_name='Email')
    code = models.CharField(
        max_length=6,
        verbose_name='Code de vérification'
    )
    is_used = models.BooleanField(
        default=False,
        verbose_name='Utilisé'
    )
    created_at = models.DateTimeField(
        auto_now_add=True,
        verbose_name='Date de création'
    )
    expires_at = models.DateTimeField(
        verbose_name='Date d\'expiration'
    )
    
    class Meta:
        verbose_name = 'Code de vérification email'
        verbose_name_plural = 'Codes de vérification email'
        ordering = ['-created_at']
        indexes = [
            models.Index(fields=['email', 'code']),
            models.Index(fields=['expires_at']),
        ]
    
    def __str__(self):
        return f"{self.email} - {'✓ Utilisé' if self.is_used else '⏳ En attente'}"
    
    @property
    def is_expired(self):
        """Check if the verification code has expired."""
        from django.utils import timezone
        return timezone.now() > self.expires_at
    
    @property
    def is_valid(self):
        """Check if the code is still valid (not expired and not used)."""
        return not self.is_expired and not self.is_used
class PasswordResetCode(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    code = models.CharField(max_length=6)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.user.username} - {self.code}"
    
class IASignalement(models.Model):
    signalement = models.ForeignKey(Signalement, on_delete=models.CASCADE)
    photo = models.CharField(max_length=500, null=True, blank=True)
    category = models.CharField(max_length=100)
    statut = models.CharField(max_length=20)
    precision = models.FloatField(default=0.0)
    synced_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        verbose_name = 'IASignalement'
        verbose_name_plural = 'IASignalements'
        ordering = ['-synced_at']  # Tri par date de création décroissante
