from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, serializers
from django.core.mail import send_mail
from django.contrib.auth import get_user_model
from django.utils.crypto import get_random_string
from ..models import PasswordResetCode
from drf_yasg.utils import swagger_auto_schema
from django.conf import Settings
from rest_framework.permissions import AllowAny
User = get_user_model()


class PasswordResetRequestSerializer(serializers.Serializer):
    email = serializers.EmailField()


class PasswordResetRequestView(APIView):
    """
    Cette vue permet d'envoyer un code aléatoire à l'email de l'utilisateur
    si celui-ci existe dans la base de données.
    """
    permission_classes = [AllowAny]
    @swagger_auto_schema(request_body=PasswordResetRequestSerializer)
    def post(self, request):
        serializer = PasswordResetRequestSerializer(data=request.data)
        if serializer.is_valid():
            email = serializer.validated_data['email']

            try:
                user = User.objects.get(email=email)
            except User.DoesNotExist:
                return Response({'error': 'Cet email n’existe pas.'}, status=status.HTTP_404_NOT_FOUND)

            # Générer un code aléatoire à 6 chiffres
            code = get_random_string(length=6, allowed_chars='0123456789')

            # Enregistrer le code dans la BD
            PasswordResetCode.objects.create(user=user, code=code)

            # Envoyer l’email
            send_mail(
                'Réinitialisation du mot de passe - Rodbalek',
                f'Voici votre code de réinitialisation : {code}',
                Settings.email_host_user,
                [email],
                fail_silently=False,
            )

            return Response({'message': 'Code envoyé avec succès à votre adresse email.'}, status=status.HTTP_200_OK)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
