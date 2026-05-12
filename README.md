# Gestion Scolaire Pro - Nordic Academia

Application JavaFX de gestion scolaire multi-role, preparee pour Eclipse en projet Java classique, sans Maven.

## Theme visuel

- Fond: `#F7F7F2`
- Sidebar: `#1E2A44`
- Accent: `#C9A961`
- Texte principal: `#1A1A1A`
- Texte secondaire: `#6B7280`
- Succes: `#2E7D5B`
- Erreur: `#C0392B`
- Police: `Inter`, `SF Pro`, fallback `Segoe UI`

## Roles disponibles

- Administrateur
- Enseignant
- Parent
- Eleve

## Fonctionnalites principales

- Authentification BCrypt avec routage par role
- Verrouillage temporaire du compte apres 5 echecs
- Dashboard distinct pour chaque role
- CRUD eleves, enseignants, classes, matieres, parents, utilisateurs, annees scolaires
- Affectations enseignant / classe / matiere
- Codes d'invitation parent-eleve
- Notes, bulletins, emploi du temps
- Presences, absences, retards, justification parent
- Messagerie interne
- Frais de scolarite
- Exports CSV
- Sauvegarde logique locale
- Audit log des actions sensibles

## Prerequis

- Java JDK 21
- Eclipse IDE
- MySQL ou MariaDB (XAMPP compatible)
- JavaFX SDK 21

## Ouvrir dans Eclipse

1. Ouvrir Eclipse
2. `File > Open Projects from File System...`
3. Choisir le dossier `gestion-scolaire-pro-extracted`
4. Faire `Project > Clean`
5. Ouvrir  
   `src/com/school/gestion/Launcher.java`
6. `Run As > Java Application`

## Base de donnees

Base par defaut:

- URL: `jdbc:mysql://localhost:3306/gestion_scolaire?serverTimezone=UTC`
- User: `root`
- Password: vide

Variables optionnelles:

- `GESTION_DB_URL`
- `GESTION_DB_USER`
- `GESTION_DB_PASSWORD`

## Scripts SQL

- Schema principal:  
  [C:\Users\Noure\.openclaw\workspace\gestion-scolaire-pro-extracted\src\sql\schema.sql](C:\Users\Noure\.openclaw\workspace\gestion-scolaire-pro-extracted\src\sql\schema.sql)
- Donnees de demo:  
  [C:\Users\Noure\.openclaw\workspace\gestion-scolaire-pro-extracted\src\sql\data_demo.sql](C:\Users\Noure\.openclaw\workspace\gestion-scolaire-pro-extracted\src\sql\data_demo.sql)
- Script tout-en-un existant:  
  [C:\Users\Noure\.openclaw\workspace\gestion-scolaire-pro-extracted\src\sql\schema_mysql.sql](C:\Users\Noure\.openclaw\workspace\gestion-scolaire-pro-extracted\src\sql\schema_mysql.sql)

Ordre recommande:

1. creer la base `gestion_scolaire`
2. executer `schema.sql`
3. executer `data_demo.sql`

## Comptes de demo

- Admin: `admin / admin123`
- Enseignant: `ahmed.bennani / admin123`
- Enseignant: `salma.idrissi / admin123`
- Enseignant: `youssef.alaoui / admin123`
- Parent: `parent.diallo / admin123`
- Parent: `parent.konate / admin123`
- Eleve: `E001 / admin123`
- Eleve: `E003 / admin123`

## Exports et sauvegarde

Les exports admin sont generes dans:

`C:\Users\<VotreNom>\Documents\gestion-scolaire-exports`

Contenu actuel:

- `eleves-export.csv`
- `notes-export.csv`
- `sauvegarde-gestion-scolaire.txt`

## Bibliotheques utilisees

- JavaFX 21
- MySQL Connector/J
- jBCrypt

## Notes

- Le projet est compilable en Java 21.
- Le module PDF complet type iText et les graphiques JFreeChart ne sont pas encore ajoutes comme dependances natives du projet.
- Le theme CSS et les dashboards ont ete alignes vers la direction `Nordic Academia`.
