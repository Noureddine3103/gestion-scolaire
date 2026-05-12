package com.school.gestion.service;

import com.school.gestion.database.DatabaseConnection;
import com.school.gestion.model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SchoolService {

    public static List<Eleve> getAllEleves() {
        List<Eleve> eleves = new ArrayList<>();
        String query = "SELECT * FROM ELEVE ORDER BY nom, prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                eleves.add(extractEleve(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleves;
    }

    public static List<Eleve> getElevesByNiveau(String niveau) {
        List<Eleve> eleves = new ArrayList<>();
        String query = "SELECT * FROM ELEVE WHERE niveau = ? ORDER BY nom, prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, niveau);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                eleves.add(extractEleve(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleves;
    }

    public static List<Eleve> getElevesNonInscrits(int idAnnee, String niveau) {
        List<Eleve> eleves = new ArrayList<>();
        String query = "SELECT * FROM ELEVE e WHERE e.niveau = ? AND e.matricule NOT IN " +
                      "(SELECT i.matricule FROM INSCRIPTION i WHERE i.idAnnee = ?) ORDER BY e.nom, e.prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, niveau);
            stmt.setInt(2, idAnnee);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                eleves.add(extractEleve(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleves;
    }

    public static boolean saveEleve(Eleve eleve) {
        String query = "INSERT INTO ELEVE (matricule, nom, prenom, dateNaissance, sexe, adresse, telephone, niveau, email, parentNom, parentPrenom, parentEmail, parentTelephone, parentAdresse, photo) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, eleve.getMatricule());
            stmt.setString(2, eleve.getNom());
            stmt.setString(3, eleve.getPrenom());
            stmt.setDate(4, Date.valueOf(eleve.getDateNaissance()));
            stmt.setString(5, eleve.getSexe());
            stmt.setString(6, eleve.getAdresse());
            stmt.setString(7, eleve.getTelephone());
            stmt.setString(8, eleve.getNiveau());
            stmt.setString(9, eleve.getEmail());
            stmt.setString(10, eleve.getParentNom());
            stmt.setString(11, eleve.getParentPrenom());
            stmt.setString(12, eleve.getParentEmail());
            stmt.setString(13, eleve.getParentTelephone());
            stmt.setString(14, eleve.getParentAdresse());
            stmt.setBytes(15, eleve.getPhoto());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Enseignant> getAllEnseignants() {
        List<Enseignant> enseignants = new ArrayList<>();
        String query = "SELECT * FROM ENSEIGNANT ORDER BY nom, prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                enseignants.add(extractEnseignant(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enseignants;
    }

    public static String getEnseignantMatriculeByClasseAndMatiere(int idClasse, String codeMatiere) {
        String query = "SELECT matriculeEnseignant FROM CLASSE_ENSEIGNANT WHERE idClasse = ? AND codeMatiere = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idClasse);
            stmt.setString(2, codeMatiere);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("matriculeEnseignant");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveEnseignant(Enseignant enseignant) {
        String query = "INSERT INTO ENSEIGNANT (matricule, nom, prenom, dateNaissance, sexe, adresse, telephone, grade, photo) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, enseignant.getMatricule());
            stmt.setString(2, enseignant.getNom());
            stmt.setString(3, enseignant.getPrenom());
            stmt.setDate(4, Date.valueOf(enseignant.getDateNaissance()));
            stmt.setString(5, enseignant.getSexe());
            stmt.setString(6, enseignant.getAdresse());
            stmt.setString(7, enseignant.getTelephone());
            stmt.setString(8, enseignant.getGrade());
            stmt.setBytes(9, enseignant.getPhoto());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Classe> getClassesByNiveau(String niveau) {
        List<Classe> classes = new ArrayList<>();
        String query = "SELECT c.*, (SELECT COUNT(*) FROM INSCRIPTION i WHERE i.idClasse = c.idClasse AND i.statut = 'ACTIF') as effectif " +
                      "FROM CLASSE c ORDER BY c.niveau, c.nom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            String wanted = cleanLabel(niveau);
            while (rs.next()) {
                Classe classe = extractClasse(rs);
                if (wanted == null || wanted.equalsIgnoreCase(classe.getNiveau())) {
                    classe.setEffectifActuel(rs.getInt("effectif"));
                    classes.add(classe);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static List<Classe> getAllClasses() {
        List<Classe> classes = new ArrayList<>();
        String query = "SELECT c.*, (SELECT COUNT(*) FROM INSCRIPTION i WHERE i.idClasse = c.idClasse AND i.statut = 'ACTIF') as effectif " +
                      "FROM CLASSE c ORDER BY c.niveau, c.nom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Classe classe = extractClasse(rs);
                classe.setEffectifActuel(rs.getInt("effectif"));
                classes.add(classe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static InvitationCode saveEleveAndGenerateInvitation(Eleve eleve, String lienParente, LocalDate expirationDate) {
        if (!saveEleve(eleve)) {
            return null;
        }
        return generateInvitationCode(eleve.getMatricule(), lienParente, expirationDate);
    }

    public static List<Classe> getClassesAvailableForLevel(String niveau, int excludeClasseId) {
        List<Classe> classes = new ArrayList<>();
        String query = "SELECT c.*, (SELECT COUNT(*) FROM INSCRIPTION i WHERE i.idClasse = c.idClasse AND i.statut = 'ACTIF') as effectif " +
                      "FROM CLASSE c WHERE c.niveau = ? AND c.idClasse <> ? ORDER BY c.nom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, niveau);
            stmt.setInt(2, excludeClasseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Classe classe = extractClasse(rs);
                classe.setEffectifActuel(rs.getInt("effectif"));
                classes.add(classe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static List<Inscription> getActiveInscriptionsByClasse(int idClasse, int idAnnee) {
        List<Inscription> inscriptions = new ArrayList<>();
        String query = "SELECT i.*, e.nom, e.prenom, e.niveau FROM INSCRIPTION i " +
                      "JOIN ELEVE e ON e.matricule = i.matricule " +
                      "WHERE i.idClasse = ? AND i.idAnnee = ? AND i.statut = 'ACTIF' " +
                      "ORDER BY e.nom, e.prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idClasse);
            stmt.setInt(2, idAnnee);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Inscription inscription = extractInscription(rs);
                Eleve eleve = new Eleve();
                eleve.setMatricule(rs.getString("matricule"));
                eleve.setNom(rs.getString("nom"));
                eleve.setPrenom(rs.getString("prenom"));
                eleve.setNiveau(rs.getString("niveau"));
                inscription.setEleve(eleve);
                inscriptions.add(inscription);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inscriptions;
    }

    public static boolean transferEleveToClasse(String matricule, int idAnnee, int sourceClasseId, int targetClasseId) {
        String countQuery = "SELECT COUNT(*) FROM INSCRIPTION WHERE idClasse = ? AND statut = 'ACTIF'";
        String updateQuery = "UPDATE INSCRIPTION SET idClasse = ? WHERE matricule = ? AND idAnnee = ? AND idClasse = ? AND statut = 'ACTIF'";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement countStmt = conn.prepareStatement(countQuery)) {
                countStmt.setInt(1, targetClasseId);
                ResultSet rs = countStmt.executeQuery();
                if (rs.next() && rs.getInt(1) >= 20) {
                    return false;
                }
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, targetClasseId);
                updateStmt.setString(2, matricule);
                updateStmt.setInt(3, idAnnee);
                updateStmt.setInt(4, sourceClasseId);
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateEnseignant(Enseignant enseignant, String originalMatricule) {
        String query = "UPDATE ENSEIGNANT SET matricule = ?, nom = ?, prenom = ?, dateNaissance = ?, sexe = ?, adresse = ?, telephone = ?, grade = ?, photo = COALESCE(?, photo) " +
                      "WHERE matricule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, enseignant.getMatricule());
            stmt.setString(2, enseignant.getNom());
            stmt.setString(3, enseignant.getPrenom());
            stmt.setDate(4, Date.valueOf(enseignant.getDateNaissance()));
            stmt.setString(5, enseignant.getSexe());
            stmt.setString(6, enseignant.getAdresse());
            stmt.setString(7, enseignant.getTelephone());
            stmt.setString(8, enseignant.getGrade());
            stmt.setBytes(9, enseignant.getPhoto());
            stmt.setString(10, originalMatricule);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteEnseignant(String matricule) {
        String query = "DELETE FROM ENSEIGNANT WHERE matricule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matricule);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateEleve(Eleve eleve, String originalMatricule) {
        String query = "UPDATE ELEVE SET matricule = ?, nom = ?, prenom = ?, dateNaissance = ?, sexe = ?, adresse = ?, telephone = ?, niveau = ?, email = ?, parentNom = ?, parentPrenom = ?, parentEmail = ?, parentTelephone = ?, parentAdresse = ?, photo = COALESCE(?, photo) " +
                      "WHERE matricule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, eleve.getMatricule());
            stmt.setString(2, eleve.getNom());
            stmt.setString(3, eleve.getPrenom());
            stmt.setDate(4, Date.valueOf(eleve.getDateNaissance()));
            stmt.setString(5, eleve.getSexe());
            stmt.setString(6, eleve.getAdresse());
            stmt.setString(7, eleve.getTelephone());
            stmt.setString(8, eleve.getNiveau());
            stmt.setString(9, eleve.getEmail());
            stmt.setString(10, eleve.getParentNom());
            stmt.setString(11, eleve.getParentPrenom());
            stmt.setString(12, eleve.getParentEmail());
            stmt.setString(13, eleve.getParentTelephone());
            stmt.setString(14, eleve.getParentAdresse());
            stmt.setBytes(15, eleve.getPhoto());
            stmt.setString(16, originalMatricule);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteEleve(String matricule) {
        String query = "DELETE FROM ELEVE WHERE matricule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matricule);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean swapElevesBetweenClasses(String matriculeA, int classeA, String matriculeB, int classeB, int idAnnee) {
        String tempStatus = "PERMUTATION_TEMP";
        String markTempQuery = "UPDATE INSCRIPTION SET statut = ? WHERE matricule = ? AND idAnnee = ? AND idClasse = ? AND statut = 'ACTIF'";
        String moveQuery = "UPDATE INSCRIPTION SET idClasse = ? WHERE matricule = ? AND idAnnee = ? AND idClasse = ?";
        String restoreQuery = "UPDATE INSCRIPTION SET statut = 'ACTIF' WHERE matricule = ? AND idAnnee = ? AND idClasse = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (
                PreparedStatement markTempStmt = conn.prepareStatement(markTempQuery);
                PreparedStatement moveStmt = conn.prepareStatement(moveQuery);
                PreparedStatement restoreStmt = conn.prepareStatement(restoreQuery)
            ) {
                markTempStmt.setString(1, tempStatus);
                markTempStmt.setString(2, matriculeB);
                markTempStmt.setInt(3, idAnnee);
                markTempStmt.setInt(4, classeB);
                if (markTempStmt.executeUpdate() == 0) {
                    conn.rollback();
                    conn.setAutoCommit(oldAutoCommit);
                    return false;
                }

                moveStmt.setInt(1, classeB);
                moveStmt.setString(2, matriculeA);
                moveStmt.setInt(3, idAnnee);
                moveStmt.setInt(4, classeA);
                if (moveStmt.executeUpdate() == 0) {
                    conn.rollback();
                    conn.setAutoCommit(oldAutoCommit);
                    return false;
                }

                moveStmt.setInt(1, classeA);
                moveStmt.setString(2, matriculeB);
                moveStmt.setInt(3, idAnnee);
                moveStmt.setInt(4, classeB);
                if (moveStmt.executeUpdate() == 0) {
                    conn.rollback();
                    conn.setAutoCommit(oldAutoCommit);
                    return false;
                }

                restoreStmt.setString(1, matriculeB);
                restoreStmt.setInt(2, idAnnee);
                restoreStmt.setInt(3, classeA);
                if (restoreStmt.executeUpdate() == 0) {
                    conn.rollback();
                    conn.setAutoCommit(oldAutoCommit);
                    return false;
                }

                conn.commit();
                conn.setAutoCommit(oldAutoCommit);
                return true;
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(oldAutoCommit);
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveClasse(Classe classe) {
        String query = "INSERT INTO CLASSE (nom, niveau, capacite, nomComplet) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, classe.getNom());
            stmt.setString(2, classe.getNiveau());
            stmt.setInt(3, classe.getCapacite());
            stmt.setString(4, classe.getNomComplet());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateClasse(Classe classe) {
        String query = "UPDATE CLASSE SET nom = ?, niveau = ?, capacite = ?, nomComplet = ? WHERE idClasse = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, classe.getNom());
            stmt.setString(2, classe.getNiveau());
            stmt.setInt(3, classe.getCapacite());
            stmt.setString(4, classe.getNomComplet());
            stmt.setInt(5, classe.getIdClasse());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteClasse(int idClasse) {
        String query = "DELETE FROM CLASSE WHERE idClasse = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idClasse);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean inscription(String matricule, int idAnnee, int idClasse) {
        String checkQuery = "SELECT COUNT(*) as count FROM INSCRIPTION WHERE idClasse = ? AND statut = 'ACTIF'";
        String insertQuery = "INSERT INTO INSCRIPTION (matricule, idAnnee, idClasse) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, idClasse);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("count") >= 20) {
                return false;
            }
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, matricule);
            insertStmt.setInt(2, idAnnee);
            insertStmt.setInt(3, idClasse);
            return insertStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Matiere> getAllMatieres() {
        List<Matiere> matieres = new ArrayList<>();
        String query = "SELECT * FROM MATIERE ORDER BY libelle";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Matiere m = new Matiere();
                m.setCode(rs.getString("code"));
                m.setLibelle(rs.getString("libelle"));
                m.setCoefficient(rs.getDouble("coefficient"));
                matieres.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matieres;
    }

    public static boolean saveMatiere(Matiere matiere) {
        String query = "INSERT INTO MATIERE (code, libelle, coefficient) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matiere.getCode());
            stmt.setString(2, matiere.getLibelle());
            stmt.setDouble(3, matiere.getCoefficient());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateMatiere(Matiere matiere, String originalCode) {
        String query = "UPDATE MATIERE SET code = ?, libelle = ?, coefficient = ? WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matiere.getCode());
            stmt.setString(2, matiere.getLibelle());
            stmt.setDouble(3, matiere.getCoefficient());
            stmt.setString(4, originalCode);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteMatiere(String code) {
        String query = "DELETE FROM MATIERE WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getAllNiveaux() {
        List<String> niveaux = new ArrayList<>();
        String query = "SELECT libelle FROM NIVEAU ORDER BY libelle";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                niveaux.add(cleanLabel(rs.getString("libelle")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return niveaux;
    }

    private static String cleanLabel(String value) {
        if (value == null) {
            return null;
        }
        return value
            .replace("1??re Ann??e", "1ere Annee")
            .replace("2??me Ann??e", "2eme Annee")
            .replace("3??me Ann??e", "3eme Annee")
            .replace("1Ã¨re AnnÃ©e", "1ere Annee")
            .replace("2Ã¨me AnnÃ©e", "2eme Annee")
            .replace("3Ã¨me AnnÃ©e", "3eme Annee")
            .replace("1ÃƒÂ¨re AnnÃƒÂ©e", "1ere Annee")
            .replace("2ÃƒÂ¨me AnnÃƒÂ©e", "2eme Annee")
            .replace("3ÃƒÂ¨me AnnÃƒÂ©e", "3eme Annee")
            .replace("Ann??e", "Annee")
            .replace("AnnÃ©e", "Annee")
            .replace("Ã¨", "e")
            .replace("Ã©", "e");
    }

    public static List<Seance> getAllSeances() {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT edt.*, c.nomComplet, m.libelle, e.nom, e.prenom " +
            "FROM EMPLOI_DU_TEMPS edt " +
            "JOIN CLASSE c ON c.idClasse = edt.idClasse " +
            "JOIN MATIERE m ON m.code = edt.codeMatiere " +
            "JOIN ENSEIGNANT e ON e.matricule = edt.matriculeEnseignant " +
            "ORDER BY FIELD(edt.jour,'Lundi','Mardi','Mercredi','Jeudi','Vendredi','Samedi'), edt.heureDebut";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                seances.add(extractSeance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    public static List<Seance> getSeancesByClasse(int idClasse) {
        return getSeances("WHERE edt.idClasse = ?", idClasse, null);
    }

    public static List<Seance> getSeancesByEnseignant(String matriculeEnseignant) {
        return getSeances("WHERE edt.matriculeEnseignant = ?", null, matriculeEnseignant);
    }

    private static List<Seance> getSeances(String whereClause, Integer idClasse, String matriculeEnseignant) {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT edt.*, c.nomComplet, m.libelle, e.nom, e.prenom " +
            "FROM EMPLOI_DU_TEMPS edt " +
            "JOIN CLASSE c ON c.idClasse = edt.idClasse " +
            "JOIN MATIERE m ON m.code = edt.codeMatiere " +
            "JOIN ENSEIGNANT e ON e.matricule = edt.matriculeEnseignant " +
            whereClause + " ORDER BY FIELD(edt.jour,'Lundi','Mardi','Mercredi','Jeudi','Vendredi','Samedi'), edt.heureDebut";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (idClasse != null) {
                stmt.setInt(1, idClasse);
            } else if (matriculeEnseignant != null) {
                stmt.setString(1, matriculeEnseignant);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                seances.add(extractSeance(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    public static boolean saveSeance(Seance seance) {
        String query = "INSERT INTO EMPLOI_DU_TEMPS (jour, heureDebut, heureFin, salle, idClasse, codeMatiere, matriculeEnseignant) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, seance.getJour());
            stmt.setTime(2, Time.valueOf(seance.getHeureDebut()));
            stmt.setTime(3, Time.valueOf(seance.getHeureFin()));
            stmt.setString(4, seance.getSalle());
            stmt.setInt(5, seance.getIdClasse());
            stmt.setString(6, seance.getCodeMatiere());
            stmt.setString(7, seance.getMatriculeEnseignant());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateSeance(Seance seance) {
        String query = "UPDATE EMPLOI_DU_TEMPS SET jour = ?, heureDebut = ?, heureFin = ?, salle = ?, idClasse = ?, codeMatiere = ?, matriculeEnseignant = ? " +
            "WHERE idSeance = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, seance.getJour());
            stmt.setTime(2, Time.valueOf(seance.getHeureDebut()));
            stmt.setTime(3, Time.valueOf(seance.getHeureFin()));
            stmt.setString(4, seance.getSalle());
            stmt.setInt(5, seance.getIdClasse());
            stmt.setString(6, seance.getCodeMatiere());
            stmt.setString(7, seance.getMatriculeEnseignant());
            stmt.setInt(8, seance.getIdSeance());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteSeance(int idSeance) {
        String query = "DELETE FROM EMPLOI_DU_TEMPS WHERE idSeance = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idSeance);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean saveNiveau(String libelle, String libelleCourt) {
        String query = "INSERT INTO NIVEAU (libelle, libelleCourt) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, libelle);
            stmt.setString(2, libelleCourt);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static AnneeScolaire getActiveAnneeScolaire() {
        String query = "SELECT * FROM ANNEE_SCOLAIRE WHERE estActive = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                AnneeScolaire a = new AnneeScolaire();
                a.setIdAnnee(rs.getInt("idAnnee"));
                a.setAnnee(rs.getString("annee"));
                a.setDateDebut(rs.getDate("dateDebut").toLocalDate());
                a.setDateFin(rs.getDate("dateFin").toLocalDate());
                a.setEstActive(rs.getBoolean("estActive"));
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Note> getNotesByClasseMatiereTrimestre(int idClasse, String codeMatiere, int trimestre, int idAnnee) {
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM NOTE WHERE idClasse = ? AND codeMatiere = ? AND trimestre = ? AND idAnnee = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idClasse);
            stmt.setString(2, codeMatiere);
            stmt.setInt(3, trimestre);
            stmt.setInt(4, idAnnee);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(extractNote(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

public static boolean saveNote(Note note) {
        String query = "INSERT INTO NOTE (matricule, idAnnee, idClasse, codeMatiere, trimestre, noteDevoir, noteExamens, noteComposition, matriculeEnseignant) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE noteDevoir = VALUES(noteDevoir), noteExamens = VALUES(noteExamens), noteComposition = VALUES(noteComposition), matriculeEnseignant = VALUES(matriculeEnseignant), dateSaisie = CURRENT_DATE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, note.getMatricule());
            stmt.setInt(2, note.getIdAnnee());
            stmt.setInt(3, note.getIdClasse());
            stmt.setString(4, note.getCodeMatiere());
            stmt.setInt(5, note.getTrimestre());
            stmt.setDouble(6, note.getNoteDevoir() != null ? note.getNoteDevoir() : 0);
            stmt.setDouble(7, note.getNoteExamens() != null ? note.getNoteExamens() : 0);
            stmt.setDouble(8, note.getNoteComposition() != null ? note.getNoteComposition() : 0);
            stmt.setString(9, note.getMatriculeEnseignant());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Eleve extractEleve(ResultSet rs) throws SQLException {
        Eleve e = new Eleve();
        e.setMatricule(rs.getString("matricule"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        Date dob = rs.getDate("dateNaissance");
        e.setDateNaissance(dob != null ? dob.toLocalDate() : null);
        e.setSexe(rs.getString("sexe"));
        e.setAdresse(rs.getString("adresse"));
        e.setTelephone(rs.getString("telephone"));
        e.setNiveau(rs.getString("niveau"));
        e.setEmail(rs.getString("email"));
        e.setParentNom(rs.getString("parentNom"));
        e.setParentPrenom(rs.getString("parentPrenom"));
        e.setParentEmail(rs.getString("parentEmail"));
        e.setParentTelephone(rs.getString("parentTelephone"));
        e.setParentAdresse(rs.getString("parentAdresse"));
        e.setPhoto(rs.getBytes("photo"));
        return e;
    }

    private static Enseignant extractEnseignant(ResultSet rs) throws SQLException {
        Enseignant en = new Enseignant();
        en.setMatricule(rs.getString("matricule"));
        en.setNom(rs.getString("nom"));
        en.setPrenom(rs.getString("prenom"));
        Date dob = rs.getDate("dateNaissance");
        en.setDateNaissance(dob != null ? dob.toLocalDate() : null);
        en.setSexe(rs.getString("sexe"));
        en.setAdresse(rs.getString("adresse"));
        en.setTelephone(rs.getString("telephone"));
        en.setPhoto(rs.getBytes("photo"));
        en.setGrade(rs.getString("grade"));
        return en;
    }

    private static Parent extractParent(ResultSet rs) throws SQLException {
        Parent parent = new Parent();
        parent.setIdParent(rs.getInt("idParent"));
        parent.setIdUtilisateur((Integer) rs.getObject("idUtilisateur"));
        parent.setNom(rs.getString("nom"));
        parent.setPrenom(rs.getString("prenom"));
        parent.setTelephone(rs.getString("telephone"));
        parent.setProfession(rs.getString("profession"));
        parent.setAdresse(rs.getString("adresse"));
        try {
            parent.setEmail(rs.getString("email"));
        } catch (SQLException ignored) {
            parent.setEmail(null);
        }
        return parent;
    }

    private static Classe extractClasse(ResultSet rs) throws SQLException {
        Classe c = new Classe();
        c.setIdClasse(rs.getInt("idClasse"));
        c.setNom(rs.getString("nom"));
        c.setNiveau(rs.getString("niveau"));
        c.setCapacite(rs.getInt("capacite"));
        c.setNomComplet(rs.getString("nomComplet"));
        c.setMatriculeEnseignant(rs.getString("idEnseignant"));
        return c;
    }

    private static Note extractNote(ResultSet rs) throws SQLException {
        Note n = new Note();
        n.setIdNote(rs.getInt("idNote"));
        n.setMatricule(rs.getString("matricule"));
        n.setIdAnnee(rs.getInt("idAnnee"));
        n.setIdClasse(rs.getInt("idClasse"));
        n.setCodeMatiere(rs.getString("codeMatiere"));
        n.setTrimestre(rs.getInt("trimestre"));
        double devoir = rs.getDouble("noteDevoir");
        n.setNoteDevoir(rs.wasNull() ? null : devoir);
        double exam = rs.getDouble("noteExamens");
        n.setNoteExamens(rs.wasNull() ? null : exam);
        double comp = rs.getDouble("noteComposition");
        n.setNoteComposition(rs.wasNull() ? null : comp);
        n.setMatriculeEnseignant(rs.getString("matriculeEnseignant"));
        return n;
    }

    private static Seance extractSeance(ResultSet rs) throws SQLException {
        Seance seance = new Seance();
        seance.setIdSeance(rs.getInt("idSeance"));
        seance.setJour(rs.getString("jour"));
        Time start = rs.getTime("heureDebut");
        Time end = rs.getTime("heureFin");
        seance.setHeureDebut(start != null ? start.toLocalTime() : LocalTime.of(8, 0));
        seance.setHeureFin(end != null ? end.toLocalTime() : LocalTime.of(9, 0));
        seance.setSalle(rs.getString("salle"));
        seance.setIdClasse(rs.getInt("idClasse"));
        seance.setClasseNom(rs.getString("nomComplet"));
        seance.setCodeMatiere(rs.getString("codeMatiere"));
        seance.setMatiereLibelle(rs.getString("libelle"));
        seance.setMatriculeEnseignant(rs.getString("matriculeEnseignant"));
        seance.setEnseignantNom(rs.getString("prenom") + " " + rs.getString("nom"));
        return seance;
    }

    public static List<Note> getNotesByClasseAndTrimestre(int idClasse, int trimestre) {
        List<Note> notes = new ArrayList<>();
        AnneeScolaire annee = getActiveAnneeScolaire();
        if (annee == null) return notes;
        
        String query = "SELECT * FROM note WHERE idClasse = ? AND trimestre = ? AND idAnnee = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idClasse);
            stmt.setInt(2, trimestre);
            stmt.setInt(3, annee.getIdAnnee());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(extractNote(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public static List<Eleve> getElevesByClasse(int idClasse) {
        List<Eleve> eleves = new ArrayList<>();
        AnneeScolaire annee = getActiveAnneeScolaire();
        if (annee == null) return eleves;
        
        String query = "SELECT e.* FROM eleve e " +
                       "JOIN inscription i ON e.matricule = i.matricule " +
                       "WHERE i.idClasse = ? AND i.idAnnee = ? AND i.statut = 'ACTIF'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idClasse);
            stmt.setInt(2, annee.getIdAnnee());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eleves.add(extractEleve(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleves;
    }

    public static String generateNextStudentMatricule() {
        int year = LocalDate.now().getYear();
        String prefix = "ELV-" + year + "-";
        String query = "SELECT matricule FROM ELEVE WHERE matricule LIKE ? ORDER BY matricule DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String last = rs.getString("matricule");
                int number = Integer.parseInt(last.substring(last.lastIndexOf('-') + 1));
                return prefix + String.format(Locale.ROOT, "%04d", number + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prefix + "0001";
    }

    public static int getTeacherClassCount(String teacherMatricule) {
        return countForTeacher("SELECT COUNT(DISTINCT idClasse) FROM CLASSE_ENSEIGNANT WHERE matriculeEnseignant = ?", teacherMatricule);
    }

    public static int getTeacherMatiereCount(String teacherMatricule) {
        return countForTeacher("SELECT COUNT(DISTINCT codeMatiere) FROM CLASSE_ENSEIGNANT WHERE matriculeEnseignant = ?", teacherMatricule);
    }

    public static int getTeacherStudentCount(String teacherMatricule, int idAnnee) {
        String query = "SELECT COUNT(DISTINCT i.matricule) FROM INSCRIPTION i " +
                      "JOIN CLASSE_ENSEIGNANT ce ON ce.idClasse = i.idClasse " +
                      "WHERE ce.matriculeEnseignant = ? AND i.idAnnee = ? AND i.statut = 'ACTIF'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, teacherMatricule);
            stmt.setInt(2, idAnnee);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Classe> getTeacherClasses(String teacherMatricule) {
        List<Classe> classes = new ArrayList<>();
        String query = "SELECT c.*, (SELECT COUNT(*) FROM INSCRIPTION i WHERE i.idClasse = c.idClasse AND i.statut = 'ACTIF') as effectif " +
                      "FROM CLASSE c JOIN CLASSE_ENSEIGNANT ce ON ce.idClasse = c.idClasse " +
                      "WHERE ce.matriculeEnseignant = ? GROUP BY c.idClasse ORDER BY c.niveau, c.nom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, teacherMatricule);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Classe classe = extractClasse(rs);
                classe.setEffectifActuel(rs.getInt("effectif"));
                classes.add(classe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static List<Matiere> getTeacherMatieres(String teacherMatricule) {
        List<Matiere> matieres = new ArrayList<>();
        String query = "SELECT DISTINCT m.code, m.libelle, m.coefficient FROM MATIERE m " +
                      "JOIN CLASSE_ENSEIGNANT ce ON ce.codeMatiere = m.code " +
                      "WHERE ce.matriculeEnseignant = ? ORDER BY m.libelle LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, teacherMatricule);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Matiere m = new Matiere();
                m.setCode(rs.getString("code"));
                m.setLibelle(rs.getString("libelle"));
                m.setCoefficient(rs.getDouble("coefficient"));
                matieres.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matieres;
    }

    public static String resolveTeacherMatricule(int userId, String username) {
        String[] queries = new String[] {
            "SELECT matricule FROM ENSEIGNANT WHERE idUtilisateur = ?",
            "SELECT matricule FROM ENSEIGNANT WHERE matricule = ?"
        };
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(queries[0])) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("matricule");
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(queries[1])) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("matricule");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "T001";
    }

    public static Parent getParentByUserId(int userId) {
        String query = "SELECT * FROM PARENT WHERE idUtilisateur = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractParent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Eleve> getElevesByParent(int parentId) {
        List<Eleve> eleves = new ArrayList<>();
        String query = "SELECT e.* FROM ELEVE e " +
            "JOIN PARENT_ELEVE pe ON pe.matriculeEleve = e.matricule " +
            "WHERE pe.parentId = ? AND pe.valideParAdmin = 1 ORDER BY e.nom, e.prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                eleves.add(extractEleve(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleves;
    }

    public static List<InvitationCode> getAllInvitationCodes() {
        List<InvitationCode> codes = new ArrayList<>();
        String query = "SELECT ci.*, e.nom, e.prenom FROM CODES_INVITATION ci " +
            "JOIN ELEVE e ON e.matricule = ci.matriculeEleve " +
            "ORDER BY ci.dateCreation DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                codes.add(extractInvitationCode(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codes;
    }

    public static List<ParentEleveLink> getAllParentEleveLinks() {
        List<ParentEleveLink> links = new ArrayList<>();
        String query = "SELECT pe.*, p.nom AS parentNom, p.prenom AS parentPrenom, p.email AS parentEmail, " +
            "e.nom AS eleveNom, e.prenom AS elevePrenom " +
            "FROM PARENT_ELEVE pe " +
            "JOIN PARENT p ON p.idParent = pe.parentId " +
            "JOIN ELEVE e ON e.matricule = pe.matriculeEleve " +
            "ORDER BY pe.valideParAdmin ASC, pe.dateLiaison DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                links.add(extractParentEleveLink(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return links;
    }

    public static List<ParentEleveLink> getPendingParentEleveLinks() {
        List<ParentEleveLink> links = new ArrayList<>();
        String query = "SELECT pe.*, p.nom AS parentNom, p.prenom AS parentPrenom, p.email AS parentEmail, " +
            "e.nom AS eleveNom, e.prenom AS elevePrenom " +
            "FROM PARENT_ELEVE pe " +
            "JOIN PARENT p ON p.idParent = pe.parentId " +
            "JOIN ELEVE e ON e.matricule = pe.matriculeEleve " +
            "WHERE pe.valideParAdmin = 0 ORDER BY pe.dateLiaison DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                links.add(extractParentEleveLink(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return links;
    }

    public static boolean validateParentEleveLink(int linkId) {
        String query = "UPDATE PARENT_ELEVE SET valideParAdmin = 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, linkId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean rejectParentEleveLink(int linkId) {
        String query = "DELETE FROM PARENT_ELEVE WHERE id = ? AND valideParAdmin = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, linkId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static InvitationCode generateInvitationCode(String matriculeEleve, String lienParente, LocalDate expirationDate) {
        String query = "INSERT INTO CODES_INVITATION (code, matriculeEleve, lienParente, utilise, dateExpiration) VALUES (?, ?, ?, 0, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            String code = buildInvitationCode();
            stmt.setString(1, code);
            stmt.setString(2, matriculeEleve);
            stmt.setString(3, lienParente);
            if (expirationDate != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(expirationDate.atTime(23, 59, 59)));
            } else {
                stmt.setTimestamp(4, null);
            }
            if (stmt.executeUpdate() > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                InvitationCode invitationCode = new InvitationCode();
                if (keys.next()) {
                    invitationCode.setId(keys.getInt(1));
                }
                invitationCode.setCode(code);
                invitationCode.setMatriculeEleve(matriculeEleve);
                Eleve eleve = getEleveByMatricule(matriculeEleve);
                invitationCode.setEleveNomComplet(eleve != null ? eleve.getNomComplet() : matriculeEleve);
                invitationCode.setLienParente(lienParente);
                invitationCode.setUtilise(false);
                invitationCode.setDateCreation(LocalDateTime.now());
                invitationCode.setDateExpiration(expirationDate != null ? expirationDate.atTime(23, 59, 59) : null);
                return invitationCode;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Utilisateur> getAllUtilisateurs() {
        List<Utilisateur> users = new ArrayList<>();
        String query = "SELECT * FROM UTILISATEUR ORDER BY role, username";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(extractUtilisateur(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static boolean saveUtilisateur(Utilisateur utilisateur, String plainPassword) {
        String query = "INSERT INTO UTILISATEUR (username, password, role, idPersonne, estActif) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, utilisateur.getUsername());
            stmt.setString(2, BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
            stmt.setString(3, utilisateur.getRole());
            stmt.setInt(4, utilisateur.getIdPersonne());
            stmt.setBoolean(5, utilisateur.isEstActif());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean toggleUtilisateur(int id, boolean actif) {
        String query = "UPDATE UTILISATEUR SET estActif = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, actif);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean resetUtilisateurPassword(int id, String plainPassword) {
        String query = "UPDATE UTILISATEUR SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateOwnPassword(int userId, String plainPassword) {
        return resetUtilisateurPassword(userId, plainPassword);
    }

    public static List<Parent> getAllParents() {
        List<Parent> parents = new ArrayList<>();
        String query = "SELECT * FROM PARENT ORDER BY nom, prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                parents.add(extractParent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parents;
    }

    public static boolean saveParent(Parent parent) {
        String query = "INSERT INTO PARENT (idUtilisateur, nom, prenom, telephone, profession, adresse, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (parent.getIdUtilisateur() == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, parent.getIdUtilisateur());
            }
            stmt.setString(2, parent.getNom());
            stmt.setString(3, parent.getPrenom());
            stmt.setString(4, parent.getTelephone());
            stmt.setString(5, parent.getProfession());
            stmt.setString(6, parent.getAdresse());
            stmt.setString(7, parent.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateParent(Parent parent) {
        String query = "UPDATE PARENT SET nom = ?, prenom = ?, telephone = ?, profession = ?, adresse = ?, email = ?, idUtilisateur = ? WHERE idParent = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, parent.getNom());
            stmt.setString(2, parent.getPrenom());
            stmt.setString(3, parent.getTelephone());
            stmt.setString(4, parent.getProfession());
            stmt.setString(5, parent.getAdresse());
            stmt.setString(6, parent.getEmail());
            if (parent.getIdUtilisateur() == null) {
                stmt.setNull(7, Types.INTEGER);
            } else {
                stmt.setInt(7, parent.getIdUtilisateur());
            }
            stmt.setInt(8, parent.getIdParent());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteParent(int idParent) {
        String query = "DELETE FROM PARENT WHERE idParent = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idParent);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<AnneeScolaire> getAllAnneesScolaires() {
        List<AnneeScolaire> annees = new ArrayList<>();
        String query = "SELECT * FROM ANNEE_SCOLAIRE ORDER BY dateDebut DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                AnneeScolaire a = new AnneeScolaire();
                a.setIdAnnee(rs.getInt("idAnnee"));
                a.setAnnee(rs.getString("annee"));
                Date debut = rs.getDate("dateDebut");
                Date fin = rs.getDate("dateFin");
                a.setDateDebut(debut != null ? debut.toLocalDate() : null);
                a.setDateFin(fin != null ? fin.toLocalDate() : null);
                a.setEstActive(rs.getBoolean("estActive"));
                annees.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return annees;
    }

    public static boolean saveAnneeScolaire(AnneeScolaire annee) {
        String query = "INSERT INTO ANNEE_SCOLAIRE (annee, dateDebut, dateFin, estActive) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, annee.getAnnee());
            stmt.setDate(2, Date.valueOf(annee.getDateDebut()));
            stmt.setDate(3, Date.valueOf(annee.getDateFin()));
            stmt.setBoolean(4, annee.isEstActive());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setAnneeActive(int idAnnee) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             PreparedStatement activate = conn.prepareStatement("UPDATE ANNEE_SCOLAIRE SET estActive = 1 WHERE idAnnee = ?")) {
            stmt.executeUpdate("UPDATE ANNEE_SCOLAIRE SET estActive = 0");
            activate.setInt(1, idAnnee);
            return activate.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<AffectationEnseignement> getAllAffectations() {
        List<AffectationEnseignement> affectations = new ArrayList<>();
        String query =
            "SELECT ce.id, ce.matriculeEnseignant, ce.idClasse, ce.codeMatiere, " +
            "e.nom, e.prenom, c.nomComplet, m.libelle " +
            "FROM CLASSE_ENSEIGNANT ce " +
            "JOIN ENSEIGNANT e ON e.matricule = ce.matriculeEnseignant " +
            "JOIN CLASSE c ON c.idClasse = ce.idClasse " +
            "JOIN MATIERE m ON m.code = ce.codeMatiere " +
            "ORDER BY c.niveau, c.nom, e.nom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                AffectationEnseignement affectation = new AffectationEnseignement();
                affectation.setId(rs.getInt("id"));
                affectation.setMatriculeEnseignant(rs.getString("matriculeEnseignant"));
                affectation.setEnseignantNomComplet((rs.getString("prenom") + " " + rs.getString("nom")).trim());
                affectation.setIdClasse(rs.getInt("idClasse"));
                affectation.setClasseNom(rs.getString("nomComplet"));
                affectation.setCodeMatiere(rs.getString("codeMatiere"));
                affectation.setMatiereLibelle(rs.getString("libelle"));
                affectations.add(affectation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectations;
    }

    public static boolean saveAffectation(String matriculeEnseignant, int idClasse, String codeMatiere) {
        String checkSubject =
            "SELECT DISTINCT codeMatiere FROM CLASSE_ENSEIGNANT WHERE matriculeEnseignant = ? AND codeMatiere <> ? LIMIT 1";
        String query = "INSERT INTO CLASSE_ENSEIGNANT (matriculeEnseignant, idClasse, codeMatiere) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSubject);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            checkStmt.setString(1, matriculeEnseignant);
            checkStmt.setString(2, codeMatiere);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
            }
            stmt.setString(1, matriculeEnseignant);
            stmt.setInt(2, idClasse);
            stmt.setString(3, codeMatiere);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteAffectation(int id) {
        String query = "DELETE FROM CLASSE_ENSEIGNANT WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<PeriodeScolaire> getAllPeriodes() {
        List<PeriodeScolaire> periodes = new ArrayList<>();
        String query = "SELECT * FROM PERIODE_SCOLAIRE ORDER BY type, ordreAffichage";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                PeriodeScolaire periode = new PeriodeScolaire();
                periode.setId(rs.getInt("id"));
                periode.setLibelle(rs.getString("libelle"));
                periode.setType(rs.getString("type"));
                periode.setOrdre(rs.getInt("ordreAffichage"));
                periode.setActive(rs.getBoolean("estActive"));
                periodes.add(periode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return periodes;
    }

    public static boolean savePeriode(PeriodeScolaire periode) {
        String query = "INSERT INTO PERIODE_SCOLAIRE (libelle, type, ordreAffichage, estActive) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, periode.getLibelle());
            stmt.setString(2, periode.getType());
            stmt.setInt(3, periode.getOrdre());
            stmt.setBoolean(4, periode.isActive());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<ConfigurationScolaire> getConfigurations() {
        List<ConfigurationScolaire> configurations = new ArrayList<>();
        String query = "SELECT * FROM CONFIGURATION_SCOLAIRE ORDER BY categorie, cleConfig";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ConfigurationScolaire config = new ConfigurationScolaire();
                config.setCle(rs.getString("cleConfig"));
                config.setValeur(rs.getString("valeurConfig"));
                config.setCategorie(rs.getString("categorie"));
                config.setDescription(rs.getString("descriptionConfig"));
                configurations.add(config);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return configurations;
    }

    public static boolean saveConfiguration(String cle, String valeur, String categorie, String description) {
        String query =
            "INSERT INTO CONFIGURATION_SCOLAIRE (cleConfig, valeurConfig, categorie, descriptionConfig) VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE valeurConfig = VALUES(valeurConfig), categorie = VALUES(categorie), descriptionConfig = VALUES(descriptionConfig)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cle);
            stmt.setString(2, valeur);
            stmt.setString(3, categorie);
            stmt.setString(4, description);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<PresenceRecord> getPresencesBySeance(int idSeance) {
        List<PresenceRecord> presences = new ArrayList<>();
        String query =
            "SELECT p.*, e.nom, e.prenom, edt.jour, edt.heureDebut, edt.heureFin, m.libelle " +
            "FROM PRESENCE_SEANCE p " +
            "JOIN ELEVE e ON e.matricule = p.matriculeEleve " +
            "JOIN EMPLOI_DU_TEMPS edt ON edt.idSeance = p.idSeance " +
            "JOIN MATIERE m ON m.code = edt.codeMatiere " +
            "WHERE p.idSeance = ? ORDER BY e.nom, e.prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idSeance);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                presences.add(extractPresence(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presences;
    }

    public static List<PresenceRecord> getPresencesByEleve(String matricule) {
        List<PresenceRecord> presences = new ArrayList<>();
        String query =
            "SELECT p.*, e.nom, e.prenom, edt.jour, edt.heureDebut, edt.heureFin, m.libelle " +
            "FROM PRESENCE_SEANCE p " +
            "JOIN ELEVE e ON e.matricule = p.matriculeEleve " +
            "JOIN EMPLOI_DU_TEMPS edt ON edt.idSeance = p.idSeance " +
            "JOIN MATIERE m ON m.code = edt.codeMatiere " +
            "WHERE p.matriculeEleve = ? ORDER BY p.dateSaisie DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matricule);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                presences.add(extractPresence(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presences;
    }

    public static boolean savePresencesForSeance(int idSeance, List<PresenceRecord> presences) {
        if (hasPresencesForSeance(idSeance)) {
            return false;
        }
        String query =
            "INSERT INTO PRESENCE_SEANCE (idSeance, matriculeEleve, statut, remarque, justificationParent) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (PresenceRecord presence : presences) {
                stmt.setInt(1, idSeance);
                stmt.setString(2, presence.getMatriculeEleve());
                stmt.setString(3, presence.getStatut());
                stmt.setString(4, presence.getRemarque());
                stmt.setString(5, presence.getJustificationParent());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasPresencesForSeance(int idSeance) {
        String query = "SELECT COUNT(*) FROM PRESENCE_SEANCE WHERE idSeance = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idSeance);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean justifyAbsence(int presenceId, String justification) {
        String query = "UPDATE PRESENCE_SEANCE SET justificationParent = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, justification);
            stmt.setInt(2, presenceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<MessageInterne> getMessagesForUser(String role, Integer userId, String matriculeEleve) {
        List<MessageInterne> messages = new ArrayList<>();
        String query =
            "SELECT * FROM MESSAGE_INTERNE WHERE " +
            "((destinataireRole = ? AND destinataireId " + (userId == null ? "IS NULL" : "= ?") + ") " +
            (matriculeEleve != null ? "OR (destinataireRole = 'ELEVE' AND matriculeEleve = ?)" : "") +
            ") ORDER BY dateEnvoi DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            stmt.setString(index++, role);
            if (userId != null) {
                stmt.setInt(index++, userId);
            }
            if (matriculeEleve != null) {
                stmt.setString(index, matriculeEleve);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(extractMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static boolean sendMessage(String expediteurRole, int expediteurId, String expediteurNom, String destinataireRole,
                                      Integer destinataireId, String matriculeEleve, String sujet, String contenu) {
        String query =
            "INSERT INTO MESSAGE_INTERNE (expediteurRole, expediteurId, expediteurNom, destinataireRole, destinataireId, matriculeEleve, sujet, contenu) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, expediteurRole);
            stmt.setInt(2, expediteurId);
            stmt.setString(3, expediteurNom);
            stmt.setString(4, destinataireRole);
            if (destinataireId == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, destinataireId);
            }
            if (matriculeEleve == null || matriculeEleve.isBlank()) {
                stmt.setNull(6, Types.VARCHAR);
            } else {
                stmt.setString(6, matriculeEleve);
            }
            stmt.setString(7, sujet);
            stmt.setString(8, contenu);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<FraisScolarite> getFraisByEleve(String matricule) {
        List<FraisScolarite> frais = new ArrayList<>();
        String query = "SELECT f.*, e.nom, e.prenom FROM FRAIS_SCOLARITE f JOIN ELEVE e ON e.matricule = f.matriculeEleve WHERE f.matriculeEleve = ? ORDER BY dateEcheance DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matricule);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                frais.add(extractFrais(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return frais;
    }

    public static List<FraisScolarite> getAllFraisScolarite() {
        List<FraisScolarite> frais = new ArrayList<>();
        String query = "SELECT f.*, e.nom, e.prenom FROM FRAIS_SCOLARITE f JOIN ELEVE e ON e.matricule = f.matriculeEleve ORDER BY f.dateEcheance DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                frais.add(extractFrais(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return frais;
    }

    public static boolean saveFrais(FraisScolarite frais) {
        String query = "INSERT INTO FRAIS_SCOLARITE (matriculeEleve, libelle, montant, dateEcheance, statut, datePaiement, commentaire) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, frais.getMatriculeEleve());
            stmt.setString(2, frais.getLibelle());
            stmt.setDouble(3, frais.getMontant());
            stmt.setDate(4, Date.valueOf(frais.getDateEcheance()));
            stmt.setString(5, frais.getStatut());
            if (frais.getDatePaiement() == null) {
                stmt.setNull(6, Types.DATE);
            } else {
                stmt.setDate(6, Date.valueOf(frais.getDatePaiement()));
            }
            stmt.setString(7, frais.getCommentaire());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean markFraisAsPaid(int id) {
        String query = "UPDATE FRAIS_SCOLARITE SET statut = 'PAYE', datePaiement = CURRENT_DATE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateEleveLimited(Eleve eleve) {
        String query = "UPDATE ELEVE SET telephone = ?, adresse = ?, email = ? WHERE matricule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, eleve.getTelephone());
            stmt.setString(2, eleve.getAdresse());
            stmt.setString(3, eleve.getEmail());
            stmt.setString(4, eleve.getMatricule());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void audit(String actionType, String details, Integer userId, String username) {
        String query = "INSERT INTO AUDIT_LOG (userId, username, actionType, detailsAction) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (userId == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, userId);
            }
            stmt.setString(2, username);
            stmt.setString(3, actionType);
            stmt.setString(4, details);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Path exportElevesCsv(Path targetDirectory) throws IOException {
        Files.createDirectories(targetDirectory);
        Path output = targetDirectory.resolve("eleves-export.csv");
        List<String> lines = new ArrayList<>();
        lines.add("matricule;nom;prenom;niveau;telephone;email");
        for (Eleve eleve : getAllEleves()) {
            lines.add(String.join(";",
                safeCsv(eleve.getMatricule()),
                safeCsv(eleve.getNom()),
                safeCsv(eleve.getPrenom()),
                safeCsv(eleve.getNiveau()),
                safeCsv(eleve.getTelephone()),
                safeCsv(eleve.getEmail())
            ));
        }
        Files.write(output, lines);
        return output;
    }

    public static Path exportNotesCsv(Path targetDirectory) throws IOException {
        Files.createDirectories(targetDirectory);
        Path output = targetDirectory.resolve("notes-export.csv");
        List<String> lines = new ArrayList<>();
        lines.add("matricule;classe;matiere;trimestre;moyenne");
        for (Classe classe : getAllClasses()) {
            for (int trimestre = 1; trimestre <= 3; trimestre++) {
                for (Note note : getNotesByClasseAndTrimestre(classe.getIdClasse(), trimestre)) {
                    lines.add(String.join(";",
                        safeCsv(note.getMatricule()),
                        safeCsv(classe.getNomComplet()),
                        safeCsv(note.getCodeMatiere()),
                        String.valueOf(trimestre),
                        String.format(Locale.US, "%.2f", note.getMoyenne())
                    ));
                }
            }
        }
        Files.write(output, lines);
        return output;
    }

    public static Path backupSnapshot(Path targetDirectory) throws IOException {
        Files.createDirectories(targetDirectory);
        Path output = targetDirectory.resolve("sauvegarde-gestion-scolaire.txt");
        List<String> lines = new ArrayList<>();
        lines.add("Sauvegarde logique - Gestion scolaire");
        lines.add("Eleves: " + getAllEleves().size());
        lines.add("Enseignants: " + getAllEnseignants().size());
        lines.add("Parents: " + getAllParents().size());
        lines.add("Classes: " + getAllClasses().size());
        lines.add("Matieres: " + getAllMatieres().size());
        lines.add("Utilisateurs: " + getAllUtilisateurs().size());
        lines.add("Affectations: " + getAllAffectations().size());
        lines.add("Periodes: " + getAllPeriodes().size());
        lines.add("Frais: " + getAllFraisScolarite().size());
        lines.add("Messages: " + countRows("MESSAGE_INTERNE"));
        Files.write(output, lines);
        return output;
    }

    public static String linkParentWithInvitation(int parentId, String rawCode) {
        String code = rawCode == null ? "" : rawCode.trim().toUpperCase(Locale.ROOT);
        if (code.isBlank()) {
            return "Le code d'invitation est vide.";
        }

        String select = "SELECT * FROM CODES_INVITATION WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return "Code d'invitation introuvable.";
            }

            InvitationCode invitationCode = extractInvitationCode(rs);
            if (invitationCode.isUtilise()) {
                return "Ce code d'invitation a deja ete utilise.";
            }
            if (invitationCode.getDateExpiration() != null && invitationCode.getDateExpiration().isBefore(LocalDateTime.now())) {
                return "Ce code d'invitation est expire.";
            }

            String duplicateCheck = "SELECT COUNT(*) FROM PARENT_ELEVE WHERE parentId = ? AND matriculeEleve = ?";
            try (PreparedStatement duplicateStmt = conn.prepareStatement(duplicateCheck)) {
                duplicateStmt.setInt(1, parentId);
                duplicateStmt.setString(2, invitationCode.getMatriculeEleve());
                ResultSet duplicateRs = duplicateStmt.executeQuery();
                if (duplicateRs.next() && duplicateRs.getInt(1) > 0) {
                    return "Cet eleve est deja lie a ce parent.";
                }
            }

            conn.setAutoCommit(false);
            try (
                PreparedStatement insertLink = conn.prepareStatement(
                    "INSERT INTO PARENT_ELEVE (parentId, matriculeEleve, lienParente, autoriseRecuperation, contactUrgence, valideParAdmin) VALUES (?, ?, ?, 1, 0, 0)"
                );
                PreparedStatement markUsed = conn.prepareStatement(
                    "UPDATE CODES_INVITATION SET utilise = 1, utilisePar = ? WHERE id = ?"
                )
            ) {
                insertLink.setInt(1, parentId);
                insertLink.setString(2, invitationCode.getMatriculeEleve());
                insertLink.setString(3, invitationCode.getLienParente() == null || invitationCode.getLienParente().isBlank() ? "TUTEUR" : invitationCode.getLienParente());
                insertLink.executeUpdate();

                markUsed.setInt(1, parentId);
                markUsed.setInt(2, invitationCode.getId());
                markUsed.executeUpdate();

                conn.commit();
                return "DEMANDE_EN_ATTENTE";
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return "Impossible de lier le parent a l'eleve.";
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur technique lors de la validation du code.";
        }
    }

    private static InvitationCode extractInvitationCode(ResultSet rs) throws SQLException {
        InvitationCode code = new InvitationCode();
        code.setId(rs.getInt("id"));
        code.setCode(rs.getString("code"));
        code.setMatriculeEleve(rs.getString("matriculeEleve"));
        try {
            code.setEleveNomComplet((rs.getString("prenom") + " " + rs.getString("nom")).trim());
        } catch (SQLException ignored) {
            code.setEleveNomComplet(rs.getString("matriculeEleve"));
        }
        code.setLienParente(rs.getString("lienParente"));
        code.setUtilise(rs.getBoolean("utilise"));
        code.setUtilisePar((Integer) rs.getObject("utilisePar"));
        Timestamp creation = rs.getTimestamp("dateCreation");
        Timestamp expiration = rs.getTimestamp("dateExpiration");
        code.setDateCreation(creation != null ? creation.toLocalDateTime() : null);
        code.setDateExpiration(expiration != null ? expiration.toLocalDateTime() : null);
        return code;
    }

    private static ParentEleveLink extractParentEleveLink(ResultSet rs) throws SQLException {
        ParentEleveLink link = new ParentEleveLink();
        link.setId(rs.getInt("id"));
        link.setParentId(rs.getInt("parentId"));
        link.setParentNomComplet((rs.getString("parentPrenom") + " " + rs.getString("parentNom")).trim());
        link.setParentEmail(rs.getString("parentEmail"));
        link.setMatriculeEleve(rs.getString("matriculeEleve"));
        link.setEleveNomComplet((rs.getString("elevePrenom") + " " + rs.getString("eleveNom")).trim());
        link.setLienParente(rs.getString("lienParente"));
        link.setValideParAdmin(rs.getBoolean("valideParAdmin"));
        Timestamp dateLiaison = rs.getTimestamp("dateLiaison");
        link.setDateLiaison(dateLiaison != null ? dateLiaison.toLocalDateTime() : null);
        return link;
    }

    private static String buildInvitationCode() {
        return "INV-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase(Locale.ROOT);
    }

    public static String resolveEleveMatricule(int userId, String username) {
        String[] queries = new String[] {
            "SELECT matricule FROM ELEVE WHERE idUtilisateur = ?",
            "SELECT matricule FROM ELEVE WHERE matricule = ?"
        };
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(queries[0])) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("matricule");
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(queries[1])) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("matricule");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Eleve getEleveByMatricule(String matricule) {
        String query = "SELECT * FROM ELEVE WHERE matricule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matricule);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractEleve(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Classe getClasseForEleve(String matricule) {
        AnneeScolaire annee = getActiveAnneeScolaire();
        if (annee == null) {
            return null;
        }
        String query = "SELECT c.*, (SELECT COUNT(*) FROM INSCRIPTION i2 WHERE i2.idClasse = c.idClasse AND i2.statut = 'ACTIF') as effectif " +
            "FROM CLASSE c JOIN INSCRIPTION i ON i.idClasse = c.idClasse WHERE i.matricule = ? AND i.idAnnee = ? AND i.statut = 'ACTIF' LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matricule);
            stmt.setInt(2, annee.getIdAnnee());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Classe classe = extractClasse(rs);
                classe.setEffectifActuel(rs.getInt("effectif"));
                return classe;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Utilisateur extractUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur user = new Utilisateur();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setIdPersonne(rs.getInt("idPersonne"));
        user.setEstActif(rs.getBoolean("estActif"));
        return user;
    }

    private static PresenceRecord extractPresence(ResultSet rs) throws SQLException {
        PresenceRecord presence = new PresenceRecord();
        presence.setId(rs.getInt("id"));
        presence.setIdSeance(rs.getInt("idSeance"));
        presence.setMatriculeEleve(rs.getString("matriculeEleve"));
        presence.setEleveNomComplet((rs.getString("prenom") + " " + rs.getString("nom")).trim());
        presence.setStatut(rs.getString("statut"));
        presence.setRemarque(rs.getString("remarque"));
        presence.setJustificationParent(rs.getString("justificationParent"));
        Timestamp timestamp = rs.getTimestamp("dateSaisie");
        presence.setDateSaisie(timestamp != null ? timestamp.toLocalDateTime() : null);
        presence.setJourSeance(rs.getString("jour"));
        presence.setHoraireSeance(rs.getString("heureDebut") + " - " + rs.getString("heureFin"));
        presence.setMatiereLibelle(rs.getString("libelle"));
        return presence;
    }

    private static MessageInterne extractMessage(ResultSet rs) throws SQLException {
        MessageInterne message = new MessageInterne();
        message.setId(rs.getInt("id"));
        message.setExpediteurRole(rs.getString("expediteurRole"));
        message.setExpediteurId(rs.getInt("expediteurId"));
        message.setExpediteurNom(rs.getString("expediteurNom"));
        message.setDestinataireRole(rs.getString("destinataireRole"));
        message.setDestinataireId((Integer) rs.getObject("destinataireId"));
        message.setMatriculeEleve(rs.getString("matriculeEleve"));
        message.setSujet(rs.getString("sujet"));
        message.setContenu(rs.getString("contenu"));
        Timestamp timestamp = rs.getTimestamp("dateEnvoi");
        message.setDateEnvoi(timestamp != null ? timestamp.toLocalDateTime() : null);
        message.setLu(rs.getBoolean("lu"));
        return message;
    }

    private static FraisScolarite extractFrais(ResultSet rs) throws SQLException {
        FraisScolarite frais = new FraisScolarite();
        frais.setId(rs.getInt("id"));
        frais.setMatriculeEleve(rs.getString("matriculeEleve"));
        frais.setEleveNomComplet((rs.getString("prenom") + " " + rs.getString("nom")).trim());
        frais.setLibelle(rs.getString("libelle"));
        frais.setMontant(rs.getDouble("montant"));
        Date echeance = rs.getDate("dateEcheance");
        Date paiement = rs.getDate("datePaiement");
        frais.setDateEcheance(echeance != null ? echeance.toLocalDate() : null);
        frais.setDatePaiement(paiement != null ? paiement.toLocalDate() : null);
        frais.setStatut(rs.getString("statut"));
        frais.setCommentaire(rs.getString("commentaire"));
        return frais;
    }

    private static String safeCsv(String value) {
        return value == null ? "" : value.replace(";", ",");
    }

    private static int countRows(String tableName) {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int countForTeacher(String query, String teacherMatricule) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, teacherMatricule);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static Inscription extractInscription(ResultSet rs) throws SQLException {
        Inscription inscription = new Inscription();
        inscription.setIdInscription(rs.getInt("idInscription"));
        inscription.setMatricule(rs.getString("matricule"));
        inscription.setIdAnnee(rs.getInt("idAnnee"));
        inscription.setIdClasse(rs.getInt("idClasse"));
        Date date = rs.getDate("dateInscription");
        inscription.setDateInscription(date != null ? date.toLocalDate() : null);
        inscription.setStatut(rs.getString("statut"));
        return inscription;
    }
}
