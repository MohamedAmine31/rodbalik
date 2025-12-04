import os
import django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'rod_balik.settings')
django.setup()
from core.models import Signalement, User
print('Users:', User.objects.count())
print('Signalements:', Signalement.objects.count())
if Signalement.objects.exists():
    print('First signalement ID:', Signalement.objects.first().id)
    signalement = Signalement.objects.get(id=7)
    print('Signalement 7 author:', signalement.author.username, 'role:', signalement.author.role)
else:
    print('No signalements found')

print('Users:')
for user in User.objects.all():
    print(f'  {user.username} ({user.role}) - ID: {user.id}')