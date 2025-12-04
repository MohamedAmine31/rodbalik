"""
Management command to create admin users for the Rod Balik project.
"""

from django.core.management.base import BaseCommand, CommandError
from django.contrib.auth import get_user_model
from django.db import transaction

User = get_user_model()


class Command(BaseCommand):
    help = 'Create an admin user with role="admin"'

    def add_arguments(self, parser):
        parser.add_argument(
            '--username',
            type=str,
            help='Username for the admin user',
            required=True
        )
        parser.add_argument(
            '--email',
            type=str,
            help='Email for the admin user',
            required=True
        )
        parser.add_argument(
            '--password',
            type=str,
            help='Password for the admin user',
            required=True
        )
        parser.add_argument(
            '--first-name',
            type=str,
            help='First name for the admin user',
            default=''
        )
        parser.add_argument(
            '--last-name',
            type=str,
            help='Last name for the admin user',
            default=''
        )
        parser.add_argument(
            '--superuser',
            action='store_true',
            help='Also make this user a Django superuser',
            default=False
        )

    def handle(self, *args, **options):
        username = options['username']
        email = options['email']
        password = options['password']
        first_name = options['first_name']
        last_name = options['last_name']
        make_superuser = options['superuser']

        # Check if user already exists
        if User.objects.filter(username=username).exists():
            raise CommandError(f'User with username "{username}" already exists.')

        if User.objects.filter(email=email).exists():
            raise CommandError(f'User with email "{email}" already exists.')

        try:
            with transaction.atomic():
                # Create the admin user
                user = User.objects.create_user(
                    username=username,
                    email=email,
                    password=password,
                    first_name=first_name,
                    last_name=last_name,
                    role='admin'
                )

                if make_superuser:
                    user.is_staff = True
                    user.is_superuser = True
                    user.save()

                self.stdout.write(
                    self.style.SUCCESS(
                        f'Successfully created admin user "{username}" with role="admin"'
                    )
                )

                if make_superuser:
                    self.stdout.write(
                        self.style.SUCCESS(
                            f'User "{username}" is also a Django superuser'
                        )
                    )

        except Exception as e:
            raise CommandError(f'Failed to create admin user: {str(e)}')