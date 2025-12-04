from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from django.utils.html import format_html
from .models import User, Category, Signalement, EmailVerificationCode
from .models import IASignalement


@admin.register(User)
class UserAdmin(BaseUserAdmin):
    """Admin interface for User model."""
    list_display = ['username', 'email', 'role', 'is_staff', 'created_at']
    list_filter = ['role', 'is_staff', 'is_active', 'created_at']
    search_fields = ['username', 'email', 'first_name', 'last_name']
    
    fieldsets = BaseUserAdmin.fieldsets + (
        ('Informations supplémentaires', {
            'fields': ('role', 'phone')
        }),
    )


@admin.register(Category)
class CategoryAdmin(admin.ModelAdmin):
    """Admin interface for Category model."""
    list_display = ['name', 'icon', 'color', 'is_active', 'created_at']
    list_filter = ['is_active', 'created_at']
    search_fields = ['name', 'description']
    prepopulated_fields = {}


@admin.register(Signalement)
class SignalementAdmin(admin.ModelAdmin):
    """Admin interface for Signalement model."""
    list_display = [
        'id', 'description', 'category', 'statut',
        'author', 'moderator', 'photo_preview', 'date'
    ]
    list_filter = [
        'statut', 'category',
        'date', 'moderated_at'
    ]
    search_fields = ['description']
    readonly_fields = [
        'date', 'photo_display'
    ]

    fieldsets = (
        ('Informations de base', {
            'fields': ('description', 'category')
        }),
        ('Photo', {
            'fields': ('photo', 'photo_display')
        }),
        ('Localisation', {
            'fields': ('latitude', 'longitude')
        }),
        ('Statut', {
            'fields': ('statut', 'author')
        }),
        ('Modération', {
            'fields': ('moderator', 'moderation_comment', 'moderated_at')
        }),
        ('Dates', {
            'fields': ('date',),
            'classes': ('collapse',)
        }),
    )

    def photo_preview(self, obj):
        """Display photo thumbnail in list view"""
        if obj.photo:
            return format_html(
                '<img src="{}" width="50" height="50" style="object-fit: cover; border-radius: 4px;" />',
                obj.photo
            )
        return '—'
    photo_preview.short_description = 'Photo'

    def photo_display(self, obj):
        """Display full photo in detail view"""
        if obj.photo:
            return format_html(
                '<img src="{}" style="max-width: 400px; max-height: 400px; border-radius: 4px;" />',
                obj.photo
            )
        return 'Aucune photo'
    photo_display.short_description = 'Aperçu de la photo'





@admin.register(EmailVerificationCode)
class EmailVerificationCodeAdmin(admin.ModelAdmin):
    """Admin interface for EmailVerificationCode model."""
    list_display = ['email', 'code', 'is_used', 'is_valid', 'created_at', 'expires_at']
    list_filter = ['is_used', 'created_at', 'expires_at']
    search_fields = ['email', 'code']
    readonly_fields = ['created_at', 'is_expired', 'is_valid']
    
    fieldsets = (
        ('Code Information', {
            'fields': ('email', 'code', 'is_used')
        }),
        ('Expiration', {
            'fields': ('created_at', 'expires_at', 'is_expired', 'is_valid'),
            'classes': ('collapse',)
        }),
    )
    
    def has_add_permission(self, request):
        """Prevent manual creation of verification codes from admin."""
        return False
@admin.register(IASignalement)
class IASignalementAdmin(admin.ModelAdmin):
    list_display = ('signalement', 'photo', 'category', 'statut', 'precision', 'synced_at', 'updated_at')
    list_filter = ('statut', 'category', 'synced_at')
    search_fields = ('category', 'photo', 'statut')
    readonly_fields = ('synced_at', 'updated_at')