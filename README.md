Projet : Rod-Balik


Description : Projet d’intégration Rod-Balik comprenant les parties suivantes :
- Frontend Angular : dossier "angular_front"
- Backend Django : dossier "backend"
- Mobile (si applicable) : dossier "mobile"
- Photos / ressources : dossier "photo de projet"

Lien GitHub du projet :
https://github.com/MohamedAmine31/rodbalik.git

Instructions pour récupérer le projet :

1. Cloner le dépôt :
   git clone https://github.com/MohamedAmine31/rodbalik.git

2. Installer les dépendances frontend :
   cd angular_front
   npm install
   ng serve

3. Installer le backend Django :
   cd ../backend
   python -m venv venv
   venv\Scripts\activate     (Windows)
   source venv/bin/activate  (Linux/Mac)
   pip install -r requirements.txt
   python manage.py migrate
   python manage.py runserver

Remarques :
- Le dossier node_modules n’est pas inclus dans GitHub.
- Utiliser npm install pour Angular et pip install -r requirements.txt pour Django.
- Le projet est public et prêt à être cloné.
