# Documentation API - Projet Rod Balik

## Base URL
\`\`\`
http://localhost:8000/api/
\`\`\`

## Authentification

Toutes les API protégées nécessitent un token JWT dans le header:
\`\`\`
Authorization: Bearer <votre_token>
\`\`\`

### Obtenir un Token

**Endpoint:** `POST /api/token/`

**Body:**
\`\`\`json
{
  "username": "votre_username",
  "password": "votre_password"
}
\`\`\`

**Réponse:**
\`\`\`json
{
  "access": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "refresh": "eyJ0eXAiOiJKV1QiLCJhbGc..."
}
\`\`\`

### Rafraîchir un Token

**Endpoint:** `POST /api/token/refresh/`

**Body:**
\`\`\`json
{
  "refresh": "votre_refresh_token"
}
\`\`\`

---

## 1. Authentification et Utilisateurs

### 1.1 Inscription

**Endpoint:** `POST /api/auth/register/`

**Permissions:** Public

**Body:**
\`\`\`json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "password_confirm": "SecurePass123",
  "first_name": "John",
  "last_name": "Doe",
  "phone": "+33612345678",
  "role": "CITOYEN"
}
\`\`\`

**Réponse:** `201 Created`
\`\`\`json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "CITOYEN"
}
\`\`\`

### 1.2 Profil Utilisateur

**Endpoint:** `GET /api/auth/profile/`

**Permissions:** Authentifié

**Réponse:**
\`\`\`json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "first_name": "John",
  "last_name": "Doe",
  "role": "CITOYEN",
  "phone": "+33612345678",
  "created_at": "2025-01-15T10:30:00Z"
}
\`\`\`

### 1.3 Envoi de Code de Vérification Email

**Endpoint:** `POST /api/email/send-code/`

**Permissions:** Public

**Body:**
\`\`\`json
{
  "email": "john@example.com"
}
\`\`\`

**Description:** Envoie un code de vérification à 6 chiffres à l'adresse e-mail spécifiée. Le code expire après 10 minutes.

**Réponse:** `200 OK`
\`\`\`json
{
  "message": "Un code de vérification a été envoyé à john@example.com."
}
\`\`\`

### 1.4 Vérification de Code Email

**Endpoint:** `POST /api/email/verify-code/`

**Permissions:** Public

**Body:**
\`\`\`json
{
  "email": "john@example.com",
  "code": "123456"
}
\`\`\`

**Description:** Vérifie le code de vérification envoyé à l'e-mail.

**Réponse:** `200 OK`
\`\`\`json
{
  "message": "Code vérifié avec succès."
}
\`\`\`

---

## 2. Catégories

### 2.1 Liste des Catégories

**Endpoint:** `GET /api/categories/`

**Permissions:** Public

**Réponse:**
\`\`\`json
[
  {
    "id": 1,
    "name": "Déchets illégaux",
    "description": "Dépôts sauvages et déchets abandonnés",
    "is_active": true,
    "created_at": "2025-01-10T08:00:00Z",
    "signalements_count": 45
  }
]
\`\`\`

### 2.2 Créer une Catégorie

**Endpoint:** `POST /api/categories/`

**Permissions:** Admin uniquement

**Body:**
\`\`\`json
{
  "name": "Vandalisme",
  "description": "Dégradations de biens publics"
}
\`\`\`

### 2.3 Modifier/Supprimer une Catégorie

**Endpoints:**
- `PUT /api/categories/<id>/`
- `DELETE /api/categories/<id>/`

**Permissions:** Admin uniquement

---

## 3. Signalements

### 3.1 Soumettre un Signalement (REQ-S-01 à REQ-S-05)

**Endpoint:** `POST /api/signalement/soumettre/`

**Permissions:** Authentifié (Citoyen)

**Content-Type:** `multipart/form-data`

**Body:**
\`\`\`
media: [fichier photo/vidéo]
description: "Dépôt sauvage de déchets sur le trottoir"
location: "123 Rue de la République, Paris"
latitude: 48.8566
longitude: 2.3522
category: 1
\`\`\`

**Réponse:** `201 Created`
\`\`\`json
{
  "id": 15,
  "user": {...},
  "category": {...},
  "media": "/media/signalements/2025/01/15/photo.jpg",
  "description": "Dépôt sauvage de déchets sur le trottoir",
  "location": "123 Rue de la République, Paris",
  "status": "EN_COURS",
  "date": "2025-01-15T14:30:00Z"
}
\`\`\`

### 3.2 Liste des Signalements

**Endpoint:** `GET /api/signalements/`

**Permissions:** Authentifié

**Query Parameters:**
- `status`: EN_COURS, VALIDE, REFUSE
- `category`: ID de la catégorie
- `location`: Filtrer par localisation
- `ordering`: -date, score_gravite

**Exemple:**
\`\`\`
GET /api/signalements/?status=VALIDE&category=1&ordering=-date
\`\`\`

### 3.3 Détail d'un Signalement

**Endpoint:** `GET /api/signalements/<id>/`

**Permissions:** Authentifié ou Public (selon configuration)

### 3.4 Modifier un Signalement

**Endpoint:** `PUT /api/signalements/<id>/update/`

**Permissions:** Propriétaire ou Admin

### 3.5 Mur Public (REQ-MP-01 à REQ-MP-03)

**Endpoint:** `GET /api/signalement/mur-public/`

**Permissions:** Public

**Query Parameters:**
- `category`: Filtrer par catégorie
- `location`: Filtrer par localisation

**Description:** Affiche uniquement les signalements validés, triés par date décroissante.

---

## 4. Administration

### 4.1 Valider/Rejeter un Signalement (REQ-AD-01)

**Endpoint:** `POST /api/signalement/valider/<id>/`

**Permissions:** Admin uniquement

**Body (Validation):**
\`\`\`json
{
  "action": "valider"
}
\`\`\`

**Body (Rejet):**
\`\`\`json
{
  "action": "rejeter",
  "rejection_reason": "Photo de mauvaise qualité"
}
\`\`\`

**Réponse:**
\`\`\`json
{
  "message": "Signalement validé avec succès",
  "signalement": {...}
}
\`\`\`

### 4.2 Supprimer un Signalement (REQ-AD-02)

**Endpoint:** `DELETE /api/signalement/supprimer/<id>/`

**Permissions:** Admin uniquement

**Réponse:** `204 No Content`

### 4.3 Signalements en Attente

**Endpoint:** `GET /api/signalement/en-attente/`

**Permissions:** Admin uniquement

**Description:** Liste tous les signalements avec status="EN_COURS"

### 4.4 Statistiques (REQ-AD-03)

**Endpoint:** `GET /api/statistiques/dashboard/`

**Permissions:** Admin uniquement

**Réponse:**
\`\`\`json
{
  "global": {
    "total": 150,
    "valides": 100,
    "en_cours": 30,
    "refuses": 20
  },
  "par_categorie": [
    {
      "category__name": "Déchets illégaux",
      "total_count": 45,
      "validated_count": 30,
      "pending_count": 10,
      "rejected_count": 5
    }
  ],
  "par_zone": [
    {
      "location": "Paris",
      "count": 80
    }
  ]
}
\`\`\`

---

## 5. Partage Automatique (REQ-PA-01)

**Endpoint:** `POST /api/signalement/partager/<id>/`

**Permissions:** Admin uniquement

**Description:** Partage un signalement validé sur Facebook et Instagram

**Réponse:**
\`\`\`json
{
  "message": "Signalement partagé avec succès",
  "signalement": {...}
}
\`\`\`

---

## 6. Analyse IA (REQ-IA-01 à REQ-IA-03)

**Endpoint:** `POST /api/signalement/analyser/<id>/`

**Permissions:** Admin uniquement

**Description:** Analyse le média avec IA pour détecter l'infraction et calculer un score de gravité

**Réponse:**
\`\`\`json
{
  "message": "Analyse IA effectuée",
  "classification": "Déchets illégaux détectés",
  "score_gravite": 75.5
}
\`\`\`

---

## Codes d'Erreur

- `400 Bad Request`: Données invalides
- `401 Unauthorized`: Token manquant ou invalide
- `403 Forbidden`: Permissions insuffisantes
- `404 Not Found`: Ressource introuvable
- `500 Internal Server Error`: Erreur serveur

---

## Notes Importantes

1. **Taille des fichiers:** Maximum 50MB pour les médias
2. **Formats acceptés:** JPG, PNG, MP4, MOV, AVI
3. **Rate limiting:** À configurer en production
4. **HTTPS:** Obligatoire en production
5. **CORS:** Configuré pour accepter toutes les origines en développement

---

## Exemples avec cURL

### Obtenir un token
\`\`\`bash
curl -X POST http://localhost:8000/api/token/ \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
\`\`\`

### Soumettre un signalement
\`\`\`bash
curl -X POST http://localhost:8000/api/signalement/soumettre/ \
  -H "Authorization: Bearer <token>" \
  -F "media=@photo.jpg" \
  -F "description=Déchets abandonnés" \
  -F "location=Paris" \
  -F "category=1"
\`\`\`

### Valider un signalement
\`\`\`bash
curl -X POST http://localhost:8000/api/signalement/valider/15/ \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"action":"valider"}'
