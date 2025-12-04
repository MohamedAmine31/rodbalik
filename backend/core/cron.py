"""
Cron jobs for the Rod Balik application.
"""

from django_cron import CronJobBase, Schedule
from .services import EmailVerificationService


class CleanupExpiredVerificationCodes(CronJobBase):
    """
    Cron job to automatically clean up expired email verification codes.
    Runs every 10 minutes.
    """
    RUN_EVERY_MINS = 10 

    schedule = Schedule(run_every_mins=RUN_EVERY_MINS)
    code = 'core.cleanup_expired_codes' 

    def do(self):
        """
        Execute the cleanup of expired verification codes.
        """
        deleted_count = EmailVerificationService.cleanup_expired_codes()
        print(f"[CRON] Cleaned up {deleted_count} expired verification codes")