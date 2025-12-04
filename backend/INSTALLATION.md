# Installation du Projet Rod Balik

## Prérequis

1. **Python 3.8+** installé
2. **XAMPP** installé et démarré (Apache + MySQL)
3. **pip** (gestionnaire de paquets Python)

## Étapes d'Installation

### 1. Créer la Base de Données MySQL

1. Démarrez XAMPP et lancez MySQL
2. Ouvrez phpMyAdmin (http://localhost/phpmyadmin)
3. Créez une nouvelle base de données nommée `rod_balik_db`
4. Utilisez l'encodage `utf8mb4_unicode_ci`

### 2. Créer l'Environnement Virtuel

\`\`\`bash
# Créer l'environnement virtuel
python -m venv env

# Activer l'environnement (Windows)
env\Scripts\activate

# Activer l'environnement (Linux/Mac)
source env/bin/activate
\`\`\`

### 3. Installer les Dépendances

\`\`\`bash
pip install -r requirements.txt
\`\`\`

**Note:** Si `mysqlclient` pose problème sur Windows, installez d'abord:
- Visual C++ Build Tools
- Ou utilisez: `pip install pymysql` puis ajoutez dans `__init__.py`:
  \`\`\`python
  import pymysql
  pymysql.install_as_MySQLdb()
  \`\`\`

### 4. Appliquer les Migrations

\`\`\`bash
# Créer les migrations
python manage.py makemigrations

# Appliquer les migrations
python manage.py migrate
\`\`\`

### 5. Créer un Super Utilisateur

\`\`\`bash
python manage.py createsuperuser
\`\`\`

Suivez les instructions pour créer un compte administrateur.

### 6. Lancer le Serveur

\`\`\`bash
python manage.py runserver
\`\`\`

Le serveur sera accessible sur: **http://localhost:8000**

## Accès aux Interfaces

- **Admin Django:** http://localhost:8000/admin
- **API Root:** http://localhost:8000/api/
- **Documentation API:** Voir API_DOCUMENTATION.md

## Configuration XAMPP

Assurez-vous que dans XAMPP:
- MySQL est démarré sur le port 3306
- Apache peut être démarré si vous voulez utiliser phpMyAdmin

## Dépannage

### Erreur de connexion MySQL
- Vérifiez que MySQL est démarré dans XAMPP
- Vérifiez le nom de la base de données: `rod_balik_db`
- Vérifiez l'utilisateur: `root` sans mot de passe (par défaut XAMPP)

### Erreur mysqlclient
- Installez Visual C++ Build Tools
- Ou utilisez pymysql comme alternative

### Port déjà utilisé
\`\`\`bash
python manage.py runserver 8080
