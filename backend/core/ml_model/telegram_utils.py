# core/telegram_utils.py
import os
import requests
from django.conf import settings

BOT_TOKEN = os.environ.get('TELEGRAM_BOT_TOKEN', "8233311490:AAFYR7WjaNrGsv-_avUsvesis7USp-fPIRU")
CHAT_ID = os.environ.get('TELEGRAM_CHAT_ID', "@rodbalik")

def send_signalement_to_telegram(message, photo=None):
    if not BOT_TOKEN or not CHAT_ID:
        print("Erreur: BOT_TOKEN ou CHAT_ID non configuré.")
        return False

    try:
        if photo and str(photo).strip():
            url = f"https://api.telegram.org/bot{BOT_TOKEN}/sendPhoto"
            photo_str = str(photo).strip()

            if not photo_str.startswith('http'):
                # Essayer comme chemin local
                full_path = os.path.join(settings.MEDIA_ROOT, os.path.basename(photo_str))
                if os.path.isfile(full_path):
                    with open(full_path, 'rb') as f:
                        files = {'photo': f}
                        data = {'chat_id': CHAT_ID, 'caption': message}
                        response = requests.post(url, files=files, data=data, timeout=10)
                        return response.status_code == 200
                print(f"Photo non trouvée: {photo_str}")
                return False
            else:
                # URL publique
                payload = {"chat_id": CHAT_ID, "photo": photo_str, "caption": message}
                response = requests.post(url, data=payload, timeout=10)
                return response.status_code == 200
        else:
            # Message texte seulement
            url = f"https://api.telegram.org/bot{BOT_TOKEN}/sendMessage"
            payload = {"chat_id": CHAT_ID, "text": message}
            response = requests.post(url, data=payload, timeout=10)
            return response.status_code == 200
    except Exception as e:
        print(f"Erreur lors de l'envoi Telegram: {e}")
        return False
