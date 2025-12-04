
from core.models import IASignalement, Signalement
from django.utils import timezone
from django.conf import settings
from ultralytics import YOLO
import os
from .telegram_utils import send_signalement_to_telegram

# Charger le modèle YOLO une seule fois
model = YOLO(r"C:\Users\user\Desktop\Pi\rod-balik-backend (2)\core\ml_model\best.pt")

def validate_iasignalement(ia_instance):
    """
    Valide automatiquement un IASignalement et envoie sur Telegram
    """
    if not ia_instance.photo:
        print(f"⚠ IASignalement {ia_instance.id} n'a pas de photo")
        return

    signalement = ia_instance.signalement

    # Prédiction IA
    image_path = os.path.join(settings.MEDIA_ROOT, os.path.basename(ia_instance.photo))
    results = model(image_path)
    result = results[0]

    probs = result.probs
    names = result.names
    predicted_class = names[probs.top1].lower()
    confidence = float(probs.top1conf)
    user_category = ia_instance.category.lower()

    unabled_index = names.index("unabled") if "unabled" in names else None
    unabled_prob = float(probs.data[unabled_index]) if unabled_index is not None else 0.0

    if predicted_class != user_category or unabled_prob >= 0.01:
        ia_instance.statut = 'EN_COURS'
        ia_instance.save()
        print(f"⚠ IASignalement {ia_instance.id} reste EN_COURS")
    elif confidence > 0.96:
        # Valider le Signalement
        signalement.statut = 'APPROUVE'
        signalement.moderated_at = timezone.now()
        signalement.save()

        # Supprimer IASignalement
        ia_instance.delete()

        # Préparer message Telegram
        telegram_message = (
            f"⚠ Nouveau signalement approuvé :\n\n"
            f"Description: {signalement.description}\n\n"
            f"Catégorie: {signalement.category.name}\n\n"
            f"Auteur: {"anonyme"}\n\n"
            f"Date: {signalement.date}"
        )
        photo_to_send = signalement.photo if signalement.photo else None
        send_signalement_to_telegram(telegram_message, photo_to_send)

        print(f"✅ Signalement {signalement.id} validé et envoyé sur Telegram")
 
 
 