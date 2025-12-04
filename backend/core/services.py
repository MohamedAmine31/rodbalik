"""
Services for email verification functionality.
"""

import random
import string
from datetime import timedelta
from django.utils import timezone
from django.core.mail import send_mail
from django.conf import settings
from .models import EmailVerificationCode, User


class EmailVerificationService:
    """
    Service pour gérer l'envoi et la vérification des codes d'email.
    """
    
    CODE_LENGTH = 6
    CODE_EXPIRATION_MINUTES = 10

    @staticmethod
    def generate_code() -> str:
        """
        Génère un code de vérification à 6 chiffres aléatoire.

        Returns:
            str: Le code de vérification à 6 chiffres (ex: "483920").
        """
        return ''.join(random.choices(string.digits, k=EmailVerificationService.CODE_LENGTH))

    @staticmethod
    def send_verification_code(email: str) -> tuple[bool, str]:
        """
        Génère et envoie un code de vérification à l'adresse e-mail spécifiée.

        Parameters:
            email (str): L'adresse e-mail de l'utilisateur à qui envoyer le code.

        Returns:
            tuple:
                - bool: Indique si l'envoi a réussi.
                - str: Message d'information ou d'erreur.
        """
        try:
            # Vérifier si l'utilisateur existe
            if not User.objects.filter(email=email).exists():
                return False, "L'adresse e-mail n'existe pas dans le système."
            
            # Supprimer les anciens codes non utilisés
            EmailVerificationCode.objects.filter(email=email, is_used=False).delete()
            
            # Générer un nouveau code et sa date d’expiration
            code = EmailVerificationService.generate_code()
            expires_at = timezone.now() + timedelta(minutes=EmailVerificationService.CODE_EXPIRATION_MINUTES)
            
            # Enregistrer le code dans la base de données
            EmailVerificationCode.objects.create(
                email=email,
                code=code,
                expires_at=expires_at
            )
            
            # Préparer le message à envoyer
            subject = "Votre code de vérification - Rod Balik"
            message = (
                f"Bonjour,\n\n"
                f"Votre code de vérification est : {code}\n"
                f"Ce code expirera dans 10 minutes.\n\n"
                f"Si vous n'avez pas demandé ce code, ignorez simplement ce message.\n\n"
                f"Cordialement,\nL'équipe Rod Balik"
            )
            
            # Envoi du mail
            send_mail(
                subject=subject,
                message=message,
                from_email=settings.DEFAULT_FROM_EMAIL,
                recipient_list=[email],
                fail_silently=False,
            )
            
            return True, "Un code de vérification a été envoyé à votre adresse e-mail."
        
        except Exception as e:
            print(f"[Erreur EmailVerificationService] {str(e)}")
            return False, "Une erreur est survenue lors de l'envoi du code de vérification."

    @staticmethod
    def verify_code(email: str, code: str) -> tuple[bool, str]:
        """
        Vérifie si le code fourni correspond au code le plus récent pour cet e-mail.

        Parameters:
            email (str): L'adresse e-mail utilisée pour l'envoi du code.
            code (str): Le code de vérification entré par l'utilisateur.

        Returns:
            tuple:
                - bool: True si le code est valide, sinon False.
                - str: Message expliquant le résultat (succès, erreur, expiration...).
        """
        try:
            verification_code = EmailVerificationCode.objects.filter(
                email=email,
                is_used=False
            ).latest('created_at')
        except EmailVerificationCode.DoesNotExist:
            return False, "Code incorrect ou expiré."
        
        # Vérification de l’expiration
        if timezone.now() > verification_code.expires_at:
            verification_code.delete()  # Nettoyage automatique
            return False, "Code expiré. Veuillez demander un nouveau code."
        
        # Vérification du code
        if verification_code.code != code:
            return False, "Code incorrect."
        
        # Marquer le code comme utilisé
        verification_code.is_used = True
        verification_code.save()
        
        return True, "Code vérifié avec succès."

    @staticmethod
    def cleanup_expired_codes() -> int:
        """
        Supprime les anciens codes expirés de la base de données.

        Returns:
            int: Le nombre de codes supprimés.
        """
        now = timezone.now()
        deleted_count, _ = EmailVerificationCode.objects.filter(expires_at__lt=now).delete()
        return deleted_count
