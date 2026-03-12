# Système de Location de Voitures

Application JavaFX pour la gestion de location de voitures avec interface utilisateur moderne et panneau d'administration.

## Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **Java 17** ou supérieur
- **Maven 3.6** ou supérieur
- **MySQL** (avec phpMyAdmin ou MySQL Workbench)
- **Git** (pour cloner le projet)

### Vérification des prérequis

```bash
java -version
mvn -version
mysql --version
```

## Installation

### 1. Cloner le projet

```bash
git clone https://github.com/SalahMesmes/Location_Voiture.git
cd LocationVoiture
```

### 2. Configuration de la base de données

#### Étape 1 : Créer la base de données

Ouvrez phpMyAdmin ou MySQL Workbench et exécutez le script SQL fourni :

```bash
# Le script se trouve dans le fichier script_sql.sql
# Importez-le dans phpMyAdmin ou exécutez-le via MySQL
```

Le script crée automatiquement :
- La base de données `location_voitures`
- Les tables : `voitures`, `clients`, `locations`
- Des données d'exemple (10 voitures)

#### Étape 2 : Configurer la connexion

Modifiez le fichier `src/main/resources/database.properties` avec vos paramètres MySQL :

```properties
db.url=jdbc:mysql://localhost:8889/location_voitures?useSSL=false&serverTimezone=Europe/Paris&allowPublicKeyRetrieval=true
db.username=root
db.password=root
```

**Important :** Ajustez le port (8889 ou 3306 selon votre configuration MySQL) et les identifiants si nécessaire.

### 3. Compiler le projet

```bash
mvn clean compile
```

## Lancement de l'application

### Méthode 1 : Avec Maven (recommandé)

```bash
mvn javafx:run
```

### Méthode 2 : Compiler puis exécuter

```bash
# Compiler
mvn clean package

# Exécuter (le JAR sera dans target/)
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp target/classes:target/dependency/* com.carrental.Main
```

### Méthode 3 : Depuis un IDE

1. Ouvrez le projet dans IntelliJ IDEA ou Eclipse
2. Configurez le SDK Java 17
3. Exécutez la classe `com.carrental.Main`

## Fonctionnalités

### Interface Client

#### Consultation des voitures
- Affichage de toutes les voitures disponibles
- Recherche par marque ou modèle
- Filtrage par type de véhicule (Berline, Sport, SUV, 4x4, etc.)
- Affichage des détails : prix par jour, année, carburant, transmission, nombre de places
- Visualisation des photos des voitures

#### Réservation
- Sélection d'une voiture
- Saisie des informations client :
  - Prénom et nom
  - Numéro de téléphone
  - Email
  - Numéro de permis de conduire
  - Date d'expiration du permis
  - Adresse
- Sélection des dates de location (début et fin)
- Calcul automatique du prix total
- Validation des données avant enregistrement

#### Rafraîchissement
- Bouton pour actualiser la liste des voitures disponibles

### Interface Admin

#### Authentification
- Connexion avec :
  - **Nom d'utilisateur :** `admin`
  - **Mot de passe :** `admine123@`

#### Gestion des voitures
- **Ajouter une voiture :**
  - Marque et modèle
  - Type de véhicule
  - Prix par jour
  - Année
  - Type de carburant (Essence, Diesel, Électrique)
  - Nombre de places
  - Transmission (Manuelle, Automatique)
  - URL de l'image (chemin local ou URL web)
  
- **Modifier une voiture :**
  - Édition de tous les champs d'une voiture existante
  
- **Supprimer une voiture :**
  - Suppression avec confirmation

- **Liste des voitures :**
  - Affichage de toutes les voitures avec leurs détails
  - Indication de la disponibilité

#### Historique des locations
- Consultation de l'historique complet des locations
- Affichage des informations :
  - Client (nom, prénom, email)
  - Voiture (marque, modèle, type)
  - Dates de location
  - Nombre de jours
  - Prix total
  - Statut (EN_COURS, TERMINÉE, ANNULÉE)

#### Gestion des locations en cours
- Liste des locations actives
- Possibilité de retourner une voiture (changement de statut)
- Mise à jour automatique de la disponibilité des voitures



## Tests

Pour exécuter les tests unitaires :

```bash
mvn test
```

Les tests couvrent la validation des données (numéros de téléphone, emails, permis de conduire, etc.).

## Configuration avancée

### Ajouter des photos de voitures

Vous pouvez ajouter des photos de voitures de deux manières :

1. **URL web :** Entrez l'URL complète de l'image dans le champ "URL Image" lors de l'ajout/modification d'une voiture
   - Exemple : `https://example.com/voiture.jpg`

2. **Chemin local :** Utilisez le chemin absolu vers l'image sur votre machine
   - Exemple : `/Users/mestaoui/LocationVoiture/src/main/resources/images/cars/voiture.jpg`
   - Les images doivent être placées dans `src/main/resources/images/cars/`

### Port MySQL

Si votre MySQL utilise un port différent de 8889, modifiez `database.properties` :
- Port 3306 (standard) : `jdbc:mysql://localhost:3306/location_voitures...`
- Port 8888 (MAMP) : `jdbc:mysql://localhost:8888/location_voitures...`
- Port 8889 (MAMP alternatif) : `jdbc:mysql://localhost:8889/location_voitures...`

## Dépannage

### Erreur de connexion à la base de données

1. Vérifiez que MySQL est démarré
2. Vérifiez le port dans `database.properties`
3. Vérifiez que la base de données `location_voitures` existe
4. Vérifiez les identifiants (username/password)

### L'application ne démarre pas

1. Vérifiez que Java 17 est installé : `java -version`
2. Vérifiez que Maven est installé : `mvn -version`
3. Nettoyez et recompilez : `mvn clean compile`

### Les images ne s'affichent pas

1. Vérifiez que l'URL est accessible ou que le chemin local est correct
2. Vérifiez les permissions d'accès aux fichiers images
3. Utilisez des chemins absolus pour les images locales

## Technologies utilisées

- **Java 17** : Langage de programmation
- **JavaFX 21** : Framework d'interface graphique
- **Maven** : Gestion des dépendances et build
- **MySQL** : Base de données relationnelle
- **JDBC** : Connexion à la base de données
- **JUnit 5** : Framework de tests unitaires

## Auteur

Salah Mesmes

## Licence

Ce projet est un projet éducatif.
