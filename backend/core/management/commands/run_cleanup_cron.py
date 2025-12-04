"""
Management command to run the cleanup cron job manually.
Useful for development and testing.
Run with: python manage.py run_cleanup_cron
"""

from django.core.management.base import BaseCommand
from django_cron.management.commands.runcrons import Command as CronCommand


class Command(BaseCommand):
    help = 'Run the cleanup cron job manually for development/testing'

    def handle(self, *args, **options):
        self.stdout.write('Running cleanup cron job...')

        # Import and run the specific cron job
        from core.cron import CleanupExpiredVerificationCodes

        cron_job = CleanupExpiredVerificationCodes()
        cron_job.do()

        self.stdout.write(
            self.style.SUCCESS('Cleanup cron job completed successfully')
        )