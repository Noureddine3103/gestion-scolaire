package com.school.gestion.controller;

import com.school.gestion.model.Utilisateur;
import com.school.gestion.util.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    public TextField usernameField;
    public PasswordField passwordField;
    public Label errorLabel;

    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        try {
            var user = SessionManager.authenticate(username, password);
            if (user != null) {
                SessionManager.setCurrentUser(user);
                navigateToDashboard(user);
            } else {
                showError("Identifiants incorrects ou connexion BD échouée");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur BD: " + e.getMessage());
        }
    }

    private void navigateToDashboard(Utilisateur user) {
        try {
            String fxmlPath;
            String title;
            if (user.isAdmin()) {
                fxmlPath = "/com/school/gestion/fxml/AdminDashboard.fxml";
                title = "Gestion Scolaire - Administrateur";
            } else if (user.isEnseignant()) {
                fxmlPath = "/com/school/gestion/fxml/TeacherDashboard.fxml";
                title = "Gestion Scolaire - Enseignant";
            } else if (user.isParent()) {
                fxmlPath = "/com/school/gestion/fxml/ParentDashboard.fxml";
                title = "Gestion Scolaire - Parent";
            } else if (user.isEleve()) {
                fxmlPath = "/com/school/gestion/fxml/StudentDashboard.fxml";
                title = "Gestion Scolaire - Eleve";
            } else {
                fxmlPath = "/com/school/gestion/fxml/AdminDashboard.fxml";
                title = "Gestion Scolaire";
            }
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = SessionManager.getPrimaryStage();
            Scene scene = new Scene(root, 1560, 920);
            scene.getStylesheets().add(getClass().getResource("/com/school/gestion/css/styles.css").toExternalForm());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMinWidth(1380);
            stage.setMinHeight(820);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
