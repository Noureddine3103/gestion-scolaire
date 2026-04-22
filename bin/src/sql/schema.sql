-- =====================================================
-- GESTION SCOLAIRE - Database Schema (MySQL / MariaDB)
-- Compatible with XAMPP (phpMyAdmin)
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TRIGGER IF EXISTS trg_check_classe_capacity;

DROP TABLE IF EXISTS NOTE;
DROP TABLE IF EXISTS INSCRIPTION;
DROP TABLE IF EXISTS CLASSE_ENSEIGNANT;
DROP TABLE IF EXISTS CLASSE;
DROP TABLE IF EXISTS NIVEAU;
DROP TABLE IF EXISTS MATIERE;
DROP TABLE IF EXISTS ENSEIGNANT;
DROP TABLE IF EXISTS ELEVE;
DROP TABLE IF EXISTS ANNEE_SCOLAIRE;
DROP TABLE IF EXISTS UTILISATEUR;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- Table: ANNEE_SCOLAIRE
-- =====================================================
CREATE TABLE ANNEE_SCOLAIRE (
    idAnnee INT PRIMARY KEY AUTO_INCREMENT,
    annee VARCHAR(9) NOT NULL UNIQUE,
    dateDebut DATE NOT NULL,
    dateFin DATE NOT NULL,
    estActive TINYINT(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: NIVEAU
-- =====================================================
CREATE TABLE NIVEAU (
    idNiveau INT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(50) NOT NULL UNIQUE,
    libelleCourt VARCHAR(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: MATIERE
-- =====================================================
CREATE TABLE MATIERE (
    code VARCHAR(10) PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE,
    coefficient DECIMAL(4,2) NOT NULL DEFAULT 1.0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: UTILISATEUR (Login credentials)
-- =====================================================
CREATE TABLE UTILISATEUR (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    idPersonne INT NOT NULL,
    estActif TINYINT(1) DEFAULT 1,
    CONSTRAINT chk_utilisateur_role CHECK (role IN ('ADMIN', 'ENSEIGNANT'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: ELEVE
-- =====================================================
CREATE TABLE ELEVE (
    matricule VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    dateNaissance DATE NOT NULL,
    sexe VARCHAR(1) NOT NULL,
    adresse VARCHAR(255),
    telephone VARCHAR(20),
    photo LONGBLOB,
    niveau VARCHAR(50) NOT NULL,
    idUtilisateur INT,
    dateInscription DATE DEFAULT (CURRENT_DATE),
    CONSTRAINT chk_eleve_sexe CHECK (sexe IN ('M', 'F'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: ENSEIGNANT
-- =====================================================
CREATE TABLE ENSEIGNANT (
    matricule VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    dateNaissance DATE NOT NULL,
    sexe VARCHAR(1) NOT NULL,
    adresse VARCHAR(255),
    telephone VARCHAR(20),
    photo LONGBLOB,
    grade VARCHAR(50) NOT NULL,
    idUtilisateur INT,
    CONSTRAINT chk_enseignant_sexe CHECK (sexe IN ('M', 'F'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: CLASSE
-- =====================================================
CREATE TABLE CLASSE (
    idClasse INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(20) NOT NULL,
    niveau VARCHAR(50) NOT NULL,
    capacite INT NOT NULL DEFAULT 20,
    nomComplet VARCHAR(100),
    idEnseignant VARCHAR(20),
    CONSTRAINT chk_classe_capacite CHECK (capacite >= 1 AND capacite <= 40),
    CONSTRAINT uq_classe_nom_niveau UNIQUE (nom, niveau),
    CONSTRAINT fk_classe_niveau FOREIGN KEY (niveau) REFERENCES NIVEAU(libelle),
    CONSTRAINT fk_classe_enseignant FOREIGN KEY (idEnseignant) REFERENCES ENSEIGNANT(matricule)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
    CONSTRAINT uq_inscription_matricule_annee UNIQUE (matricule, idAnnee),
    CONSTRAINT fk_inscription_eleve FOREIGN KEY (matricule) REFERENCES ELEVE(matricule),
    CONSTRAINT fk_inscription_annee FOREIGN KEY (idAnnee) REFERENCES ANNEE_SCOLAIRE(idAnnee),
    CONSTRAINT fk_inscription_classe FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: CLASSE_ENSEIGNANT (Enseignant-Classes relationship)
-- =====================================================
CREATE TABLE CLASSE_ENSEIGNANT (
    id INT PRIMARY KEY AUTO_INCREMENT,
    matriculeEnseignant VARCHAR(20) NOT NULL,
    idClasse INT NOT NULL,
    codeMatiere VARCHAR(10) NOT NULL,
    CONSTRAINT uq_classe_enseignant UNIQUE (matriculeEnseignant, idClasse, codeMatiere),
    CONSTRAINT fk_ce_enseignant FOREIGN KEY (matriculeEnseignant) REFERENCES ENSEIGNANT(matricule),
    CONSTRAINT fk_ce_classe FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse),
    CONSTRAINT fk_ce_matiere FOREIGN KEY (codeMatiere) REFERENCES MATIERE(code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: NOTE
-- =====================================================
CREATE TABLE NOTE (
    idNote INT PRIMARY KEY AUTO_INCREMENT,
    matricule VARCHAR(20) NOT NULL,
    idAnnee INT NOT NULL,
    idClasse INT NOT NULL,
    codeMatiere VARCHAR(10) NOT NULL,
    trimestre INT NOT NULL,
    noteDevoir DECIMAL(4,2) NULL,
    noteExamens DECIMAL(4,2) NULL,
    noteComposition DECIMAL(4,2) NULL,
    dateSaisie DATE DEFAULT (CURRENT_DATE),
    matriculeEnseignant VARCHAR(20),
    CONSTRAINT uq_note_unique UNIQUE (matricule, idAnnee, idClasse, codeMatiere, trimestre),
    CONSTRAINT chk_note_trimestre CHECK (trimestre BETWEEN 1 AND 3),
    CONSTRAINT chk_note_devoir CHECK (noteDevoir IS NULL OR (noteDevoir >= 0 AND noteDevoir <= 20)),
    CONSTRAINT chk_note_examens CHECK (noteExamens IS NULL OR (noteExamens >= 0 AND noteExamens <= 20)),
    CONSTRAINT chk_note_composition CHECK (noteComposition IS NULL OR (noteComposition >= 0 AND noteComposition <= 20)),
    CONSTRAINT fk_note_eleve FOREIGN KEY (matricule) REFERENCES ELEVE(matricule),
    CONSTRAINT fk_note_annee FOREIGN KEY (idAnnee) REFERENCES ANNEE_SCOLAIRE(idAnnee),
    CONSTRAINT fk_note_classe FOREIGN KEY (idClasse) REFERENCES CLASSE(idClasse),
    CONSTRAINT fk_note_matiere FOREIGN KEY (codeMatiere) REFERENCES MATIERE(code),
    CONSTRAINT fk_note_enseignant FOREIGN KEY (matriculeEnseignant) REFERENCES ENSEIGNANT(matricule)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Trigger for class capacity enforcement
-- =====================================================
DELIMITER $$
CREATE TRIGGER trg_check_classe_capacity
BEFORE INSERT ON INSCRIPTION
FOR EACH ROW
BEGIN
    DECLARE capaciteMax INT DEFAULT 0;
    DECLARE effectifActuel INT DEFAULT 0;

    SELECT capacite INTO capaciteMax
    FROM CLASSE
    WHERE idClasse = NEW.idClasse;

    SELECT COUNT(*) INTO effectifActuel
    FROM INSCRIPTION
    WHERE idClasse = NEW.idClasse AND statut = 'ACTIF';

    IF effectifActuel >= capaciteMax THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La capacité maximale de la classe est dépassée';
    END IF;
END$$
DELIMITER ;

-- =====================================================
-- Insert sample data
-- =====================================================
INSERT INTO NIVEAU (libelle, libelleCourt) VALUES
('1ère Année', '1A'),
('2ème Année', '2A'),
('3ème Année', '3A');

INSERT INTO MATIERE (code, libelle, coefficient) VALUES
('MATH', 'Mathématiques', 4.00),
('PHY', 'Physique', 3.00),
('FR', 'Français', 3.50),
('HG', 'Histoire-Géographie', 2.00),
('SVT', 'Sciences Naturelles', 2.50),
('ANG', 'Anglais', 2.00);

INSERT INTO ANNEE_SCOLAIRE (annee, dateDebut, dateFin, estActive) VALUES
('2025-2026', '2025-10-01', '2026-06-30', 1);

-- Default Admin user (password: admin123)
INSERT INTO UTILISATEUR (username, password, role, idPersonne, estActif) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhVu', 'ADMIN', 0, 1);
