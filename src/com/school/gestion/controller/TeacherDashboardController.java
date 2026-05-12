package com.school.gestion.controller;

import com.school.gestion.database.DatabaseConnection;
import com.school.gestion.model.AnneeScolaire;
import com.school.gestion.model.Classe;
import com.school.gestion.model.Eleve;
import com.school.gestion.model.Matiere;
import com.school.gestion.model.Note;
import com.school.gestion.model.Parent;
import com.school.gestion.model.PresenceRecord;
import com.school.gestion.model.Seance;
import com.school.gestion.service.DashboardAssistantService;
import com.school.gestion.service.SchoolService;
import com.school.gestion.util.AlertUtils;
import com.school.gestion.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardController {

    public Label headerTitle;
    public Label contextBadge;
    public Label userLabel;
    public VBox contentArea;
    public VBox assistantMessages;
    public FlowPane assistantQuickActions;
    public TextField assistantInput;
    public Button dashboardNavButton;
    public Button notesNavButton;
    public Button elevesNavButton;
    public Button emploisNavButton;

    private String teacherMatricule;

    public void initialize() {
        if (SessionManager.getCurrentUser() != null) {
            userLabel.setText("Enseignant  " + SessionManager.getCurrentUser().getUsername());
            teacherMatricule = SchoolService.resolveTeacherMatricule(
                SessionManager.getCurrentUser().getId(),
                SessionManager.getCurrentUser().getUsername()
            );
        } else {
            teacherMatricule = "T001";
        }
        configureAssistant();
        showDashboard();
        headerTitle.setText("Bonjour Enseignant");
        activateNav(dashboardNavButton, "Tableau de bord");
    }

    public void navigateToDashboard() {
        headerTitle.setText("Bonjour Enseignant");
        showDashboard();
        activateNav(dashboardNavButton, "Tableau de bord");
    }

    public void navigateToNotes() {
        headerTitle.setText("Saisie des notes");
        showNotesView();
        activateNav(notesNavButton, "Notes");
    }

    public void navigateToMesEleves() {
        headerTitle.setText("Classes et eleves");
        showMesElevesView();
        activateNav(elevesNavButton, "Mes classes");
    }

    public void navigateToEmplois() {
        headerTitle.setText("Mon emploi du temps");
        showEmploiView();
        activateNav(emploisNavButton, "Emploi du temps");
    }

    public void handleLogout() {
        SessionManager.logout();
    }

    public void handleAssistantSend() {
        postAssistantQuestion(assistantInput == null ? "" : assistantInput.getText());
    }

    public void handleQuickSearch() {
        navigateToMesEleves();
    }

    public void handleQuickNotifications() {
        navigateToEmplois();
    }

    public void handleQuickMessages() {
        navigateToMesEleves();
    }

    private void showDashboard() {
        contentArea.getChildren().clear();
        AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();
        int classCount = SchoolService.getTeacherClassCount(teacherMatricule);
        int matiereCount = SchoolService.getTeacherMatiereCount(teacherMatricule);
        int studentCount = annee != null ? SchoolService.getTeacherStudentCount(teacherMatricule, annee.getIdAnnee()) : 0;

        VBox hero = createHeroBlock(
            "Console enseignant",
            "Suivi des classes, evaluation continue et visibilite sur les groupes affectes.",
            annee != null ? "Annee active  " + annee.getAnnee() : "Aucune annee active"
        );

        HBox stats = new HBox(18,
            createMetricCard("Classes suivies", String.valueOf(classCount), "Planning pedagogue"),
            createMetricCard("Matieres", String.valueOf(matiereCount), "Portefeuille d'enseignement"),
            createMetricCard("Eleves actifs", String.valueOf(studentCount), "Effectif consolide")
        );

        VBox classesSection = createPanel("Mes classes", "Resume des classes assignees.");
        VBox classesList = new VBox(12);
        for (Classe classe : SchoolService.getTeacherClasses(teacherMatricule)) {
            classesList.getChildren().add(createTeacherClassRow(classe));
        }
        if (classesList.getChildren().isEmpty()) {
            classesList.getChildren().add(createEmptyState("Aucune classe affectee pour le moment."));
        }
        classesSection.getChildren().add(classesList);

        VBox matieresSection = createPanel("Mes matieres", "Repartition pedagogique.");
        FlowPane chips = new FlowPane();
        chips.setHgap(10);
        chips.setVgap(10);
        for (Matiere matiere : SchoolService.getTeacherMatieres(teacherMatricule)) {
            Label chip = new Label(matiere.getLibelle() + "  Coef " + matiere.getCoefficient());
            chip.getStyleClass().add("subject-chip");
            chips.getChildren().add(chip);
        }
        if (chips.getChildren().isEmpty()) {
            chips.getChildren().add(createEmptyState("Aucune matiere reliee a ce profil."));
        }
        matieresSection.getChildren().add(chips);

        VBox schedulePreview = createPanel("Mon rythme hebdomadaire", "Apercu du prochain planning.");
        TableView<Seance> scheduleTable = createSeanceTable();
        scheduleTable.setItems(FXCollections.observableArrayList(SchoolService.getSeancesByEnseignant(teacherMatricule)));
        schedulePreview.getChildren().add(scheduleTable);

        contentArea.getChildren().addAll(hero, stats, classesSection, matieresSection, schedulePreview);
    }

    private void showNotesView() {
        VBox root = new VBox(18);

        VBox filters = createPanel("Saisie des notes", "Selectionnez le trimestre, la classe et la matiere.");
        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ComboBox<Integer> trimestreCombo = new ComboBox<>();
        trimestreCombo.getItems().addAll(1, 2, 3);
        trimestreCombo.setPromptText("Trimestre");

        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.getItems().addAll(SchoolService.getTeacherClasses(teacherMatricule));
        classeCombo.setPromptText("Classe");

        ComboBox<Matiere> matiereCombo = new ComboBox<>();
        matiereCombo.getItems().addAll(SchoolService.getTeacherMatieres(teacherMatricule));
        matiereCombo.setPromptText("Matiere");

        Button loadBtn = new Button("Charger la grille");
        loadBtn.getStyleClass().add("btn");

        topBar.getChildren().addAll(
            labelledField("Trimestre", trimestreCombo),
            labelledField("Classe", classeCombo),
            labelledField("Matiere", matiereCombo),
            loadBtn
        );
        filters.getChildren().add(topBar);

        VBox workPanel = createPanel("Grille d'evaluation", "Renseignez les notes puis enregistrez.");
        GridPane gradeGrid = new GridPane();
        gradeGrid.setHgap(12);
        gradeGrid.setVgap(10);

        Button saveBtn = new Button("Enregistrer les notes");
        saveBtn.getStyleClass().add("btn");

        loadBtn.setOnAction(e -> {
            if (classeCombo.getValue() == null || matiereCombo.getValue() == null || trimestreCombo.getValue() == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez une classe, une matiere et un trimestre.");
                return;
            }
            buildGradeEntryGrid(gradeGrid, classeCombo.getValue(), matiereCombo.getValue(), trimestreCombo.getValue());
        });

        saveBtn.setOnAction(e -> saveGrades(gradeGrid, classeCombo.getValue(), matiereCombo.getValue(), trimestreCombo.getValue()));

        workPanel.getChildren().addAll(gradeGrid, saveBtn);
        root.getChildren().addAll(filters, workPanel);
        contentArea.getChildren().setAll(root);
    }

    private void buildGradeEntryGrid(GridPane grid, Classe classe, Matiere matiere, int trimestre) {
        grid.getChildren().clear();
        Label title = new Label(classe.getNomComplet() + "  " + matiere.getLibelle() + "  Trimestre " + trimestre);
        title.getStyleClass().add("panel-title");
        grid.add(title, 0, 0, 4, 1);

        addGridHeader(grid, "Eleve", 0);
        addGridHeader(grid, "Devoir", 1);
        addGridHeader(grid, "Examen", 2);
        addGridHeader(grid, "Composition", 3);

        AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();
        if (annee == null) {
            return;
        }

        List<Note> existingNotes = SchoolService.getNotesByClasseMatiereTrimestre(classe.getIdClasse(), matiere.getCode(), trimestre, annee.getIdAnnee());
        String query = "SELECT i.matricule, e.nom, e.prenom FROM INSCRIPTION i " +
                      "JOIN ELEVE e ON e.matricule = i.matricule " +
                      "WHERE i.idClasse = ? AND i.idAnnee = ? AND i.statut = 'ACTIF' " +
                      "ORDER BY e.nom, e.prenom";

        try (var conn = DatabaseConnection.getConnection();
             var stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, classe.getIdClasse());
            stmt.setInt(2, annee.getIdAnnee());
            ResultSet rs = stmt.executeQuery();
            int row = 2;
            while (rs.next()) {
                String matricule = rs.getString("matricule");
                grid.add(new Label(rs.getString("prenom") + " " + rs.getString("nom")), 0, row);

                TextField devoirField = createScoreField(matricule);
                TextField examField = createScoreField(matricule);
                TextField compositionField = createScoreField(matricule);

                Note existing = existingNotes.stream().filter(note -> note.getMatricule().equals(matricule)).findFirst().orElse(null);
                if (existing != null) {
                    devoirField.setText(existing.getNoteDevoir() != null ? String.valueOf(existing.getNoteDevoir()) : "");
                    examField.setText(existing.getNoteExamens() != null ? String.valueOf(existing.getNoteExamens()) : "");
                    compositionField.setText(existing.getNoteComposition() != null ? String.valueOf(existing.getNoteComposition()) : "");
                }

                GridPane.setColumnIndex(devoirField, 1);
                GridPane.setRowIndex(devoirField, row);
                GridPane.setColumnIndex(examField, 2);
                GridPane.setRowIndex(examField, row);
                GridPane.setColumnIndex(compositionField, 3);
                GridPane.setRowIndex(compositionField, row);

                grid.getChildren().addAll(devoirField, examField, compositionField);
                row++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveGrades(GridPane grid, Classe classe, Matiere matiere, Integer trimestre) {
        AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();
        if (annee == null || classe == null || matiere == null || trimestre == null) {
            AlertUtils.showError("Erreur", "Veuillez selectionner tous les champs.");
            return;
        }

        int saved = 0;
        for (Node node : grid.getChildren()) {
            if (!(node instanceof TextField field) || field.getUserData() == null) {
                continue;
            }

            int row = GridPane.getRowIndex(field) != null ? GridPane.getRowIndex(field) : 0;
            if (row < 2 || GridPane.getColumnIndex(field) == null || GridPane.getColumnIndex(field) != 1) {
                continue;
            }

            String matricule = (String) field.getUserData();
            TextField devoirField = findField(grid, row, 1);
            TextField examField = findField(grid, row, 2);
            TextField compositionField = findField(grid, row, 3);

            try {
                Note note = new Note(matricule, annee.getIdAnnee(), classe.getIdClasse(), matiere.getCode(), trimestre);
                note.setMatriculeEnseignant(teacherMatricule);
                note.setNoteDevoir(parseNullableScore(devoirField));
                note.setNoteExamens(parseNullableScore(examField));
                note.setNoteComposition(parseNullableScore(compositionField));
                if (SchoolService.saveNote(note)) {
                    saved++;
                }
            } catch (NumberFormatException ex) {
                AlertUtils.showWarning("Valeur invalide", "Une ou plusieurs notes ne sont pas numeriques.");
                return;
            }
        }
        AlertUtils.showInfo("Succes", saved + " enregistrement(s) realise(s).");
    }

    private void showMesElevesView() {
        VBox root = new VBox(18);
        VBox filterPanel = createPanel("Classes et eleves", "Visualisez la composition de chaque classe.");

        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.getItems().addAll(SchoolService.getTeacherClasses(teacherMatricule));
        classeCombo.setPromptText("Choisir une classe");

        filterPanel.getChildren().add(labelledField("Classe", classeCombo));

        VBox tablePanel = createPanel("Liste d'eleves", "Population active de la classe selectionnee.");
        TableView<Eleve> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        table.getColumns().addAll(
            createColumn("Matricule", "matricule", 130),
            createColumn("Nom", "nom", 150),
            createColumn("Prenom", "prenom", 150),
            createColumn("Date de naissance", "dateNaissance", 160)
        );
        configureDataTable(table, "Aucun eleve dans cette classe.", 320);

        classeCombo.setOnAction(e -> {
            if (classeCombo.getValue() != null) {
                table.setItems(FXCollections.observableArrayList(SchoolService.getElevesByClasse(classeCombo.getValue().getIdClasse())));
            }
        });

        tablePanel.getChildren().add(table);
        VBox attendancePanel = createPanel("Presences par seance", "Saisissez les presences, absences et retards pour une seance.");
        ComboBox<Seance> seanceCombo = new ComboBox<>();
        seanceCombo.getItems().addAll(SchoolService.getSeancesByEnseignant(teacherMatricule));
        Button loadPresenceBtn = new Button("Charger la feuille d'appel");
        loadPresenceBtn.getStyleClass().add("btn-secondary");
        GridPane attendanceGrid = new GridPane();
        attendanceGrid.setHgap(10);
        attendanceGrid.setVgap(8);
        Button savePresenceBtn = new Button("Enregistrer les presences");
        savePresenceBtn.getStyleClass().add("btn");
        savePresenceBtn.setOnAction(e -> saveAttendance(attendanceGrid, seanceCombo.getValue()));
        loadPresenceBtn.setOnAction(e -> buildAttendanceGrid(attendanceGrid, seanceCombo.getValue()));
        attendancePanel.getChildren().addAll(new HBox(12, labelledField("Seance", seanceCombo), loadPresenceBtn), attendanceGrid, savePresenceBtn);

        VBox messagePanel = createPanel("Messagerie interne", "Communiquez avec les parents et la direction.");
        ComboBox<Eleve> messageStudentCombo = new ComboBox<>();
        TextField sujetField = new TextField();
        sujetField.setPromptText("Sujet");
        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Message a transmettre...");
        bodyArea.setPrefRowCount(3);
        Button adminBtn = new Button("Envoyer a la direction");
        adminBtn.getStyleClass().add("btn-secondary");
        adminBtn.setOnAction(e -> sendTeacherMessage(null, "ADMIN", 1, sujetField, bodyArea));
        Button parentBtn = new Button("Envoyer au parent de l'eleve");
        parentBtn.getStyleClass().add("btn");
        parentBtn.setOnAction(e -> {
            Eleve target = messageStudentCombo.getValue();
            if (target == null) {
                AlertUtils.showWarning("Eleve requis", "Choisissez un eleve.");
                return;
            }
            Parent parent = SchoolService.getAllParents().stream()
                .filter(p -> SchoolService.getElevesByParent(p.getIdParent()).stream().anyMatch(el -> el.getMatricule().equals(target.getMatricule())))
                .findFirst().orElse(null);
            if (parent == null) {
                AlertUtils.showWarning("Parent introuvable", "Aucun parent lie a cet eleve.");
                return;
            }
            sendTeacherMessage(target, "PARENT", parent.getIdParent(), sujetField, bodyArea);
        });
        messagePanel.getChildren().addAll(labelledField("Eleve cible", messageStudentCombo), sujetField, bodyArea, new HBox(12, adminBtn, parentBtn));

        classeCombo.setOnAction(e -> {
            if (classeCombo.getValue() != null) {
                List<Eleve> eleves = SchoolService.getElevesByClasse(classeCombo.getValue().getIdClasse());
                table.setItems(FXCollections.observableArrayList(eleves));
                messageStudentCombo.setItems(FXCollections.observableArrayList(eleves));
            }
        });
        if (!classeCombo.getItems().isEmpty()) {
            classeCombo.getSelectionModel().selectFirst();
            List<Eleve> eleves = SchoolService.getElevesByClasse(classeCombo.getValue().getIdClasse());
            table.setItems(FXCollections.observableArrayList(eleves));
            messageStudentCombo.setItems(FXCollections.observableArrayList(eleves));
        }

        root.getChildren().addAll(filterPanel, tablePanel, attendancePanel, messagePanel);
        contentArea.getChildren().setAll(root);
    }

    private void showEmploiView() {
        VBox root = new VBox(18);
        VBox hero = createHeroBlock(
            "Mon emploi du temps",
            "Une lecture claire de vos seances par jour, matiere, classe et salle.",
            "Planning personnel"
        );

        VBox schedulePanel = createPanel("Planning enseignant", "Toutes les seances qui vous sont affectees.");
        TableView<Seance> table = createSeanceTable();
        table.setItems(FXCollections.observableArrayList(SchoolService.getSeancesByEnseignant(teacherMatricule)));
        schedulePanel.getChildren().add(table);

        root.getChildren().addAll(hero, schedulePanel);
        contentArea.getChildren().setAll(root);
    }

    private void buildAttendanceGrid(GridPane grid, Seance seance) {
        grid.getChildren().clear();
        if (seance == null) {
            return;
        }
        if (SchoolService.hasPresencesForSeance(seance.getIdSeance())) {
            Label warning = new Label("La feuille d'appel de cette seance est deja enregistree. Choisissez une autre seance pour saisir un nouvel appel.");
            warning.getStyleClass().add("form-label");
            grid.add(warning, 0, 0, 3, 1);
            return;
        }
        addGridHeader(grid, "Eleve", 0);
        addGridHeader(grid, "Statut", 1);
        addGridHeader(grid, "Remarque", 2);
        List<Eleve> eleves = SchoolService.getElevesByClasse(seance.getIdClasse());
        int row = 2;
        for (Eleve eleve : eleves) {
            grid.add(new Label(eleve.getNomComplet()), 0, row);
            ComboBox<String> statutCombo = new ComboBox<>(FXCollections.observableArrayList("PRESENT", "ABSENT", "RETARD"));
            statutCombo.setValue("PRESENT");
            TextField remarqueField = new TextField();
            remarqueField.setUserData(eleve.getMatricule());
            GridPane.setColumnIndex(statutCombo, 1);
            GridPane.setRowIndex(statutCombo, row);
            GridPane.setColumnIndex(remarqueField, 2);
            GridPane.setRowIndex(remarqueField, row);
            grid.getChildren().addAll(statutCombo, remarqueField);
            row++;
        }
    }

    private void saveAttendance(GridPane grid, Seance seance) {
        if (seance == null) {
            AlertUtils.showWarning("Seance requise", "Choisissez une seance.");
            return;
        }
        if (SchoolService.hasPresencesForSeance(seance.getIdSeance())) {
            AlertUtils.showWarning("Appel deja saisi", "Cette seance possede deja une feuille d'appel. Choisissez une autre seance pour creer un nouvel appel.");
            return;
        }
        List<PresenceRecord> presences = new ArrayList<>();
        for (Node node : grid.getChildren()) {
            if (!(node instanceof TextField remarqueField) || remarqueField.getUserData() == null) {
                continue;
            }
            Integer row = GridPane.getRowIndex(remarqueField);
            if (row == null || row < 2) {
                continue;
            }
            ComboBox<String> statutCombo = findCombo(grid, row, 1);
            PresenceRecord presence = new PresenceRecord();
            presence.setIdSeance(seance.getIdSeance());
            presence.setMatriculeEleve((String) remarqueField.getUserData());
            presence.setStatut(statutCombo != null && statutCombo.getValue() != null ? statutCombo.getValue() : "PRESENT");
            presence.setRemarque(remarqueField.getText().trim());
            presences.add(presence);
        }
        if (SchoolService.savePresencesForSeance(seance.getIdSeance(), presences)) {
            AlertUtils.showInfo("Succes", "Presences enregistrees.");
        } else {
            AlertUtils.showError("Erreur", "Impossible d'enregistrer les presences. Verifiez que la seance n'a pas deja une feuille d'appel.");
        }
    }

    private ComboBox<String> findCombo(GridPane grid, int row, int column) {
        for (Node node : grid.getChildren()) {
            if (node instanceof ComboBox<?> combo) {
                Integer nodeRow = GridPane.getRowIndex(combo);
                Integer nodeColumn = GridPane.getColumnIndex(combo);
                if (nodeRow != null && nodeColumn != null && nodeRow == row && nodeColumn == column) {
                    @SuppressWarnings("unchecked")
                    ComboBox<String> casted = (ComboBox<String>) combo;
                    return casted;
                }
            }
        }
        return null;
    }

    private void sendTeacherMessage(Eleve eleve, String role, Integer targetId, TextField sujetField, TextArea bodyArea) {
        if (sujetField.getText().isBlank() || bodyArea.getText().isBlank()) {
            AlertUtils.showWarning("Message incomplet", "Saisissez un sujet et un contenu.");
            return;
        }
        boolean success = SchoolService.sendMessage(
            "ENSEIGNANT",
            SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : 0,
            userLabel.getText(),
            role,
            targetId,
            eleve != null ? eleve.getMatricule() : null,
            sujetField.getText().trim(),
            bodyArea.getText().trim()
        );
        if (success) {
            sujetField.clear();
            bodyArea.clear();
            AlertUtils.showInfo("Succes", "Message envoye.");
        } else {
            AlertUtils.showError("Erreur", "Impossible d'envoyer le message.");
        }
    }

    private void configureAssistant() {
        if (assistantMessages == null || assistantQuickActions == null) {
            return;
        }
        assistantMessages.getChildren().clear();
        assistantQuickActions.getChildren().clear();
        addAssistantMessage(
            "Assistant",
            "Je suis votre assistant de classe. Je peux resumer vos classes, vos matieres, vos eleves et la procedure de saisie des notes.",
            false
        );
        for (String action : DashboardAssistantService.getQuickActions(false)) {
            Button quickAction = new Button(action);
            quickAction.getStyleClass().add("quick-chip");
            quickAction.setOnAction(e -> postAssistantQuestion(action));
            assistantQuickActions.getChildren().add(quickAction);
        }
    }

    private void postAssistantQuestion(String message) {
        String value = message == null ? "" : message.trim();
        if (value.isBlank()) {
            return;
        }
        addAssistantMessage("Vous", value, true);
        if (assistantInput != null) {
            assistantInput.clear();
        }
        String reply = DashboardAssistantService.answerForTeacher(value, teacherMatricule);
        addAssistantMessage("Assistant", reply, false);
    }

    private void addAssistantMessage(String author, String message, boolean user) {
        if (assistantMessages == null) {
            return;
        }
        VBox bubble = new VBox(6);
        bubble.getStyleClass().add(user ? "assistant-bubble-user" : "assistant-bubble-bot");

        Label authorLabel = new Label(author);
        authorLabel.getStyleClass().add("assistant-author");

        Label bodyLabel = new Label(message);
        bodyLabel.getStyleClass().add("assistant-body");
        bodyLabel.setWrapText(true);

        bubble.getChildren().addAll(authorLabel, bodyLabel);
        assistantMessages.getChildren().add(bubble);
    }

    private void activateNav(Button activeButton, String context) {
        if (contextBadge != null) {
            contextBadge.setText(context);
        }
        List<Button> navButtons = List.of(dashboardNavButton, notesNavButton, elevesNavButton, emploisNavButton);
        for (Button button : navButtons) {
            if (button == null) {
                continue;
            }
            button.getStyleClass().removeAll("nav-button-active", "soft-nav-button-active");
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("soft-nav-button-active")) {
            activeButton.getStyleClass().add("soft-nav-button-active");
        }
    }

    private VBox createHeroBlock(String titleText, String bodyText, String tagText) {
        VBox hero = new VBox(10);
        hero.getStyleClass().add("hero-panel");

        Label tag = new Label(tagText);
        tag.getStyleClass().add("hero-tag");

        Label title = new Label(titleText);
        title.getStyleClass().add("hero-title");

        Label body = new Label(bodyText);
        body.getStyleClass().add("hero-body");
        body.setWrapText(true);

        hero.getChildren().addAll(tag, title, body);
        return hero;
    }

    private VBox createMetricCard(String titleText, String valueText, String noteText) {
        VBox card = new VBox(8);
        card.getStyleClass().add("metric-card");

        Label title = new Label(titleText);
        title.getStyleClass().add("metric-title");

        Label value = new Label(valueText);
        value.getStyleClass().add("metric-value");

        Label note = new Label(noteText);
        note.getStyleClass().add("metric-note");

        card.getChildren().addAll(title, value, note);
        return card;
    }

    private VBox createPanel(String titleText, String subtitleText) {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("content-panel");

        Label title = new Label(titleText);
        title.getStyleClass().add("panel-title");

        Label subtitle = new Label(subtitleText);
        subtitle.getStyleClass().add("panel-subtitle");

        panel.getChildren().addAll(title, subtitle);
        return panel;
    }

    private VBox labelledField(String labelText, Node field) {
        VBox wrapper = new VBox(7);
        wrapper.setPrefWidth(220);
        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");
        if (field instanceof Region region) {
            region.setMaxWidth(Double.MAX_VALUE);
        }
        wrapper.getChildren().addAll(label, field);
        return wrapper;
    }

    private VBox createTeacherClassRow(Classe classe) {
        VBox row = new VBox(8);
        row.getStyleClass().add("mini-card");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(classe.getNomComplet());
        title.getStyleClass().add("mini-card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label capacity = new Label(classe.getEffectifActuel() + " / " + classe.getCapacite());
        capacity.getStyleClass().add("mini-card-badge");
        top.getChildren().addAll(title, spacer, capacity);

        Label subtitle = new Label("Taux de remplissage " + Math.round(classe.getRemplissage() * 100) + " %");
        subtitle.getStyleClass().add("mini-card-subtitle");

        row.getChildren().addAll(top, subtitle);
        return row;
    }

    private Label createEmptyState(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("empty-state");
        return label;
    }

    private void addGridHeader(GridPane grid, String text, int column) {
        Label header = new Label(text);
        header.getStyleClass().add("grid-header");
        grid.add(header, column, 1);
    }

    private TextField createScoreField(String matricule) {
        TextField field = new TextField();
        field.getStyleClass().add("score-field");
        field.setUserData(matricule);
        return field;
    }

    private TextField findField(GridPane grid, int row, int column) {
        for (Node node : grid.getChildren()) {
            if (node instanceof TextField field) {
                Integer nodeRow = GridPane.getRowIndex(field);
                Integer nodeColumn = GridPane.getColumnIndex(field);
                if (nodeRow != null && nodeColumn != null && nodeRow == row && nodeColumn == column) {
                    return field;
                }
            }
        }
        return null;
    }

    private Double parseNullableScore(TextField field) {
        if (field == null) {
            return null;
        }
        String value = field.getText();
        if (value == null || value.isBlank()) {
            return null;
        }
        return Double.parseDouble(value.trim());
    }

    private <T> TableColumn<T, Object> createColumn(String title, String property, double width) {
        TableColumn<T, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> {
            try {
                var method = data.getValue().getClass().getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
                Object val = method.invoke(data.getValue());
                return new javafx.beans.property.SimpleObjectProperty<>(val != null ? val : "");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleObjectProperty<>("");
            }
        });
        col.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.valueOf(item));
                setGraphic(null);
                setStyle("-fx-text-fill: #1d2433; -fx-padding: 10 12 10 12; -fx-alignment: CENTER-LEFT;");
            }
        });
        col.setPrefWidth(width);
        return col;
    }

    private <T> void configureDataTable(TableView<T> table, String emptyMessage, double prefHeight) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label(emptyMessage));
        table.setFixedCellSize(42);
        table.setPrefHeight(prefHeight);
        table.setMinHeight(prefHeight);
    }

    private TableView<Seance> createSeanceTable() {
        TableView<Seance> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        configureDataTable(table, "Aucune seance affectee.", 280);
        table.getColumns().addAll(
            createColumn("Jour", "jour", 110),
            createColumn("Horaire", "plageHoraire", 150),
            createColumn("Classe", "classeNom", 180),
            createColumn("Matiere", "matiereLibelle", 180),
            createColumn("Salle", "salle", 100)
        );
        return table;
    }
}
