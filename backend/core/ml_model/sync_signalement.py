import os
import sys
import django

# Ajouter la racine du projet dans le PYTHONPATH
sys.path.append(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))) )

# Configurer Django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rod_balik.settings')
django.setup()

from core.models import IASignalement, Signalement
from ultralytics import YOLO
from django.conf import settings

# Ton modèle YOLO
model = YOLO("C:/Users/rayen/runs/classify/train4/weights/best.pt")

signalements_ia = IASignalement.objects.filter(statut='EN_COURS')
for ia in signalements_ia:
    if not ia.photo:
        continue

    image_path = os.path.join(settings.MEDIA_ROOT, os.path.basename(ia.photo))
    results = model(image_path)
    result = results[0]

    probs = result.probs
    names = result.names
    predicted_class = names[probs.top1].lower()
    confidence = float(probs.top1conf)

    user_category = ia.category.lower()

    # Vérification selon IA
    if predicted_class != user_category or "unabled" in names and probs.data[names.index("unabled")] >= 0.01:
        ia.statut = 'EN_COURS'
        ia.save()
    elif confidence > 0.96:
        # Signalement validé : on met à jour le Signalement correspondant
        signalement = ia.signalement  # <-- lien correct
        signalement.statut = 'APPROUVE'
        signalement.save()
        print(f"Signalement {signalement.id} -> Statut mis à jour : APPROUVE")
        # Supprimer l'entrée IASignalement
        ia.delete()
