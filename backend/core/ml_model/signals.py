# core/models.py
from django.db.models.signals import post_save
from django.dispatch import receiver
from ..models import IASignalement
from .sync_signalement_utils import validate_iasignalement

@receiver(post_save, sender=IASignalement)
def iasignalement_post_save(sender, instance, created, **kwargs):
    if created:  # ne traiter que les nouveaux
        print(f"[DEBUG] Nouveau IASignalement créé: {instance.id}")
        validate_iasignalement(instance)

