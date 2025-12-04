"""
Management command to clean up expired email verification codes.
Run with: python manage.py cleanup_expired_codes
"""

from django.core.management.base import BaseCommand
from signalements.services import EmailVerificationService


class Command(BaseCommand):
    help = 'Delete expired email verification codes from the database'

    def handle(self, *args, **options):
        deleted_count = EmailVerificationService.cleanup_expired_codes()
        
        self.stdout.write(
            self.style.SUCCESS(
                f'Successfully deleted {deleted_count} expired verification codes'
            )
        )
