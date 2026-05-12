-- =====================================================
-- GESTION SCOLAIRE - Schema MySQL (XAMPP Compatible)
-- =====================================================

-- Drop tables if exist (for development reset)
DROP TABLE IF EXISTS NOTE;
DROP TABLE IF EXISTS EMPLOI_DU_TEMPS;
DROP TABLE IF EXISTS MESSAGE_INTERNE;
DROP TABLE IF EXISTS PRESENCE_SEANCE;
DROP TABLE IF EXISTS FRAIS_SCOLARITE;
DROP TABLE IF EXISTS CONFIGURATION_SCOLAIRE;
DROP TABLE IF EXISTS PERIODE_SCOLAIRE;
DROP TABLE IF EXISTS CODES_INVITATION;
DROP TABLE IF EXISTS PARENT_ELEVE;
DROP TABLE IF EXISTS CLASSE_ENSEIGNANT;
DROP TABLE IF EXISTS INSCRIPTION;
DROP TABLE IF EXISTS CLASSE;
DROP TABLE IF EXISTS NIVEAU;
DROP TABLE IF EXISTS MATIERE;
DROP TABLE IF EXISTS PARENT;
DROP TABLE IF EXISTS ENSEIGNANT;
DROP TABLE IF EXISTS ELEVE;
DROP TABLE IF EXISTS ANNEE_SCOLAIRE;
DROP TABLE IF EXISTS UTILISATEUR;

-- =====================================================
-- Table: ANNEE_SCOLAIRE
-- =====================================================
CREATE TABLE ANNEE_SCOLAIRE (
    idAnnee INT PRIMARY KEY AUTO_INCREMENT,
    annee VARCHAR(9) NOT NULL UNIQUE,
    dateDebut DATE NOT NULL,
    dateFin DATE NOT NULL,
    estActive TINYINT(1) DEFAULT 0
);

-- =====================================================
-- Table: NIVEAU
-- =====================================================
CREATE TABLE NIVEAU (
    idNiveau INT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(50) NOT NULL UNIQUE,
    libelleCourt VARCHAR(10) NOT NULL
);

-- =====================================================
-- Table: MATIERE
-- =====================================================
CREATE TABLE MATIERE (
    code VARCHAR(10) PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    coefficient DECIMAL(4,2) NOT NULL DEFAULT 1.0
);

-- =====================================================
-- Table: UTILISATEUR
-- =====================================================
CREATE TABLE UTILISATEUR (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'ENSEIGNANT', 'PARENT', 'ELEVE')),
    idPersonne INT NOT NULL,
    estActif TINYINT(1) DEFAULT 1
);

-- =====================================================
-- Table: ELEVE
-- =====================================================
CREATE TABLE ELEVE (
    matricule VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    dateNaissance DATE NOT NULL,
    sexe VARCHAR(1) NOT NULL CHECK (sexe IN ('M', 'F')),
    adresse VARCHAR(255),
    telephone VARCHAR(20),
    photo LONGBLOB,
    niveau VARCHAR(50) NOT NULL,
    idUtilisateur INT,
    dateInscription DATE DEFAULT (CURRENT_DATE),
    email VARCHAR(120),
    parentNom VARCHAR(80),
    parentPrenom VARCHAR(80),
    parentEmail VARCHAR(120),
    parentTelephone VARCHAR(30),
    parentAdresse VARCHAR(255)
);

-- =====================================================
-- Table: ENSEIGNANT
-- =====================================================
CREATE TABLE ENSEIGNANT (
    matricule VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    dateNaissance DATE NOT NULL,
    sexe VARCHAR(1) NOT NULL CHECK (sexe IN ('M', 'F')),
    adresse VARCHAR(255),
    telephone VARCHAR(20),
    photo LONGBLOB,
    grade VARCHAR(50) NOT NULL,
    idUtilisateur INT
);

-- =====================================================
-- Table: PARENT
-- =====================================================
CREATE TABLE PARENT (
    idParent INT PRIMARY KEY AUTO_INCREMENT,
    idUtilisateur INT,
    nom VARCHAR(80) NOT NULL,
    prenom VARCHAR(80) NOT NULL,
    telephone VARCHAR(30),
    profession VARCHAR(120),
    adresse VARCHAR(255),
    email VARCHAR(120),
    FOREIGN KEY (idUtilisateur) REFERENCES UTILISATEUR(id)
);

-- =====================================================
-- Table: CLASSE
-- =====================================================
CREATE TABLE CLASSE (
    idClasse INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(20) NOT NULL,
    niveau VARCHAR(50) NOT NULL,
    capacite INT NOT NULL DEFAULT 20 CHECK (capacite >= 1 AND capacite <= 40),
    nomComplet VARCHAR(100),
    idEnseignant VARCHAR(20),
    FOREIGN KEY (niveau) REFERENCES NIVEAU(libelle),
    FOREIGN KEY (idEnseignant) REFERENCES ENSEIGNANT(matricule),
    UNIQUE (nom, niveau)
);

-- =====================================================
-- Table: INSCRIPTION
-- =====================================================
CREATE TABLE INSCRIPTION (
    idInscription INT PRIMARY KEY AUTO_INCREMENT,
    matricule VARCHAR(20) NOT NULL,
    idAnnee INT NOT NULL,
    idClasse INT NOT NULL,
    dateInscription DATE DEFAULT (CURRENT_DATE),
    statut VARCHAR(20) DEFAULT 'ACTIF',
    FOREIGN KEY (matricule) REFERENCES ELEVE(matricule),
    FOREIGN KEY (idAnnee) REFERENCES ANNEE_SCOLAIRE(idAnnee),
    FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse),
    UNIQUE (matricule, idAnnee)
);

-- =====================================================
-- Table: CLASSE_ENSEIGNANT
-- =====================================================
CREATE TABLE CLASSE_ENSEIGNANT (
    id INT PRIMARY KEY AUTO_INCREMENT,
    matriculeEnseignant VARCHAR(20) NOT NULL,
    idClasse INT NOT NULL,
    codeMatiere VARCHAR(10) NOT NULL,
    FOREIGN KEY (matriculeEnseignant) REFERENCES ENSEIGNANT(matricule),
    FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse),
    FOREIGN KEY (codeMatiere) REFERENCES MATIERE(code),
    UNIQUE (matriculeEnseignant, idClasse, codeMatiere)
);

-- =====================================================
-- Table: NOTE
-- =====================================================
CREATE TABLE NOTE (
    idNote INT PRIMARY KEY AUTO_INCREMENT,
    matricule VARCHAR(20) NOT NULL,
    idAnnee INT NOT NULL,
    idClasse INT NOT NULL,
    codeMatiere VARCHAR(10) NOT NULL,
    trimestre INT NOT NULL CHECK (trimestre BETWEEN 1 AND 3),
    noteDevoir DECIMAL(4,2) CHECK (noteDevoir IS NULL OR (noteDevoir >= 0 AND noteDevoir <= 20)),
    noteExamens DECIMAL(4,2) CHECK (noteExamens IS NULL OR (noteExamens >= 0 AND noteExamens <= 20)),
    noteComposition DECIMAL(4,2) CHECK (noteComposition IS NULL OR (noteComposition >= 0 AND noteComposition <= 20)),
    dateSaisie DATE DEFAULT (CURRENT_DATE),
    matriculeEnseignant VARCHAR(20),
    FOREIGN KEY (matricule) REFERENCES ELEVE(matricule),
    FOREIGN KEY (idAnnee) REFERENCES ANNEE_SCOLAIRE(idAnnee),
    FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse),
    FOREIGN KEY (codeMatiere) REFERENCES MATIERE(code),
    FOREIGN KEY (matriculeEnseignant) REFERENCES ENSEIGNANT(matricule),
    UNIQUE (matricule, idAnnee, idClasse, codeMatiere, trimestre)
);

CREATE TABLE EMPLOI_DU_TEMPS (
    idSeance INT PRIMARY KEY AUTO_INCREMENT,
    jour VARCHAR(20) NOT NULL,
    heureDebut TIME NOT NULL,
    heureFin TIME NOT NULL,
    salle VARCHAR(40),
    idClasse INT NOT NULL,
    codeMatiere VARCHAR(10) NOT NULL,
    matriculeEnseignant VARCHAR(20) NOT NULL,
    FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse),
    FOREIGN KEY (codeMatiere) REFERENCES MATIERE(code),
    FOREIGN KEY (matriculeEnseignant) REFERENCES ENSEIGNANT(matricule)
);

CREATE TABLE PARENT_ELEVE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    parentId INT NOT NULL,
    matriculeEleve VARCHAR(20) NOT NULL,
    lienParente VARCHAR(20) NOT NULL DEFAULT 'TUTEUR',
    autoriseRecuperation TINYINT(1) DEFAULT 1,
    contactUrgence TINYINT(1) DEFAULT 0,
    dateLiaison DATETIME DEFAULT CURRENT_TIMESTAMP,
    valideParAdmin TINYINT(1) DEFAULT 0,
    UNIQUE KEY uk_parent_eleve (parentId, matriculeEleve),
    FOREIGN KEY (parentId) REFERENCES PARENT(idParent) ON DELETE CASCADE,
    FOREIGN KEY (matriculeEleve) REFERENCES ELEVE(matricule) ON DELETE CASCADE
);

CREATE TABLE CODES_INVITATION (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) UNIQUE NOT NULL,
    matriculeEleve VARCHAR(20) NOT NULL,
    lienParente VARCHAR(20),
    utilise TINYINT(1) DEFAULT 0,
    utilisePar INT NULL,
    dateCreation DATETIME DEFAULT CURRENT_TIMESTAMP,
    dateExpiration DATETIME,
    FOREIGN KEY (matriculeEleve) REFERENCES ELEVE(matricule),
    FOREIGN KEY (utilisePar) REFERENCES PARENT(idParent)
);

CREATE TABLE PERIODE_SCOLAIRE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(60) NOT NULL,
    type VARCHAR(20) NOT NULL,
    ordreAffichage INT NOT NULL DEFAULT 1,
    estActive TINYINT(1) DEFAULT 1
);

CREATE TABLE CONFIGURATION_SCOLAIRE (
    cleConfig VARCHAR(80) PRIMARY KEY,
    valeurConfig VARCHAR(255),
    categorie VARCHAR(40),
    descriptionConfig VARCHAR(255)
);

CREATE TABLE PRESENCE_SEANCE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    idSeance INT NOT NULL,
    matriculeEleve VARCHAR(20) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    remarque VARCHAR(255),
    justificationParent VARCHAR(255),
    dateSaisie DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_presence (idSeance, matriculeEleve),
    FOREIGN KEY (idSeance) REFERENCES EMPLOI_DU_TEMPS(idSeance) ON DELETE CASCADE,
    FOREIGN KEY (matriculeEleve) REFERENCES ELEVE(matricule) ON DELETE CASCADE
);

CREATE TABLE MESSAGE_INTERNE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    expediteurRole VARCHAR(20) NOT NULL,
    expediteurId INT NOT NULL,
    expediteurNom VARCHAR(120) NOT NULL,
    destinataireRole VARCHAR(20) NOT NULL,
    destinataireId INT NULL,
    matriculeEleve VARCHAR(20) NULL,
    sujet VARCHAR(150) NOT NULL,
    contenu TEXT NOT NULL,
    dateEnvoi DATETIME DEFAULT CURRENT_TIMESTAMP,
    lu TINYINT(1) DEFAULT 0,
    FOREIGN KEY (matriculeEleve) REFERENCES ELEVE(matricule) ON DELETE SET NULL
);

CREATE TABLE FRAIS_SCOLARITE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    matriculeEleve VARCHAR(20) NOT NULL,
    libelle VARCHAR(120) NOT NULL,
    montant DECIMAL(10,2) NOT NULL DEFAULT 0,
    dateEcheance DATE NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    datePaiement DATE NULL,
    commentaire VARCHAR(255),
    FOREIGN KEY (matriculeEleve) REFERENCES ELEVE(matricule) ON DELETE CASCADE
);

-- =====================================================
-- Triggers for class capacity enforcement
-- =====================================================
DELIMITER //
CREATE TRIGGER trg_CheckClasseCapacity_Insert
BEFORE INSERT ON INSCRIPTION
FOR EACH ROW
BEGIN
    DECLARE current_count INT;
    DECLARE max_capacity INT;
    
    SELECT COUNT(*) INTO current_count FROM INSCRIPTION WHERE idClasse = NEW.idClasse AND statut = 'ACTIF';
    SELECT capacite INTO max_capacity FROM CLASSE WHERE idClasse = NEW.idClasse;
    
    IF current_count >= max_capacity THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La capacité maximale de la classe est dépassée (20 élèves)';
    END IF;
END //

CREATE TRIGGER trg_CheckClasseCapacity_Update
BEFORE UPDATE ON INSCRIPTION
FOR EACH ROW
BEGIN
    DECLARE current_count INT;
    DECLARE max_capacity INT;
    
    IF NEW.idClasse != OLD.idClasse THEN
        SELECT COUNT(*) INTO current_count FROM INSCRIPTION WHERE idClasse = NEW.idClasse AND statut = 'ACTIF';
        SELECT capacite INTO max_capacity FROM CLASSE WHERE idClasse = NEW.idClasse;
        
        IF current_count >= max_capacity THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La capacité maximale de la classe est dépassée (20 élèves)';
        END IF;
    END IF;
END //
DELIMITER ;

-- =====================================================
