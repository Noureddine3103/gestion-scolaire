package com.school.gestion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.school.gestion.util.SessionManager;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SessionManager.setPrimaryStage(stage);
        Parent root = FXMLLoader.load(getClass().getResource("/com/school/gestion/fxml/LoginView.fxml"));
        Scene scene = new Scene(root, 1180, 760);
        scene.getStylesheets().add(getClass().getResource("/com/school/gestion/css/styles.css").toExternalForm());
        stage.setTitle("Gestion Scolaire - Connexion");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.show();
    }
}
