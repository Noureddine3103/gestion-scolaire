module com.school.gestion {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;

    exports com.school.gestion;
    exports com.school.gestion.controller;
    exports com.school.gestion.model;

    opens com.school.gestion to javafx.graphics, javafx.fxml;
    opens com.school.gestion.controller to javafx.fxml;
    opens com.school.gestion.model to javafx.base;
}
