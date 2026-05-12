package com.school.gestion;

import javafx.application.Application;

public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        System.setProperty("prism.order", "d3d,sw");
        System.setProperty("prism.verbose", "false");
        Application.launch(MainApp.class, args);
    }
}
