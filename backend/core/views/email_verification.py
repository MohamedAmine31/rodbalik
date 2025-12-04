"""
Views for email verification functionality.
"""

from rest_framework import status, permissions
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from django.core.mail import send_mail
from django.conf import settings
import random
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi
from ..serializers import SendCodeSerializer, VerifyCodeSerializer, ChangePasswordSerializer
from ..services import EmailVerificationService


@swagger_auto_schema(
    method='post',
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        required=['email'],
        properties={
            'email': openapi.Schema(
                type=openapi.TYPE_STRING,
                description="Entrez votre adresse e-mail pour recevoir le code",
                example=""
            ),
        },
    ),
    responses={
        200: openapi.Response(
            description="Code envoyé avec succès.",
            examples={"application/json": {"message": "Un code de vérification a été envoyé à votre adresse e-mail."}},
        ),
        400: openapi.Response(
            description="Erreur : l'adresse e-mail n'existe pas.",
            examples={"application/json": {"error": "L'adresse e-mail n'existe pas dans le système."}},
        ),
    },
    operation_summary="Envoyer un code de vérification"
)
@api_view(['POST'])
@permission_classes([AllowAny])
def send_verification_code(request):
    email = request.data.get('email')
    if not email:
        return Response({"error": "L'adresse e-mail est requise."}, status=status.HTTP_400_BAD_REQUEST)

    # Utiliser le service EmailVerificationService pour envoyer le code
    success, message = EmailVerificationService.send_verification_code(email)

    if success:
        return Response({"message": message}, status=status.HTTP_200_OK)
    else:
        return Response({"error": message}, status=status.HTTP_400_BAD_REQUEST)


@swagger_auto_schema(
    method='post',
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        required=['email', 'new_password', 'confirm_password'],
        properties={
            'email': openapi.Schema(
                type=openapi.TYPE_STRING,
                description="Entrez votre adresse e-mail pour modifier le mot de passe",
                example="utilisateur@example.com"
            ),
            'new_password': openapi.Schema(
                type=openapi.TYPE_STRING,
                description="Nouveau mot de passe",
                example="MotDePasse123!"
            ),
            'confirm_password': openapi.Schema(
                type=openapi.TYPE_STRING,
                description="Confirmez le nouveau mot de passe",
                example="MotDePasse123!"
            ),
        },
    ),
    responses={
        200: openapi.Response(
            description="Mot de passe modifié avec succès",
            examples={"application/json": {"message": "Mot de passe modifié avec succès."}}
        ),
        400: openapi.Response(
            description="Erreur de validation",
            examples={"application/json": {"error": "Les mots de passe ne correspondent pas."}}
        ),
    },
    operation_summary="Modifier le mot de passe d’un utilisateur",
    operation_description="Cette API permet à un utilisateur de définir un nouveau mot de passe en fournissant son e-mail, le nouveau mot de passe et sa confirmation."
)
@api_view(['POST'])
@permission_classes([AllowAny])
def change_password(request):
    serializer = ChangePasswordSerializer(data=request.data)
    if serializer.is_valid():
        user = serializer.validated_data['user']
        user.set_password(serializer.validated_data['new_password'])
        user.save()
        return Response({"message": "Mot de passe modifié avec succès."}, status=status.HTTP_200_OK)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@swagger_auto_schema(
    method='post',
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        required=['email', 'code'],
        properties={
            'email': openapi.Schema(
                type=openapi.TYPE_STRING,
                description="Entrez votre adresse e-mail",
                example=""
            ),
            'code': openapi.Schema(
                type=openapi.TYPE_STRING,
                description="Entrez le code que vous avez reçu par email",
                example=""
            ),
        },
    ),
    responses={
        200: openapi.Response(
            description="Code vérifié avec succès.",
            examples={"application/json": {"message": "Code vérifié avec succès."}},
        ),
        400: openapi.Response(
            description="Code incorrect ou expiré.",
            examples={"application/json": {"error": "Code incorrect ou expiré."}},
        ),
    },
    operation_summary="Vérifier le code de vérification"
)
@api_view(['POST'])
@permission_classes([AllowAny])
def verify_code(request):
    email = request.data.get('email')
    code = request.data.get('code')

    if not email or not code:
        return Response({"error": "Email et code sont requis."}, status=status.HTTP_400_BAD_REQUEST)

    # Utiliser le service EmailVerificationService pour vérifier le code
    success, message = EmailVerificationService.verify_code(email, code)

    if success:
        return Response({"message": message}, status=status.HTTP_200_OK)
    else:
        return Response({"error": message}, status=status.HTTP_400_BAD_REQUEST)
