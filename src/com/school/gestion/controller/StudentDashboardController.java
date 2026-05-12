package com.school.gestion.controller;

import com.school.gestion.model.Classe;
import com.school.gestion.model.Eleve;
import com.school.gestion.model.MessageInterne;
import com.school.gestion.model.Note;
import com.school.gestion.model.PresenceRecord;
import com.school.gestion.model.Seance;
import com.school.gestion.service.SchoolService;
import com.school.gestion.util.AlertUtils;
import com.school.gestion.util.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StudentDashboardController {
    public Label headerTitle;
    public Label headerSubtitle;
    public Label contextBadge;
    public Label userLabel;
    public VBox contentArea;
    public Button dashboardNavButton;
    public Button coursNavButton;
    public Button notesNavButton;
    public Button devoirsNavButton;
    public Button emploisNavButton;
    public Button absencesNavButton;
    public Button messagesNavButton;
    public Button profilNavButton;

    private Eleve currentStudent;
    private Classe currentClasse;
    private int selectedTrimestre = 1;

    public void initialize() {
        String matricule = SessionManager.getCurrentUser() != null
            ? SchoolService.resolveEleveMatricule(SessionManager.getCurrentUser().getId(), SessionManager.getCurrentUser().getUsername())
            : null;
        currentStudent = matricule != null ? SchoolService.getEleveByMatricule(matricule) : null;
        currentClasse = currentStudent != null ? SchoolService.getClasseForEleve(currentStudent.getMatricule()) : null;
        userLabel.setText(currentStudent != null ? currentStudent.getNomComplet() : "Eleve");
        headerTitle.setText("Bonjour " + (currentStudent != null ? currentStudent.getPrenom() : ""));
        headerSubtitle.setText("Voici votre activite scolaire d'aujourd'hui.");
        contextBadge.setText("Eleve");
        navigateToDashboard();
    }

    public void navigateToDashboard() {
        headerSubtitle.setText("Voici votre activite scolaire d'aujourd'hui.");
        contextBadge.setText("Tableau de bord");
        showDashboard();
        activate(dashboardNavButton);
    }

    public void navigateToCours() {
        headerSubtitle.setText("Retrouvez vos prochains cours et votre planning.");
        contextBadge.setText("Cours");
        showCoursView();
        activate(coursNavButton);
    }

    public void navigateToNotes() {
        headerSubtitle.setText("Consultez vos resultats par matiere.");
        contextBadge.setText("Notes");
        showNotesView();
        activate(notesNavButton);
    }

    public void navigateToDevoirs() {
        headerSubtitle.setText("Suivez vos devoirs et rappels de rendu.");
        contextBadge.setText("Devoirs");
        showDevoirsView();
        activate(devoirsNavButton);
    }

    public void navigateToEmploi() {
        headerSubtitle.setText("Consultez votre emploi du temps complet.");
        contextBadge.setText("Emploi du temps");
        showEmploiView();
        activate(emploisNavButton);
    }

    public void navigateToAbsences() {
        headerSubtitle.setText("Visualisez vos absences et retards.");
        contextBadge.setText("Absences");
        showAbsencesView();
        activate(absencesNavButton);
    }

    public void navigateToMessages() {
        headerSubtitle.setText("Messages recus des enseignants.");
        contextBadge.setText("Messages");
        showMessagesView();
        activate(messagesNavButton);
    }

    public void navigateToProfil() {
        headerSubtitle.setText("Informations completes du profil eleve.");
        contextBadge.setText("Profil");
        showProfilView();
        activate(profilNavButton);
    }

    public void handleLogout() {
        SessionManager.logout();
    }

    public void handleQuickSearch() {
        headerSubtitle.setText("Recherche rapide dans vos cours, notes et messages.");
        contextBadge.setText("Recherche");
        showSearchView();
        activate(null);
    }

    public void handleQuickNotifications() {
        headerSubtitle.setText("Toutes vos notifications importantes.");
        contextBadge.setText("Notifications");
        showNotificationsView();
        activate(null);
    }

    public void handleQuickMessages() {
        navigateToMessages();
    }

    private void showDashboard() {
        contentArea.getChildren().clear();
        if (currentStudent == null) {
            contentArea.getChildren().add(new Label("Aucun profil eleve associe a ce compte."));
            return;
        }

        HBox top = new HBox(20);
        HBox.setHgrow(top, Priority.ALWAYS);
        top.getChildren().addAll(buildProfileCard(), buildNotesCard(), buildCoursesCard());
        contentArea.getChildren().add(top);
        contentArea.getChildren().add(buildMessagesCard());
    }

    private void showCoursView() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(buildCoursesCard());
    }

    private void showNotesView() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(buildNotesCard());
    }

    private void showDevoirsView() {
        contentArea.getChildren().clear();
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Mes devoirs");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().addAll(
            title,
            messageItem("Math", "Exercices a rendre", "Resoudre les equations du chapitre 4."),
            messageItem("Histoire", "Expose en groupe", "Preparer la presentation sur les empires africains.")
        );
        contentArea.getChildren().add(card);
    }

    private void showEmploiView() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(buildWeeklyScheduleCard());
    }

    private void showAbsencesView() {
        contentArea.getChildren().clear();
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Absences et retards");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        List<PresenceRecord> presences = currentStudent == null ? List.of() : SchoolService.getPresencesByEleve(currentStudent.getMatricule());
        List<PresenceRecord> incidents = presences.stream().filter(p -> !"PRESENT".equalsIgnoreCase(p.getStatut())).toList();
        if (incidents.isEmpty()) {
            card.getChildren().add(new Label("Aucune absence ni retard enregistre."));
        } else {
            incidents.forEach(presence -> card.getChildren().add(messageItem(
                presence.getStatut(),
                presence.getMatiereLibelle() + " - " + presence.getJourSeance(),
                (presence.getRemarque() == null || presence.getRemarque().isBlank() ? "Sans remarque" : presence.getRemarque())
                    + (presence.getJustificationParent() != null && !presence.getJustificationParent().isBlank()
                    ? " | Justification : " + presence.getJustificationParent() : "")
            )));
        }
        contentArea.getChildren().add(card);
    }

    private void showMessagesView() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(buildMessagesCard());
    }

    private void showProfilView() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(buildProfileDetailsCard());
    }

    private void showSearchView() {
        contentArea.getChildren().clear();
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Recherche globale");
        title.getStyleClass().add("soft-section-title");
        TextField queryField = new TextField();
        queryField.setPromptText("Rechercher un cours, une note, un message...");
        VBox results = new VBox(10);
        Runnable refresh = () -> {
            results.getChildren().clear();
            String q = queryField.getText() == null ? "" : queryField.getText().trim().toLowerCase(Locale.ROOT);
            if (currentClasse != null) {
                SchoolService.getSeancesByClasse(currentClasse.getIdClasse()).stream()
                    .filter(s -> q.isBlank()
                        || s.getMatiereLibelle().toLowerCase(Locale.ROOT).contains(q)
                        || s.getJour().toLowerCase(Locale.ROOT).contains(q))
                    .forEach(s -> results.getChildren().add(messageItem("Cours", s.getMatiereLibelle(), s.getJour() + " " + s.getHeureDebut() + " - " + s.getHeureFin())));
                SchoolService.getNotesByClasseAndTrimestre(currentClasse.getIdClasse(), selectedTrimestre).stream()
                    .filter(n -> currentStudent.getMatricule().equals(n.getMatricule()))
                    .filter(n -> q.isBlank() || n.getCodeMatiere().toLowerCase(Locale.ROOT).contains(q))
                    .forEach(n -> results.getChildren().add(messageItem("Note", n.getCodeMatiere(), String.format(Locale.US, "%.2f/20 - %s", n.getMoyenne(), getAppreciation(n.getMoyenne())))));
            }
            SchoolService.getMessagesForUser("ELEVE", null, currentStudent != null ? currentStudent.getMatricule() : null).stream()
                .filter(m -> q.isBlank()
                    || m.getSujet().toLowerCase(Locale.ROOT).contains(q)
                    || m.getContenu().toLowerCase(Locale.ROOT).contains(q))
                .forEach(m -> results.getChildren().add(messageItem("Message", m.getSujet(), m.getContenu())));
            if (results.getChildren().isEmpty()) {
                results.getChildren().add(new Label("Aucun resultat trouve."));
            }
        };
        queryField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        refresh.run();
        card.getChildren().addAll(title, queryField, results);
        contentArea.getChildren().setAll(card);
    }

    private void showNotificationsView() {
        contentArea.getChildren().clear();
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Centre de notifications");
        title.getStyleClass().add("soft-section-title");
        int messageCount = currentStudent == null ? 0 : SchoolService.getMessagesForUser("ELEVE", null, currentStudent.getMatricule()).size();
        long absenceCount = currentStudent == null ? 0 : SchoolService.getPresencesByEleve(currentStudent.getMatricule()).stream()
            .filter(p -> !"PRESENT".equalsIgnoreCase(p.getStatut())).count();
        int courseCount = currentClasse == null ? 0 : SchoolService.getSeancesByClasse(currentClasse.getIdClasse()).size();

        Button messages = new Button("Messages recus : " + messageCount);
        messages.getStyleClass().add("soft-outline-button");
        messages.setOnAction(e -> navigateToMessages());
        Button absences = new Button("Absences / retards : " + absenceCount);
        absences.getStyleClass().add("soft-outline-button");
        absences.setOnAction(e -> navigateToAbsences());
        Button cours = new Button("Cours planifies : " + courseCount);
        cours.getStyleClass().add("soft-outline-button");
        cours.setOnAction(e -> navigateToEmploi());
        card.getChildren().addAll(title, messages, absences, cours);
        contentArea.getChildren().setAll(card);
    }

    private VBox buildProfileCard() {
        VBox card = baseCard("soft-card-profile", 24);
        card.setPrefWidth(320);
        StackPane avatar = new StackPane(new Label(initials(currentStudent.getPrenom(), currentStudent.getNom())));
        avatar.getStyleClass().add("soft-avatar-large");
        Label name = new Label(currentStudent.getNomComplet());
        name.getStyleClass().add("soft-card-title");
        Label classe = new Label("Classe : " + (currentClasse != null ? currentClasse.getNomComplet() : "-"));
        classe.getStyleClass().add("soft-highlight-text");
        Button profileButton = new Button("Voir le profil complet");
        profileButton.getStyleClass().add("soft-outline-button");
        profileButton.setOnAction(e -> navigateToProfil());
        card.getChildren().addAll(avatar, name, classe, new Label("Matricule : " + currentStudent.getMatricule()), new Label("Niveau : " + currentStudent.getNiveau()), profileButton);
        return card;
    }

    private VBox buildNotesCard() {
        VBox card = baseCard("soft-card-main", 22);
        card.setPrefWidth(470);
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Mes notes");
        title.getStyleClass().add("soft-section-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        ComboBox<Integer> trimestreCombo = new ComboBox<>();
        trimestreCombo.getItems().addAll(1, 2, 3);
        trimestreCombo.setValue(selectedTrimestre);
        trimestreCombo.setOnAction(e -> {
            if (trimestreCombo.getValue() != null) {
                selectedTrimestre = trimestreCombo.getValue();
                showNotesView();
            }
        });
        header.getChildren().addAll(title, spacer, trimestreCombo);
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(10);
        addHeader(grid, "Matiere", 0);
        addHeader(grid, "Moyenne", 1);
        addHeader(grid, "Appreciation", 2);

        List<Note> notes = currentClasse == null ? List.of() : SchoolService.getNotesByClasseAndTrimestre(currentClasse.getIdClasse(), selectedTrimestre).stream()
            .filter(note -> currentStudent.getMatricule().equals(note.getMatricule()))
            .toList();
        double total = 0.0;
        int row = 1;
        for (Note note : notes) {
            double moyenne = note.getMoyenne();
            total += moyenne;
            grid.add(bodyCell(note.getCodeMatiere()), 0, row);
            grid.add(bodyCell(String.format(Locale.US, "%.2f", moyenne)), 1, row);
            grid.add(bodyCell(getAppreciation(moyenne)), 2, row);
            row++;
        }
        double moyenneGenerale = notes.isEmpty() ? 0.0 : total / notes.size();
        Label summary = new Label("Moyenne generale : " + String.format(Locale.US, "%.2f/20", moyenneGenerale));
        summary.getStyleClass().add("soft-highlight-text");
        Button bulletinButton = new Button("Voir le bulletin complet");
        bulletinButton.getStyleClass().add("soft-outline-button");
        bulletinButton.setOnAction(e -> navigateToNotes());
        card.getChildren().addAll(header, grid, summary, bulletinButton);
        return card;
    }

    private VBox buildCoursesCard() {
        VBox card = baseCard("soft-card-side", 20);
        card.setPrefWidth(320);
        Label title = new Label("Prochains cours");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        if (currentClasse != null) {
            SchoolService.getSeancesByClasse(currentClasse.getIdClasse()).stream()
                .sorted(Comparator.comparing(Seance::getJour).thenComparing(Seance::getHeureDebut))
                .limit(4)
                .forEach(seance -> card.getChildren().add(courseItem(seance)));
        }
        return card;
    }

    private VBox buildMessagesCard() {
        VBox card = baseCard("soft-card-wide", 20);
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Messages des enseignants");
        title.getStyleClass().add("soft-section-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button allMessagesButton = new Button("Voir tous les messages");
        allMessagesButton.getStyleClass().add("soft-link-button");
        allMessagesButton.setOnAction(e -> navigateToMessages());
        header.getChildren().addAll(title, spacer, allMessagesButton);
        card.getChildren().add(header);
        List<MessageInterne> messages = currentStudent == null
            ? List.of()
            : SchoolService.getMessagesForUser("ELEVE", null, currentStudent.getMatricule());
        if (messages.isEmpty()) {
            card.getChildren().add(new Label("Aucun message recu."));
        } else {
            messages.stream().limit(8).forEach(message ->
                card.getChildren().add(messageItem(message.getExpediteurNom(), message.getSujet(), message.getContenu()))
            );
        }
        return card;
    }

    private VBox buildWeeklyScheduleCard() {
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Emploi du temps");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        if (currentClasse != null) {
            SchoolService.getSeancesByClasse(currentClasse.getIdClasse()).stream()
                .sorted(Comparator.comparing(Seance::getJour).thenComparing(Seance::getHeureDebut))
                .forEach(seance -> card.getChildren().add(messageItem(
                    seance.getJour(),
                    seance.getMatiereLibelle(),
                    seance.getHeureDebut() + " - " + seance.getHeureFin() + "  " + seance.getClasseNom()
                )));
        }
        return card;
    }

    private VBox buildProfileDetailsCard() {
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Profil complet");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        card.getChildren().add(detailLine("Nom complet", currentStudent.getNomComplet()));
        card.getChildren().add(detailLine("Matricule", currentStudent.getMatricule()));
        card.getChildren().add(detailLine("Niveau", currentStudent.getNiveau()));
        card.getChildren().add(detailLine("Classe", currentClasse != null ? currentClasse.getNomComplet() : "-"));
        TextField phoneField = new TextField(currentStudent.getTelephone() == null ? "" : currentStudent.getTelephone());
        TextField emailField = new TextField(currentStudent.getEmail() == null ? "" : currentStudent.getEmail());
        TextArea addressArea = new TextArea(currentStudent.getAdresse() == null ? "" : currentStudent.getAdresse());
        addressArea.setPrefRowCount(3);
        TextField passwordField = new TextField();
        passwordField.setPromptText("Nouveau mot de passe");
        Button saveProfileBtn = new Button("Mettre a jour mes informations");
        saveProfileBtn.getStyleClass().add("soft-outline-button");
        saveProfileBtn.setOnAction(e -> {
            currentStudent.setTelephone(phoneField.getText().trim());
            currentStudent.setEmail(emailField.getText().trim());
            currentStudent.setAdresse(addressArea.getText().trim());
            if (SchoolService.updateEleveLimited(currentStudent)) {
                AlertUtils.showInfo("Succes", "Informations mises a jour.");
            } else {
                AlertUtils.showError("Erreur", "Impossible de mettre a jour les informations.");
            }
        });
        Button savePasswordBtn = new Button("Modifier mon mot de passe");
        savePasswordBtn.getStyleClass().add("soft-outline-button");
        savePasswordBtn.setOnAction(e -> {
            if (SessionManager.getCurrentUser() == null || passwordField.getText().isBlank()) {
                AlertUtils.showWarning("Mot de passe manquant", "Saisissez un nouveau mot de passe.");
                return;
            }
            if (SchoolService.updateOwnPassword(SessionManager.getCurrentUser().getId(), passwordField.getText().trim())) {
                passwordField.clear();
                AlertUtils.showInfo("Succes", "Mot de passe mis a jour.");
            } else {
                AlertUtils.showError("Erreur", "Impossible de modifier le mot de passe.");
            }
        });
        card.getChildren().add(detailLine("Telephone actuel", currentStudent.getTelephone()));
        card.getChildren().add(detailLine("Adresse actuelle", currentStudent.getAdresse()));
        card.getChildren().add(new Label("Telephone"));
        card.getChildren().add(phoneField);
        card.getChildren().add(new Label("Email"));
        card.getChildren().add(emailField);
        card.getChildren().add(new Label("Adresse"));
        card.getChildren().add(addressArea);
        card.getChildren().add(saveProfileBtn);
        card.getChildren().add(new Label("Mot de passe"));
        card.getChildren().add(passwordField);
        card.getChildren().add(savePasswordBtn);
        return card;
    }

    private VBox baseCard(String styleClass, double padding) {
        VBox box = new VBox(16);
        box.getStyleClass().add(styleClass);
        box.setPadding(new Insets(padding));
        return box;
    }

    private void addHeader(GridPane grid, String text, int column) {
        Label label = new Label(text);
        label.getStyleClass().add("soft-grid-header");
        grid.add(label, column, 0);
    }

    private Label bodyCell(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("soft-grid-cell");
        return label;
    }

    private HBox courseItem(Seance seance) {
        HBox row = new HBox(14);
        row.getStyleClass().add("soft-list-item");
        VBox time = new VBox(new Label(String.valueOf(seance.getHeureDebut())), new Label(String.valueOf(seance.getHeureFin())));
        time.getStyleClass().add("soft-time-box");
        VBox info = new VBox(new Label(seance.getMatiereLibelle()), new Label(seance.getClasseNom()));
        info.getStyleClass().add("soft-course-info");
        row.getChildren().addAll(time, info);
        return row;
    }

    private HBox messageItem(String author, String subject, String body) {
        HBox row = new HBox(14);
        row.getStyleClass().add("soft-message-item");
        StackPane avatar = new StackPane(new Label(author.substring(0, 1)));
        avatar.getStyleClass().add("soft-avatar-small");
        VBox content = new VBox(new Label(author), new Label(subject), new Label(body));
        content.getStyleClass().add("soft-message-content");
        row.getChildren().addAll(avatar, content);
        return row;
    }

    private HBox detailLine(String labelText, String valueText) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.getStyleClass().add("soft-detail-label");
        Label value = new Label(valueText == null || valueText.isBlank() ? "-" : valueText);
        value.getStyleClass().add("soft-detail-value");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(label, spacer, value);
        return row;
    }

    private String initials(String first, String last) {
        String a = first != null && !first.isBlank() ? first.substring(0, 1).toUpperCase(Locale.ROOT) : "";
        String b = last != null && !last.isBlank() ? last.substring(0, 1).toUpperCase(Locale.ROOT) : "";
        return a + b;
    }

    private String getAppreciation(double moyenne) {
        if (moyenne >= 16) return "Tres bien";
        if (moyenne >= 14) return "Bien";
        if (moyenne >= 12) return "Assez bien";
        return "A renforcer";
    }

    private void activate(Button active) {
        for (Button button : List.of(dashboardNavButton, coursNavButton, notesNavButton, devoirsNavButton, emploisNavButton, absencesNavButton, messagesNavButton, profilNavButton)) {
            button.getStyleClass().remove("soft-nav-button-active");
        }
        if (active != null && !active.getStyleClass().contains("soft-nav-button-active")) {
            active.getStyleClass().add("soft-nav-button-active");
        }
    }
}
