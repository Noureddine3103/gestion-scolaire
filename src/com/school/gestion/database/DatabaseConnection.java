package com.school.gestion.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = System.getenv("GESTION_DB_URL") != null
        ? System.getenv("GESTION_DB_URL")
        : "jdbc:mysql://localhost:3306/gestion_scolaire?serverTimezone=UTC";
    private static final String DB_USER = System.getenv("GESTION_DB_USER") != null
        ? System.getenv("GESTION_DB_USER")
        : "root";
    private static final String DB_PASSWORD = System.getenv("GESTION_DB_PASSWORD") != null
        ? System.getenv("GESTION_DB_PASSWORD")
        : "";

    private static Connection connection;
    private static boolean schemaChecked;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            ensureSchema(connection);
        }
        return connection;
    }

    private static void ensureSchema(Connection connection) {
        if (schemaChecked) {
            return;
        }
        String sql = "CREATE TABLE IF NOT EXISTS EMPLOI_DU_TEMPS (" +
            "idSeance INT PRIMARY KEY AUTO_INCREMENT, " +
            "jour VARCHAR(20) NOT NULL, " +
            "heureDebut TIME NOT NULL, " +
            "heureFin TIME NOT NULL, " +
            "salle VARCHAR(40), " +
            "idClasse INT NOT NULL, " +
            "codeMatiere VARCHAR(10) NOT NULL, " +
            "matriculeEnseignant VARCHAR(20) NOT NULL, " +
            "FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse), " +
            "FOREIGN KEY (codeMatiere) REFERENCES MATIERE(code), " +
            "FOREIGN KEY (matriculeEnseignant) REFERENCES ENSEIGNANT(matricule)" +
            ")";
        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
            stmt.execute("ALTER TABLE ELEVE ADD COLUMN IF NOT EXISTS email VARCHAR(120)");
            stmt.execute("ALTER TABLE ELEVE ADD COLUMN IF NOT EXISTS photo LONGBLOB");
            stmt.execute("ALTER TABLE ELEVE ADD COLUMN IF NOT EXISTS parentNom VARCHAR(80)");
            stmt.execute("ALTER TABLE ELEVE ADD COLUMN IF NOT EXISTS parentPrenom VARCHAR(80)");
            stmt.execute("ALTER TABLE ELEVE ADD COLUMN IF NOT EXISTS parentEmail VARCHAR(120)");
            stmt.execute("ALTER TABLE ELEVE ADD COLUMN IF NOT EXISTS parentTelephone VARCHAR(30)");
            stmt.execute("ALTER TABLE ELEVE ADD COLUMN IF NOT EXISTS parentAdresse VARCHAR(255)");
            stmt.execute("ALTER TABLE UTILISATEUR ADD COLUMN IF NOT EXISTS failedAttempts INT DEFAULT 0");
            stmt.execute("ALTER TABLE UTILISATEUR ADD COLUMN IF NOT EXISTS lockUntil DATETIME NULL");
            stmt.execute("ALTER TABLE ENSEIGNANT ADD COLUMN IF NOT EXISTS photo LONGBLOB");
            stmt.execute("CREATE TABLE IF NOT EXISTS PARENT (" +
                "idParent INT PRIMARY KEY AUTO_INCREMENT, " +
                "idUtilisateur INT NULL, " +
                "nom VARCHAR(80) NOT NULL, " +
                "prenom VARCHAR(80) NOT NULL, " +
                "telephone VARCHAR(30), " +
                "profession VARCHAR(120), " +
                "adresse VARCHAR(255), " +
                "email VARCHAR(120), " +
                "FOREIGN KEY (idUtilisateur) REFERENCES UTILISATEUR(id)" +
                ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS PARENT_ELEVE (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "parentId INT NOT NULL, " +
                "matriculeEleve VARCHAR(20) NOT NULL, " +
                "lienParente VARCHAR(20) NOT NULL DEFAULT 'TUTEUR', " +
                "autoriseRecuperation TINYINT(1) DEFAULT 1, " +
                "contactUrgence TINYINT(1) DEFAULT 0, " +
                "valideParAdmin TINYINT(1) DEFAULT 0, " +
                "dateLiaison DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE KEY uk_parent_eleve (parentId, matriculeEleve), " +
                "FOREIGN KEY (parentId) REFERENCES PARENT(idParent) ON DELETE CASCADE, " +
                "FOREIGN KEY (matriculeEleve) REFERENCES ELEVE(matricule) ON DELETE CASCADE" +
                ")");
            stmt.execute("ALTER TABLE PARENT_ELEVE MODIFY COLUMN valideParAdmin TINYINT(1) DEFAULT 0");
            stmt.execute("CREATE TABLE IF NOT EXISTS CODES_INVITATION (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "code VARCHAR(20) UNIQUE NOT NULL, " +
                "matriculeEleve VARCHAR(20) NOT NULL, " +
                "lienParente VARCHAR(20), " +
                "utilise TINYINT(1) DEFAULT 0, " +
                "utilisePar INT NULL, " +
                "dateCreation DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "dateExpiration DATETIME NULL, " +
                "FOREIGN KEY (matriculeEleve) REFERENCES ELEVE(matricule), " +
                "FOREIGN KEY (utilisePar) REFERENCES PARENT(idParent)" +
                ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS PERIODE_SCOLAIRE (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "libelle VARCHAR(60) NOT NULL, " +
                "type VARCHAR(20) NOT NULL, " +
                "ordreAffichage INT NOT NULL DEFAULT 1, " +
                "estActive TINYINT(1) DEFAULT 1" +
                ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS CONFIGURATION_SCOLAIRE (" +
                "cleConfig VARCHAR(80) PRIMARY KEY, " +
                "valeurConfig VARCHAR(255), " +
                "categorie VARCHAR(40), " +
                "descriptionConfig VARCHAR(255)" +
                ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS PRESENCE_SEANCE (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "idSeance INT NOT NULL, " +
                "matriculeEleve VARCHAR(20) NOT NULL, " +
                "statut VARCHAR(20) NOT NULL DEFAULT 'PRESENT', " +
                "remarque VARCHAR(255), " +
                "justificationParent VARCHAR(255), " +
                "dateSaisie DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE KEY uk_presence (idSeance, matriculeEleve), " +
                "FOREIGN KEY (idSeance) REFERENCES EMPLOI_DU_TEMPS(idSeance) ON DELETE CASCADE, " +
                "FOREIGN KEY (matriculeEleve) REFERENCES ELEVE(matricule) ON DELETE CASCADE" +
                ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS MESSAGE_INTERNE (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "expediteurRole VARCHAR(20) NOT NULL, " +
                "expediteurId INT NOT NULL, " +
                "expediteurNom VARCHAR(120) NOT NULL, " +
                "destinataireRole VARCHAR(20) NOT NULL, " +
                "destinataireId INT NULL, " +
                "matriculeEleve VARCHAR(20) NULL, " +
                "sujet VARCHAR(150) NOT NULL, " +
                "contenu TEXT NOT NULL, " +
                "dateEnvoi DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "lu TINYINT(1) DEFAULT 0, " +
                "FOREIGN KEY (matriculeEleve) REFERENCES ELEVE(matricule) ON DELETE SET NULL" +
                ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS FRAIS_SCOLARITE (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "matriculeEleve VARCHAR(20) NOT NULL, " +
                "libelle VARCHAR(120) NOT NULL, " +
                "montant DECIMAL(10,2) NOT NULL DEFAULT 0, " +
                "dateEcheance DATE NOT NULL, " +
                "statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE', " +
                "datePaiement DATE NULL, " +
                "commentaire VARCHAR(255), " +
                "FOREIGN KEY (matriculeEleve) REFERENCES ELEVE(matricule) ON DELETE CASCADE" +
                ")");
            stmt.execute("CREATE TABLE IF NOT EXISTS AUDIT_LOG (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "userId INT NULL, " +
                "username VARCHAR(80), " +
                "actionType VARCHAR(80) NOT NULL, " +
                "detailsAction VARCHAR(255), " +
                "createdAt DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")");
            seedSupportTables(stmt);
            schemaChecked = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedSupportTables(java.sql.Statement stmt) throws SQLException {
        stmt.executeUpdate(
            "INSERT INTO PERIODE_SCOLAIRE (libelle, type, ordreAffichage, estActive) " +
            "SELECT 'Trimestre 1', 'TRIMESTRE', 1, 1 " +
            "WHERE NOT EXISTS (SELECT 1 FROM PERIODE_SCOLAIRE)"
        );
        stmt.executeUpdate(
            "INSERT INTO PERIODE_SCOLAIRE (libelle, type, ordreAffichage, estActive) " +
            "SELECT 'Trimestre 2', 'TRIMESTRE', 2, 1 " +
            "WHERE NOT EXISTS (SELECT 1 FROM PERIODE_SCOLAIRE WHERE libelle = 'Trimestre 2')"
        );
        stmt.executeUpdate(
            "INSERT INTO PERIODE_SCOLAIRE (libelle, type, ordreAffichage, estActive) " +
            "SELECT 'Trimestre 3', 'TRIMESTRE', 3, 1 " +
            "WHERE NOT EXISTS (SELECT 1 FROM PERIODE_SCOLAIRE WHERE libelle = 'Trimestre 3')"
        );
        stmt.executeUpdate(
            "INSERT INTO CONFIGURATION_SCOLAIRE (cleConfig, valeurConfig, categorie, descriptionConfig) " +
            "SELECT 'BAREME_NOTE','20','BAREME','Bareme general des notes' " +
            "WHERE NOT EXISTS (SELECT 1 FROM CONFIGURATION_SCOLAIRE WHERE cleConfig='BAREME_NOTE')"
        );
        stmt.executeUpdate(
            "INSERT INTO CONFIGURATION_SCOLAIRE (cleConfig, valeurConfig, categorie, descriptionConfig) " +
            "SELECT 'COEF_DEVOIR','1','COEFFICIENT','Coefficient des devoirs' " +
            "WHERE NOT EXISTS (SELECT 1 FROM CONFIGURATION_SCOLAIRE WHERE cleConfig='COEF_DEVOIR')"
        );
        stmt.executeUpdate(
            "INSERT INTO CONFIGURATION_SCOLAIRE (cleConfig, valeurConfig, categorie, descriptionConfig) " +
            "SELECT 'COEF_EXAMEN','2','COEFFICIENT','Coefficient des examens' " +
            "WHERE NOT EXISTS (SELECT 1 FROM CONFIGURATION_SCOLAIRE WHERE cleConfig='COEF_EXAMEN')"
        );
        stmt.executeUpdate(
            "INSERT INTO CONFIGURATION_SCOLAIRE (cleConfig, valeurConfig, categorie, descriptionConfig) " +
            "SELECT 'COEF_COMPOSITION','2','COEFFICIENT','Coefficient des compositions' " +
            "WHERE NOT EXISTS (SELECT 1 FROM CONFIGURATION_SCOLAIRE WHERE cleConfig='COEF_COMPOSITION')"
        );
        stmt.executeUpdate(
            "INSERT INTO FRAIS_SCOLARITE (matriculeEleve, libelle, montant, dateEcheance, statut, datePaiement, commentaire) " +
            "SELECT 'E001','Frais de scolarite 2025-2026',8500.00,'2025-10-15','PAYE','2025-10-10','Paiement complet' " +
            "WHERE NOT EXISTS (SELECT 1 FROM FRAIS_SCOLARITE)"
        );
        stmt.executeUpdate(
            "INSERT INTO FRAIS_SCOLARITE (matriculeEleve, libelle, montant, dateEcheance, statut, datePaiement, commentaire) " +
            "SELECT 'E002','Frais de scolarite 2025-2026',8500.00,'2025-10-15','EN_ATTENTE',NULL,'Relance a prevoir' " +
            "WHERE NOT EXISTS (SELECT 1 FROM FRAIS_SCOLARITE WHERE matriculeEleve='E002')"
        );
        stmt.executeUpdate(
            "INSERT INTO MESSAGE_INTERNE (expediteurRole, expediteurId, expediteurNom, destinataireRole, destinataireId, matriculeEleve, sujet, contenu, lu) " +
            "SELECT 'ENSEIGNANT',2,'Salma El Idrissi','PARENT',1,'E001','Projet de classe','Merci de verifier le cahier de textes de votre enfant.',0 " +
            "WHERE NOT EXISTS (SELECT 1 FROM MESSAGE_INTERNE)"
        );
        stmt.executeUpdate(
            "INSERT INTO MESSAGE_INTERNE (expediteurRole, expediteurId, expediteurNom, destinataireRole, destinataireId, matriculeEleve, sujet, contenu, lu) " +
            "SELECT 'ENSEIGNANT',2,'Salma El Idrissi','ELEVE',NULL,'E003','Lecture obligatoire','Pensez a terminer la lecture avant vendredi.',0 " +
            "WHERE NOT EXISTS (SELECT 1 FROM MESSAGE_INTERNE WHERE destinataireRole='ELEVE' AND matriculeEleve='E003')"
        );
        stmt.executeUpdate(
            "INSERT INTO PRESENCE_SEANCE (idSeance, matriculeEleve, statut, remarque, justificationParent) " +
            "SELECT 1,'E001','PRESENT','',NULL " +
            "WHERE NOT EXISTS (SELECT 1 FROM PRESENCE_SEANCE)"
        );
        stmt.executeUpdate(
            "INSERT INTO PRESENCE_SEANCE (idSeance, matriculeEleve, statut, remarque, justificationParent) " +
            "SELECT 1,'E002','RETARD','Arrive avec 10 minutes de retard',NULL " +
            "WHERE NOT EXISTS (SELECT 1 FROM PRESENCE_SEANCE WHERE idSeance=1 AND matriculeEleve='E002')"
        );
        stmt.executeUpdate(
            "INSERT INTO PRESENCE_SEANCE (idSeance, matriculeEleve, statut, remarque, justificationParent) " +
            "SELECT 2,'E003','ABSENT','Absence constatee',NULL " +
            "WHERE NOT EXISTS (SELECT 1 FROM PRESENCE_SEANCE WHERE idSeance=2 AND matriculeEleve='E003')"
        );
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
