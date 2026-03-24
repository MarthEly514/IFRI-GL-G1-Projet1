# IFRI-GL-G1-Projet1 -- CampusDocs — Guide d'installation et d'utilisation

**CampusDocs** est une application Java (Spring Boot + JavaFX) permettant de gérer efficacement les actes administratifs au sein des universités, notamment à l'IFRI, en respectant les principes du génie logiciel et en suivant la méthode agile Scrum.

---

## 1. Prérequis

| Composant   | Version minimale                                      |
|-------------|-------------------------------------------------------|
| Java (JDK)  | 17 ou supérieure (21 recommandée)                     |
| JavaFX      | Inclus dans le client (pas d'installation séparée)    |
| MySQL       | 8.0                                                   |
| Maven       | 3.6+                                                  |
| Git         | Optionnel, pour cloner le dépôt                       |
| Spring Boot | Utilisé côté serveur                                  |

---

## 2. Installation et configuration

### 2.1 Base de données MySQL

1. Créez la base de données `campusdocs_db` :

```sql
CREATE DATABASE campusdocs_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Importez le schéma initial (fichier fourni dans le dossier `database/` du dépôt) :

```bash
mysql -u root -p campusdocs_db < database/schema.sql
```

3. Vérifiez que les tables `user`, `usager`, `agentAdministratif`, `administrateur`, `demande`, etc. sont bien créées.

---

### 2.2 Serveur Spring Boot

1. Clonez la branche `spring-server` :

```bash
git clone -b spring-server https://github.com/MarthEly514/IFRI-GL-G1-Projet1.git campusdocs-server
cd campusdocs-server
```

2. Configurez la connexion à la base dans `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campusdocs_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=****
spring.jpa.hibernate.ddl-auto=update

jwt.secret=uneCleSecreteTresLonguePourHS256
jwt.expiration=86400000
```

3. Compilez et lancez le serveur :

```bash
mvn clean package
java -jar target/campusdocs-server.jar
```

Le serveur démarre sur `http://localhost:8080`.

---

### 2.3 Client JavaFX

1. Depuis le même dépôt, basculez sur la branche `client` :

```bash
git checkout client
```

2. Compilez le projet :

```bash
mvn clean package
```

3. Si nécessaire, modifiez l'URL de l'API dans `com.campusdocs.client.api.ApiClient` :

```java
private static final boolean DEV_MODE = false; // false pour la production
private static final String PROD_URL = "http://localhost:8080/api";
```

4. Lancez l'application :
   - Depuis un IDE : exécutez la classe `App.java`.
   - En ligne de commande : `java -jar target/campusdocs-client.jar`

---

## 3. Création du compte administrateur (premier accès)

Le système ne crée pas automatiquement de compte administrateur. Vous devez l'insérer manuellement en base de données.

### 3.1 Générer le hash du mot de passe

Le mot de passe est stocké avec l'algorithme BCrypt. Utilisez le programme Java suivant :

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("admin"));
    }
}
```

Exécutez-le pour obtenir le hash (ex. : `$2a$10$...`).

### 3.2 Insérer l'administrateur dans la base

```sql
INSERT INTO user (id, nom, prenom, email, password, role, actif, date_creation)
VALUES (1, 'Admin', 'System', 'admin@campusdocs.com', 'le_hash_généré', 'ADMIN', 1, NOW());

INSERT INTO administrateur (id) VALUES (1);
```

Vous pouvez désormais vous connecter avec l'adresse `admin@campusdocs.com` et le mot de passe `admin`.

---

## 4. Lancement de l'application

1. **Démarrez le serveur** (Spring Boot) : la console affiche `Started App in ... seconds`.
2. **Lancez le client** (JavaFX) : l'écran de connexion apparaît.

---

## 5. Guide d'utilisation

### 5.1 Se connecter

Saisissez votre adresse e-mail et votre mot de passe. Si les identifiants sont corrects, vous êtes redirigé vers le tableau de bord correspondant à votre rôle.

---

### 5.2 L'étudiant (Usager)

**Soumettre une demande**

1. Dans le menu « Demandes », cliquez sur « Nouvelle demande ».
2. Choisissez le type de document (attestation, bulletin, relevé de notes), remplissez les champs requis, joignez les pièces justificatives (PDF) et validez.
3. Une référence unique est générée automatiquement (ex. : `DEM-2026-001`).

**Consulter ses demandes**

La liste affiche le statut de chaque demande : *En attente*, *Approuvée* ou *Rejetée*. Si la demande est approuvée, un document PDF est disponible en téléchargement.

**Annuler une demande**

Une annulation n'est possible que si la demande est encore *En attente*. Depuis la liste des demandes, cliquez sur « Annuler ».

---

### 5.3 L'agent administratif

**Voir toutes les demandes**

Le tableau de bord affiche les demandes en attente. Vous pouvez filtrer par statut.

**Traiter une demande**

Cliquez sur une demande pour consulter les informations et les pièces jointes.

- **Valider** : le système génère automatiquement le document PDF, crée l'acte administratif et notifie l'étudiant.
- **Rejeter** : vous devez fournir un motif, qui sera affiché à l'étudiant.

**Générer des rapports**

Dans la section « Statistiques », vous pouvez exporter des données (CSV, PDF), selon vos droits d'accès.

---

### 5.4 L'administrateur

**Gérer les agents**

Dans « Gestion utilisateurs », cliquez sur « Créer un agent ». Remplissez le formulaire (nom, prénom, e-mail, service, mot de passe). Le compte agent est créé avec le rôle `AGENT` et activé automatiquement.

**Gérer les comptes utilisateurs**

Vous pouvez désactiver ou réactiver tout compte (étudiant, agent, administrateur) depuis le même écran.

**Configurer le système**

Les paramètres généraux (types d'actes, délais) sont accessibles via le menu « Configuration » (selon l'avancement du projet).

---

## 6. Dépannage

| Problème | Solution |
|---|---|
| **Le serveur ne démarre pas : adresse déjà utilisée** | Arrêtez le processus occupant le port 8080. Sur Linux : `sudo lsof -i :8080` puis `sudo kill -9 <PID>`. Sur Windows : `netstat -ano \| findstr :8080` puis `taskkill /PID <PID> /F`. |
| **Connexion impossible : « Compte désactivé »** | Vérifiez dans la base que `user.actif = 1`. Si l'agent a été créé sans `actif = true`, ajoutez `agent.setActif(true);` dans `UserService.creerAgent`. |
| **Erreur 403 sur `/api/demandes/me`** | Vérifiez que l'utilisateur connecté est bien un `Usager` (table `usager` remplie). Dans le code client, assurez-vous que l'URL ne duplique pas `/api` (utilisez `/demandes/me` et non `/api/demandes/me`). |
| **Mot de passe agent reçu `null`** | Vérifiez l'annotation dans `User.java` : utilisez `@JsonProperty("motDePasse")` sur le setter si le client envoie ce nom. Assurez-vous que le champ `password` est bien présent dans le JSON. |
| **Impossible de créer un agent : rôle `null` en base** | Dans `UserService.creerAgent`, ajoutez `agent.setRole("AGENT");` avant l'enregistrement. |

---

## 7. Annexes

- **Documentation technique** : Javadoc disponible dans le dossier `docs/javadoc/`.
- **Diagrammes UML** : disponibles dans `docs/uml/` (cas d'utilisation, classes, séquences, activités, composants, déploiement).
- **Cahier des charges et document des exigences** : disponibles dans `docs/`.
- **Scripts SQL** : `database/schema.sql` pour la création des tables.

---

## 8. Conclusion

CampusDocs fournit une solution complète et sécurisée pour la gestion des actes administratifs. Grâce à son architecture modulaire et à ses rôles bien définis, elle permet une administration aisée et un suivi transparent des demandes. Ce guide vous permet d'installer, de configurer et d'utiliser l'application dans les meilleures conditions.
