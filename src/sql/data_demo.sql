-- =====================================================
INSERT INTO NIVEAU (libelle, libelleCourt) VALUES
('1ère Année', '1A'),
('2ème Année', '2A'),
('3ème Année', '3A');

-- INSERT INTO MATIERE (code, libelle, coefficient) VALUES
-- ('MATH', 'Mathématiques', 4.00),
-- ('PHY', 'Physique', 3.00),
-- ('FR', 'Français', 3.50),
-- ('HG', 'Histoire-Géographie', 2.00),
-- ('SVT', 'Sciences Naturelles', 2.50),
-- ('ANG', 'Anglais', 2.00);

INSERT INTO ANNEE_SCOLAIRE (annee, dateDebut, dateFin, estActive) VALUES
('2025-2026', '2025-10-01', '2026-06-30', 1);

INSERT INTO PERIODE_SCOLAIRE (libelle, type, ordreAffichage, estActive) VALUES
('Trimestre 1', 'TRIMESTRE', 1, 1),
('Trimestre 2', 'TRIMESTRE', 2, 1),
('Trimestre 3', 'TRIMESTRE', 3, 1),
('Semestre 1', 'SEMESTRE', 1, 1),
('Semestre 2', 'SEMESTRE', 2, 1);

INSERT INTO CONFIGURATION_SCOLAIRE (cleConfig, valeurConfig, categorie, descriptionConfig) VALUES
('BAREME_NOTE', '20', 'BAREME', 'Bareme general des notes'),
('COEF_DEVOIR', '1', 'COEFFICIENT', 'Coefficient des devoirs'),
('COEF_EXAMEN', '2', 'COEFFICIENT', 'Coefficient des examens'),
('COEF_COMPOSITION', '2', 'COEFFICIENT', 'Coefficient des compositions'),
('FRAIS_ANNUELS_1A', '8500', 'FRAIS', 'Frais annuels 1ere Annee'),
('FRAIS_ANNUELS_2A', '9200', 'FRAIS', 'Frais annuels 2eme Annee');

-- Default Admin user (password: admin123)
INSERT INTO UTILISATEUR (username, password, role, idPersonne, estActif) VALUES
('admin', '$2a$10$Twcbwh9rL5/TlfKe4M6nTuECKHdN9o94RXd/kWDpvDFLzuVhl1B3C', 'ADMIN', 0, 1);

INSERT INTO MATIERE (code, libelle, coefficient) VALUES
('MATH', 'Mathematiques', 4.00),
('FR', 'Francais', 3.00),
('ANG', 'Anglais', 2.00),
('PC', 'Physique Chimie', 3.00),
('SVT', 'Sciences de la vie', 2.00),
('HG', 'Histoire Geographie', 2.00);

INSERT INTO ENSEIGNANT (matricule, nom, prenom, dateNaissance, sexe, adresse, telephone, grade, idUtilisateur) VALUES
('T001', 'Bennani', 'Ahmed', '1986-03-14', 'M', 'Hay Salam, Casablanca', '0611223344', 'Professeur principal', NULL),
('T002', 'El Idrissi', 'Salma', '1990-07-19', 'F', 'Maarif, Casablanca', '0622334455', 'Professeure', NULL),
('T003', 'Alaoui', 'Youssef', '1988-11-08', 'M', 'Racine, Casablanca', '0633445566', 'Professeur', NULL);

INSERT INTO UTILISATEUR (username, password, role, idPersonne, estActif) VALUES
('ahmed.bennani', '$2a$10$Twcbwh9rL5/TlfKe4M6nTuECKHdN9o94RXd/kWDpvDFLzuVhl1B3C', 'ENSEIGNANT', 1, 1),
('salma.idrissi', '$2a$10$Twcbwh9rL5/TlfKe4M6nTuECKHdN9o94RXd/kWDpvDFLzuVhl1B3C', 'ENSEIGNANT', 2, 1),
('youssef.alaoui', '$2a$10$Twcbwh9rL5/TlfKe4M6nTuECKHdN9o94RXd/kWDpvDFLzuVhl1B3C', 'ENSEIGNANT', 3, 1);

UPDATE ENSEIGNANT SET idUtilisateur = 2 WHERE matricule = 'T001';
UPDATE ENSEIGNANT SET idUtilisateur = 3 WHERE matricule = 'T002';
UPDATE ENSEIGNANT SET idUtilisateur = 4 WHERE matricule = 'T003';

INSERT INTO CLASSE (nom, niveau, capacite, nomComplet, idEnseignant) VALUES
('A', '1ère Année', 28, '1ere Annee - A', 'T001'),
('B', '1ère Année', 28, '1ere Annee - B', 'T002'),
('A', '2ème Année', 30, '2eme Annee - A', 'T003');

INSERT INTO ELEVE (matricule, nom, prenom, dateNaissance, sexe, adresse, telephone, niveau, dateInscription, email, parentNom, parentPrenom, parentEmail, parentTelephone, parentAdresse) VALUES
('E001', 'Ait Said', 'Meryem', '2010-02-12', 'F', 'Sidi Maarouf, Casablanca', '0661001100', '1ère Année', '2025-09-04', 'meryem.aitsaid@ecole.ma', 'Ait Said', 'Khalid', 'khalid.aitsaid@gmail.com', '0670001100', 'Sidi Maarouf, Casablanca'),
('E002', 'Berrada', 'Omar', '2010-06-21', 'M', 'Bourgogne, Casablanca', '0661002200', '1ère Année', '2025-09-04', 'omar.berrada@ecole.ma', 'Berrada', 'Samira', 'samira.berrada@gmail.com', '0670002200', 'Bourgogne, Casablanca'),
('E003', 'Chraibi', 'Salma', '2010-08-03', 'F', 'Ain Sebaa, Casablanca', '0661003300', '1ère Année', '2025-09-04', 'salma.chraibi@ecole.ma', 'Chraibi', 'Hicham', 'hicham.chraibi@gmail.com', '0670003300', 'Ain Sebaa, Casablanca'),
('E004', 'Daoudi', 'Yassine', '2010-11-18', 'M', 'Oulfa, Casablanca', '0661004400', '1ère Année', '2025-09-04', 'yassine.daoudi@ecole.ma', 'Daoudi', 'Nadia', 'nadia.daoudi@gmail.com', '0670004400', 'Oulfa, Casablanca'),
('E005', 'El Fassi', 'Ines', '2009-01-27', 'F', 'Anfa, Casablanca', '0661005500', '2ème Année', '2025-09-04', 'ines.elfassi@ecole.ma', 'El Fassi', 'Rachid', 'rachid.elfassi@gmail.com', '0670005500', 'Anfa, Casablanca'),
('E006', 'Fakir', 'Ayoub', '2009-04-09', 'M', 'Mers Sultan, Casablanca', '0661006600', '2ème Année', '2025-09-04', 'ayoub.fakir@ecole.ma', 'Fakir', 'Latifa', 'latifa.fakir@gmail.com', '0670006600', 'Mers Sultan, Casablanca'),
('E007', 'Gharbi', 'Lina', '2010-03-15', 'F', 'Sidi Bernoussi, Casablanca', '0661007700', '1ère Année', '2025-09-04', 'lina.gharbi@ecole.ma', 'Gharbi', 'Adil', 'adil.gharbi@gmail.com', '0670007700', 'Sidi Bernoussi, Casablanca'),
('E008', 'Harroudi', 'Zakaria', '2009-12-01', 'M', 'California, Casablanca', '0661008800', '2ème Année', '2025-09-04', 'zakaria.harroudi@ecole.ma', 'Harroudi', 'Mina', 'mina.harroudi@gmail.com', '0670008800', 'California, Casablanca');

INSERT INTO INSCRIPTION (matricule, idAnnee, idClasse, dateInscription, statut) VALUES
('E001', 1, 1, '2025-09-04', 'ACTIF'),
('E002', 1, 1, '2025-09-04', 'ACTIF'),
('E003', 1, 2, '2025-09-04', 'ACTIF'),
('E004', 1, 2, '2025-09-04', 'ACTIF'),
('E005', 1, 3, '2025-09-04', 'ACTIF'),
('E006', 1, 3, '2025-09-04', 'ACTIF'),
('E007', 1, 1, '2025-09-04', 'ACTIF'),
('E008', 1, 3, '2025-09-04', 'ACTIF');

INSERT INTO CLASSE_ENSEIGNANT (matriculeEnseignant, idClasse, codeMatiere) VALUES
('T001', 1, 'MATH'),
('T002', 2, 'FR'),
('T003', 3, 'SVT');

INSERT INTO NOTE (matricule, idAnnee, idClasse, codeMatiere, trimestre, noteDevoir, noteExamens, noteComposition, matriculeEnseignant) VALUES
('E001', 1, 1, 'MATH', 1, 15.0, 14.0, 16.0, 'T001'),
('E001', 1, 1, 'PC', 1, 14.0, 13.5, 15.0, 'T001'),
('E001', 1, 1, 'ANG', 1, 16.0, 15.0, 15.5, 'T001'),
('E002', 1, 1, 'MATH', 1, 12.0, 11.5, 13.0, 'T001'),
('E002', 1, 1, 'PC', 1, 13.0, 12.0, 12.5, 'T001'),
('E002', 1, 1, 'ANG', 1, 14.0, 13.5, 14.5, 'T001'),
('E003', 1, 2, 'FR', 1, 15.5, 16.0, 15.0, 'T002'),
('E003', 1, 2, 'HG', 1, 14.5, 15.0, 14.0, 'T002'),
('E003', 1, 2, 'ANG', 1, 16.5, 16.0, 16.0, 'T002'),
('E004', 1, 2, 'FR', 1, 11.0, 12.0, 12.5, 'T002'),
('E004', 1, 2, 'HG', 1, 13.0, 12.5, 13.5, 'T002'),
('E004', 1, 2, 'ANG', 1, 12.0, 11.5, 12.5, 'T002'),
('E005', 1, 3, 'MATH', 1, 17.0, 16.0, 17.5, 'T003'),
('E005', 1, 3, 'SVT', 1, 16.5, 16.0, 16.0, 'T003'),
('E005', 1, 3, 'FR', 1, 15.0, 15.5, 16.0, 'T003'),
('E006', 1, 3, 'MATH', 1, 13.5, 14.0, 13.0, 'T003'),
('E006', 1, 3, 'SVT', 1, 14.0, 13.5, 14.5, 'T003'),
('E006', 1, 3, 'FR', 1, 12.5, 13.0, 12.0, 'T003'),
('E008', 1, 3, 'MATH', 1, 11.0, 12.0, 11.5, 'T003'),
('E008', 1, 3, 'SVT', 1, 12.5, 12.0, 13.0, 'T003'),
('E008', 1, 3, 'FR', 1, 13.0, 12.5, 13.5, 'T003');

INSERT INTO EMPLOI_DU_TEMPS (jour, heureDebut, heureFin, salle, idClasse, codeMatiere, matriculeEnseignant) VALUES
('Lundi', '08:00:00', '10:00:00', 'S1', 1, 'MATH', 'T001'),
('Lundi', '10:15:00', '12:00:00', 'S2', 2, 'FR', 'T002'),
('Lundi', '14:00:00', '16:00:00', 'S3', 3, 'SVT', 'T003'),
('Mardi', '08:00:00', '10:00:00', 'S1', 1, 'MATH', 'T001'),
('Mardi', '10:15:00', '12:00:00', 'S2', 2, 'FR', 'T002'),
('Mardi', '14:00:00', '16:00:00', 'S3', 3, 'SVT', 'T003'),
('Mercredi', '08:00:00', '09:30:00', 'S4', 1, 'MATH', 'T001'),
('Mercredi', '10:00:00', '11:30:00', 'S5', 2, 'FR', 'T002'),
('Jeudi', '08:00:00', '10:00:00', 'S3', 3, 'SVT', 'T003'),
('Vendredi', '09:00:00', '11:00:00', 'S1', 1, 'MATH', 'T001'),
('Vendredi', '11:15:00', '13:00:00', 'S2', 2, 'FR', 'T002'),
('Samedi', '09:00:00', '11:00:00', 'S3', 3, 'SVT', 'T003');

INSERT INTO UTILISATEUR (username, password, role, idPersonne, estActif) VALUES
('parent.diallo', '$2a$10$Twcbwh9rL5/TlfKe4M6nTuECKHdN9o94RXd/kWDpvDFLzuVhl1B3C', 'PARENT', 1, 1),
('parent.konate', '$2a$10$Twcbwh9rL5/TlfKe4M6nTuECKHdN9o94RXd/kWDpvDFLzuVhl1B3C', 'PARENT', 2, 1),
('E001', '$2a$10$Twcbwh9rL5/TlfKe4M6nTuECKHdN9o94RXd/kWDpvDFLzuVhl1B3C', 'ELEVE', 1, 1),
('E003', '$2a$10$Twcbwh9rL5/TlfKe4M6nTuECKHdN9o94RXd/kWDpvDFLzuVhl1B3C', 'ELEVE', 3, 1);

INSERT INTO PARENT (idUtilisateur, nom, prenom, telephone, profession, adresse, email) VALUES
(5, 'Diallo', 'Aminata', '0674001100', 'Ingenieure', 'Casablanca', 'parent.diallo@gmail.com'),
(6, 'Konate', 'Moussa', '0674002200', 'Commercant', 'Casablanca', 'parent.konate@gmail.com');

UPDATE ELEVE SET idUtilisateur = 7 WHERE matricule = 'E001';
UPDATE ELEVE SET idUtilisateur = 8 WHERE matricule = 'E003';

INSERT INTO PARENT_ELEVE (parentId, matriculeEleve, lienParente, autoriseRecuperation, contactUrgence, valideParAdmin) VALUES
(1, 'E001', 'MERE', 1, 1, 1),
(1, 'E002', 'TUTEUR', 1, 0, 1),
(2, 'E003', 'PERE', 1, 1, 1);

INSERT INTO CODES_INVITATION (code, matriculeEleve, lienParente, utilise, utilisePar, dateExpiration) VALUES
('INV-2026-A1B2C', 'E001', 'MERE', 1, 1, '2026-12-31 23:59:59'),
('INV-2026-D4E5F', 'E002', 'TUTEUR', 0, NULL, '2026-12-31 23:59:59'),
('INV-2026-G7H8I', 'E003', 'PERE', 1, 2, '2026-12-31 23:59:59');

INSERT INTO PRESENCE_SEANCE (idSeance, matriculeEleve, statut, remarque, justificationParent) VALUES
(1, 'E001', 'PRESENT', '', NULL),
(1, 'E002', 'RETARD', 'Arrive avec 10 minutes de retard', NULL),
(2, 'E003', 'ABSENT', 'Absence constatee', NULL),
(2, 'E004', 'PRESENT', '', NULL),
(3, 'E005', 'ABSENT', 'Certificat medical attendu', 'Rendez-vous medical'),
(3, 'E006', 'PRESENT', '', NULL);

INSERT INTO MESSAGE_INTERNE (expediteurRole, expediteurId, expediteurNom, destinataireRole, destinataireId, matriculeEleve, sujet, contenu, lu) VALUES
('ENSEIGNANT', 2, 'Salma El Idrissi', 'PARENT', 1, 'E001', 'Projet de classe', 'Merci de verifier le cahier de textes de votre enfant.', 0),
('ENSEIGNANT', 2, 'Salma El Idrissi', 'ELEVE', NULL, 'E003', 'Lecture obligatoire', 'Pensez a terminer la lecture avant vendredi.', 0),
('ADMIN', 1, 'Direction', 'PARENT', 2, 'E003', 'Reunion parents', 'La reunion parents-professeurs aura lieu jeudi a 15h.', 0),
('PARENT', 1, 'Aminata Diallo', 'ENSEIGNANT', 2, 'E001', 'Justification absence', 'Je vous confirme que l''absence de Meryem etait pour raison medicale.', 0);

INSERT INTO FRAIS_SCOLARITE (matriculeEleve, libelle, montant, dateEcheance, statut, datePaiement, commentaire) VALUES
('E001', 'Frais de scolarite 2025-2026', 8500.00, '2025-10-15', 'PAYE', '2025-10-10', 'Paiement complet'),
('E002', 'Frais de scolarite 2025-2026', 8500.00, '2025-10-15', 'EN_ATTENTE', NULL, 'Relance a prevoir'),
('E003', 'Frais de scolarite 2025-2026', 8500.00, '2025-10-15', 'PARTIEL', '2025-10-05', 'Acompte recu'),
('E005', 'Frais de scolarite 2025-2026', 9200.00, '2025-10-20', 'PAYE', '2025-10-18', 'Paiement recu');

