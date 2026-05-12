package com.school.gestion.util;

import com.school.gestion.database.DatabaseConnection;
import com.school.gestion.model.Utilisateur;
import com.school.gestion.service.SchoolService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class SessionManager {
    private static Utilisateur currentUser;
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setCurrentUser(Utilisateur user) {
        currentUser = user;
    }

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public static boolean isEnseignant() {
        return currentUser != null && currentUser.isEnseignant();
    }

    public static boolean isParent() {
        return currentUser != null && currentUser.isParent();
    }

    public static boolean isEleve() {
        return currentUser != null && currentUser.isEleve();
    }

    public static void logout() {
        currentUser = null;
        if (primaryStage == null) {
            return;
        }
        try {
            Parent root = FXMLLoader.load(SessionManager.class.getResource("/com/school/gestion/fxml/LoginView.fxml"));
            Scene scene = new Scene(root, 1180, 760);
            scene.getStylesheets().add(SessionManager.class.getResource("/com/school/gestion/css/styles.css").toExternalForm());
            primaryStage.setTitle("Gestion Scolaire - Connexion");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de revenir a l'ecran de connexion.", e);
        }
    }

    public static Utilisateur authenticate(String username, String password) throws Exception {
        String query = "SELECT * FROM UTILISATEUR WHERE username = ? AND estActif = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }

            Timestamp lockUntilTs = rs.getTimestamp("lockUntil");
            if (lockUntilTs != null && lockUntilTs.toLocalDateTime().isAfter(LocalDateTime.now())) {
                throw new Exception("Compte verrouille temporairement. Reessayez dans quelques minutes.");
            }

            String hashedPassword = rs.getString("password");
            boolean valid;
            try {
                valid = BCrypt.checkpw(password, hashedPassword);
            } catch (IllegalArgumentException ex) {
                valid = false;
            }

            if (!valid) {
                registerFailure(conn, rs.getInt("id"), rs.getInt("failedAttempts"), username);
                return null;
            }

            resetFailureState(conn, rs.getInt("id"));
            Utilisateur user = new Utilisateur();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(hashedPassword);
            user.setRole(rs.getString("role"));
            user.setIdPersonne(rs.getInt("idPersonne"));
            user.setEstActif(rs.getBoolean("estActif"));
            SchoolService.audit("LOGIN_SUCCESS", "Connexion reussie", user.getId(), user.getUsername());
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Erreur de connexion a la base de donnees: " + e.getMessage());
        }
    }

    private static void registerFailure(Connection conn, int userId, int currentAttempts, String username) {
        String query = "UPDATE UTILISATEUR SET failedAttempts = ?, lockUntil = ? WHERE id = ?";
        int nextAttempts = currentAttempts + 1;
        LocalDateTime lockUntil = nextAttempts >= 5 ? LocalDateTime.now().plusMinutes(15) : null;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, nextAttempts >= 5 ? 0 : nextAttempts);
            if (lockUntil == null) {
                stmt.setNull(2, java.sql.Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(2, Timestamp.valueOf(lockUntil));
            }
            stmt.setInt(3, userId);
            stmt.executeUpdate();
            SchoolService.audit("LOGIN_FAILURE", "Echec de connexion", userId, username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void resetFailureState(Connection conn, int userId) {
        String query = "UPDATE UTILISATEUR SET failedAttempts = 0, lockUntil = NULL WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
