package com.school.gestion.controller;

import com.school.gestion.model.Classe;
import com.school.gestion.model.Eleve;
import com.school.gestion.model.Enseignant;
import com.school.gestion.model.FraisScolarite;
import com.school.gestion.model.MessageInterne;
import com.school.gestion.model.Note;
import com.school.gestion.model.Parent;
import com.school.gestion.model.PresenceRecord;
import com.school.gestion.model.Seance;
import com.school.gestion.service.SchoolService;
import com.school.gestion.util.AlertUtils;
import com.school.gestion.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ParentDashboardController {
    public Label headerTitle;
    public Label headerSubtitle;
    public Label contextBadge;
    public Label userLabel;
    public Label profileRoleLabel;
    public VBox contentArea;
    public Button dashboardNavButton;
    public Button elevesNavButton;
    public Button coursNavButton;
    public Button notesNavButton;
    public Button devoirsNavButton;
    public Button emploisNavButton;
    public Button absencesNavButton;
    public Button communicationsNavButton;
    public Button facturationNavButton;
    public Button parametresNavButton;

    private Parent currentParent;
    private List<Eleve> linkedStudents = new ArrayList<>();
    private Eleve selectedStudent;
    private int selectedTrimestre = 1;
    private String currentView = "dashboard";

    public void initialize() {
        currentParent = SessionManager.getCurrentUser() != null
            ? SchoolService.getParentByUserId(SessionManager.getCurrentUser().getId())
            : null;
        linkedStudents = currentParent != null ? SchoolService.getElevesByParent(currentParent.getIdParent()) : new ArrayList<>();
        selectedStudent = linkedStudents.isEmpty() ? null : linkedStudents.get(0);

        String parentName = currentParent != null ? currentParent.getNomComplet() : "Parent";
        userLabel.setText(parentName);
        profileRoleLabel.setText("Parent");
        headerTitle.setText("Bonjour " + parentName);
        headerSubtitle.setText("Voici un apercu de l'activite scolaire aujourd'hui.");
        contextBadge.setText("Parent");
        navigateToDashboard();
    }

    public void navigateToDashboard() {
        currentView = "dashboard";
        headerSubtitle.setText("Voici un apercu de l'activite scolaire aujourd'hui.");
        contextBadge.setText("Tableau de bord");
        showDashboard();
        activate(dashboardNavButton);
    }

    public void navigateToEleves() {
        currentView = "eleves";
        headerSubtitle.setText("Consultez les informations completes des enfants lies a votre compte.");
        contextBadge.setText("Eleves");
        showElevesView();
        activate(elevesNavButton);
    }

    public void navigateToCours() {
        currentView = "cours";
        headerSubtitle.setText("Retrouvez les cours suivis et les matieres du moment.");
        contextBadge.setText("Cours");
        showCoursView();
        activate(coursNavButton);
    }

    public void navigateToNotes() {
        currentView = "notes";
        headerSubtitle.setText("Visualisez les resultats detailes par matiere.");
        contextBadge.setText("Notes");
        showNotesView();
        activate(notesNavButton);
    }

    public void navigateToDevoirs() {
        currentView = "devoirs";
        headerSubtitle.setText("Suivez les devoirs et les rappels pedagogiques.");
        contextBadge.setText("Devoirs");
        showDevoirsView();
        activate(devoirsNavButton);
    }

    public void navigateToEmploi() {
        currentView = "emploi";
        headerSubtitle.setText("Parcourez l'emploi du temps complet.");
        contextBadge.setText("Emploi du temps");
        showEmploiView();
        activate(emploisNavButton);
    }

    public void navigateToAbsences() {
        currentView = "absences";
        headerSubtitle.setText("Consultez les absences et les justifications.");
        contextBadge.setText("Absences");
        showAbsencesView();
        activate(absencesNavButton);
    }

    public void navigateToCommunications() {
        currentView = "communications";
        headerSubtitle.setText("Lisez les communications des enseignants et de l'etablissement.");
        contextBadge.setText("Communications");
        showCommunicationsView();
        activate(communicationsNavButton);
    }

    public void navigateToFacturation() {
        currentView = "facturation";
        headerSubtitle.setText("Suivi simplifie des frais et paiements.");
        contextBadge.setText("Facturation");
        showFacturationView();
        activate(facturationNavButton);
    }

    public void navigateToParametres() {
        currentView = "parametres";
        headerSubtitle.setText("Ajustez les preferences du compte parent.");
        contextBadge.setText("Parametres");
        showParametresView();
        activate(parametresNavButton);
    }

    public void handleLogout() {
        SessionManager.logout();
    }

    public void handleQuickSearch() {
        showSearchView();
        activate(null);
        contextBadge.setText("Recherche");
    }

    public void handleQuickNotifications() {
        showNotificationsView();
        activate(null);
        contextBadge.setText("Notifications");
    }

    public void handleQuickMessages() {
        navigateToCommunications();
    }

    private void showDashboard() {
        currentView = "dashboard";
        contentArea.getChildren().clear();

        HBox top = new HBox(20);
        VBox center = new VBox(20);
        VBox right = new VBox(20);
        HBox.setHgrow(center, Priority.ALWAYS);
        center.getChildren().add(buildStudentSelector());
        center.getChildren().add(buildMiddleRow());
        center.getChildren().add(buildMessagesCard());
        right.getChildren().add(buildUpcomingCoursesCard());
        right.getChildren().add(buildEventsCard());
        top.getChildren().addAll(buildProfileCard(), center, right);
        contentArea.getChildren().add(top);
    }

    private void showElevesView() {
        currentView = "eleves";
        contentArea.getChildren().clear();
        VBox page = new VBox(20);
        page.getChildren().add(buildInvitationLinkCard());
        page.getChildren().add(buildStudentSelector());
        page.getChildren().add(buildProfileCard());
        page.getChildren().add(buildLinkedStudentsOverview());
        contentArea.getChildren().add(page);
    }

    private void showCoursView() {
        currentView = "cours";
        contentArea.getChildren().clear();
        VBox page = new VBox(20);
        page.getChildren().add(buildStudentSelector());
        page.getChildren().add(buildUpcomingCoursesCard());
        page.getChildren().add(buildCoursesSummaryCard());
        contentArea.getChildren().add(page);
    }

    private void showNotesView() {
        currentView = "notes";
        contentArea.getChildren().clear();
        VBox page = new VBox(20);
        page.getChildren().add(buildStudentSelector());
        page.getChildren().add(buildBulletinCard());
        contentArea.getChildren().add(page);
    }

    private void showDevoirsView() {
        currentView = "devoirs";
        contentArea.getChildren().clear();
        VBox card = baseCard("soft-card-wide", 20);
        card.getChildren().add(new Label("Devoirs et suivis"));
        card.getChildren().add(messageItem("M. Bennani", "Devoir de mathematiques", "Exercices 3, 4 et 5 a remettre lundi.", "A rendre"));
        card.getChildren().add(messageItem("Mme Idrissi", "Lecture d'histoire", "Relire le chapitre sur les civilisations africaines.", "Cette semaine"));
        contentArea.getChildren().setAll(card);
    }

    private void showEmploiView() {
        currentView = "emploi";
        contentArea.getChildren().clear();
        VBox page = new VBox(20);
        page.getChildren().add(buildStudentSelector());
        page.getChildren().add(buildUpcomingCoursesCard());
        page.getChildren().add(buildFullScheduleCard());
        contentArea.getChildren().add(page);
    }

    private void showAbsencesView() {
        currentView = "absences";
        contentArea.getChildren().clear();
        VBox page = new VBox(20);
        page.getChildren().add(buildStudentSelector());
        page.getChildren().add(buildAbsencesCard());
        contentArea.getChildren().setAll(page);
    }

    private void showCommunicationsView() {
        currentView = "communications";
        contentArea.getChildren().clear();
        VBox page = new VBox(20);
        page.getChildren().add(buildStudentSelector());
        page.getChildren().add(buildRealMessagesCard());
        contentArea.getChildren().setAll(page);
    }

    private void showFacturationView() {
        currentView = "facturation";
        contentArea.getChildren().clear();
        VBox page = new VBox(20);
        page.getChildren().add(buildStudentSelector());
        page.getChildren().add(buildFacturationCard());
        contentArea.getChildren().setAll(page);
    }

    private void showParametresView() {
        currentView = "parametres";
        contentArea.getChildren().clear();
        VBox card = baseCard("soft-card-wide", 20);
        card.getChildren().add(new Label("Parametres du compte"));
        card.getChildren().add(detailLine("Compte", currentParent != null ? currentParent.getNomComplet() : "Parent"));
        card.getChildren().add(detailLine("Email", currentParent != null ? currentParent.getEmail() : "-"));
        card.getChildren().add(detailLine("Telephone", currentParent != null ? currentParent.getTelephone() : "-"));
        TextField passwordField = new TextField();
        passwordField.setPromptText("Nouveau mot de passe");
        Button passwordButton = new Button("Mettre a jour le mot de passe");
        passwordButton.getStyleClass().add("soft-outline-button");
        passwordButton.setOnAction(e -> {
            if (SessionManager.getCurrentUser() == null || passwordField.getText().isBlank()) {
                AlertUtils.showWarning("Mot de passe manquant", "Saisissez un nouveau mot de passe.");
                return;
            }
            if (SchoolService.updateOwnPassword(SessionManager.getCurrentUser().getId(), passwordField.getText().trim())) {
                passwordField.clear();
                AlertUtils.showInfo("Succes", "Mot de passe mis a jour.");
            } else {
                AlertUtils.showError("Erreur", "Impossible de mettre a jour le mot de passe.");
            }
        });
        card.getChildren().addAll(passwordField, passwordButton);
        contentArea.getChildren().setAll(card);
    }

    private void showSearchView() {
        currentView = "search";
        contentArea.getChildren().clear();
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Recherche globale");
        title.getStyleClass().add("soft-section-title");
        TextField queryField = new TextField();
        queryField.setPromptText("Rechercher un eleve, une matiere, un message...");
        VBox results = new VBox(10);

        Runnable refresh = () -> {
            results.getChildren().clear();
            String q = queryField.getText() == null ? "" : queryField.getText().trim().toLowerCase(Locale.ROOT);
            linkedStudents.stream()
                .filter(e -> q.isBlank() || e.getNomComplet().toLowerCase(Locale.ROOT).contains(q) || e.getMatricule().toLowerCase(Locale.ROOT).contains(q))
                .forEach(e -> {
                    Classe classe = SchoolService.getClasseForEleve(e.getMatricule());
                    HBox row = messageItem("Eleve", e.getNomComplet(), "Matricule " + e.getMatricule(), classe != null ? classe.getNomComplet() : "-");
                    row.setOnMouseClicked(ev -> {
                        selectedStudent = e;
                        navigateToEleves();
                    });
                    results.getChildren().add(row);
                });
            if (selectedStudent != null) {
                Classe classe = SchoolService.getClasseForEleve(selectedStudent.getMatricule());
                List<Seance> seances = classe == null ? List.of() : SchoolService.getSeancesByClasse(classe.getIdClasse());
                seances.stream()
                    .filter(s -> q.isBlank()
                        || s.getMatiereLibelle().toLowerCase(Locale.ROOT).contains(q)
                        || s.getJour().toLowerCase(Locale.ROOT).contains(q))
                    .limit(8)
                    .forEach(s -> results.getChildren().add(messageItem("Cours", s.getMatiereLibelle(), s.getJour(), s.getHeureDebut() + " - " + s.getHeureFin())));
            }
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
        currentView = "notifications";
        contentArea.getChildren().clear();
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Centre de notifications");
        title.getStyleClass().add("soft-section-title");

        int messageCount = SchoolService.getMessagesForUser("PARENT", currentParent != null ? currentParent.getIdParent() : null,
            selectedStudent != null ? selectedStudent.getMatricule() : null).size();
        long absenceCount = selectedStudent == null ? 0 : SchoolService.getPresencesByEleve(selectedStudent.getMatricule()).stream()
            .filter(p -> !"PRESENT".equalsIgnoreCase(p.getStatut())).count();
        int unpaidCount = selectedStudent == null ? 0 : (int) SchoolService.getFraisByEleve(selectedStudent.getMatricule()).stream()
            .filter(f -> !"PAYE".equalsIgnoreCase(f.getStatut())).count();

        Button messages = new Button("Messages enseignants : " + messageCount);
        messages.getStyleClass().add("soft-outline-button");
        messages.setOnAction(e -> navigateToCommunications());
        Button absences = new Button("Absences / retards : " + absenceCount);
        absences.getStyleClass().add("soft-outline-button");
        absences.setOnAction(e -> navigateToAbsences());
        Button frais = new Button("Frais a suivre : " + unpaidCount);
        frais.getStyleClass().add("soft-outline-button");
        frais.setOnAction(e -> navigateToFacturation());

        card.getChildren().addAll(title, messages, absences, frais);
        contentArea.getChildren().setAll(card);
    }

    private VBox buildStudentSelector() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(4, 4, 0, 4));
        if (!linkedStudents.isEmpty()) {
            ComboBox<Eleve> combo = new ComboBox<>(FXCollections.observableArrayList(linkedStudents));
            combo.setValue(selectedStudent);
            combo.setMaxWidth(280);
            combo.setOnAction(e -> {
                selectedStudent = combo.getValue();
                refreshCurrentView();
            });
            box.getChildren().add(combo);
        }
        return box;
    }

    private VBox buildProfileCard() {
        VBox card = baseCard("soft-card-profile", 24);
        card.setPrefWidth(330);
        if (selectedStudent == null) {
            card.getChildren().add(new Label("Aucun eleve lie."));
            return card;
        }

        StackPane avatar = new StackPane(new Label(initials(selectedStudent.getPrenom(), selectedStudent.getNom())));
        avatar.getStyleClass().add("soft-avatar-large");

        Classe classe = SchoolService.getClasseForEleve(selectedStudent.getMatricule());
        String classeNom = classe != null ? classe.getNomComplet() : "-";

        Label name = new Label(selectedStudent.getNomComplet());
        name.getStyleClass().add("soft-card-title");
        Label classeLabel = new Label("Classe : " + classeNom);
        classeLabel.getStyleClass().add("soft-highlight-text");
        Label birth = new Label("Ne le " + String.valueOf(selectedStudent.getDateNaissance()));
        birth.getStyleClass().add("soft-muted");

        card.getChildren().addAll(avatar, name, classeLabel, birth, sectionDivider());
        card.getChildren().add(detailLine("Matricule", selectedStudent.getMatricule()));
        card.getChildren().add(detailLine("Annee scolaire", SchoolService.getActiveAnneeScolaire() != null ? SchoolService.getActiveAnneeScolaire().getAnnee() : "-"));
        card.getChildren().add(detailLine("Statut", "Actif"));
        Button button = new Button("Voir le profil complet");
        button.getStyleClass().add("soft-outline-button");
        button.setOnAction(e -> navigateToEleves());
        card.getChildren().add(button);
        return card;
    }

    private VBox buildMiddleRow() {
        VBox wrapper = new VBox(20);
        wrapper.getChildren().add(buildBulletinCard());
        return wrapper;
    }

    private VBox buildBulletinCard() {
        VBox card = baseCard("soft-card-main", 22);
        if (selectedStudent == null) {
            card.getChildren().add(new Label("Aucun bulletin disponible."));
            return card;
        }

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Bulletin de " + selectedStudent.getPrenom());
        title.getStyleClass().add("soft-section-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        ComboBox<Integer> trimestre = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3));
        trimestre.setValue(selectedTrimestre);
        trimestre.setOnAction(e -> {
            if (trimestre.getValue() != null) {
                selectedTrimestre = trimestre.getValue();
                refreshCurrentView();
            }
        });
        header.getChildren().addAll(title, spacer, trimestre);

        GridPane notesGrid = new GridPane();
        notesGrid.getStyleClass().add("soft-notes-grid");
        notesGrid.setHgap(14);
        notesGrid.setVgap(10);
        addHeader(notesGrid, "Matiere", 0);
        addHeader(notesGrid, "Moyenne", 1);
        addHeader(notesGrid, "Appreciation", 2);

        Classe classe = SchoolService.getClasseForEleve(selectedStudent.getMatricule());
        List<Note> notes = classe == null ? List.of() : SchoolService.getNotesByClasseAndTrimestre(classe.getIdClasse(), selectedTrimestre).stream()
            .filter(note -> selectedStudent.getMatricule().equals(note.getMatricule()))
            .toList();

        int row = 1;
        double total = 0.0;
        for (Note note : notes) {
            double moyenne = note.getMoyenne();
            total += moyenne;
            notesGrid.add(bodyCell(note.getCodeMatiere()), 0, row);
            notesGrid.add(bodyCell(String.format(Locale.US, "%.2f", moyenne)), 1, row);
            notesGrid.add(appreciationCell(getAppreciation(moyenne)), 2, row);
            row++;
        }

        double moyenneGenerale = notes.isEmpty() ? 0.0 : total / notes.size();
        HBox summary = new HBox(24);
        summary.getChildren().add(summaryPill("Moyenne generale", String.format(Locale.US, "%.2f/20", moyenneGenerale)));
        summary.getChildren().add(summaryPill("Rang", computeRankText(selectedStudent, classe, selectedTrimestre)));
        summary.getChildren().add(summaryPill("Effectif", classe != null ? classe.getEffectifActuel() + " eleves" : "-"));

        Button button = new Button("Voir le bulletin complet");
        button.getStyleClass().add("soft-outline-button");
        button.setOnAction(e -> navigateToNotes());
        card.getChildren().addAll(header, notesGrid, summary, button);
        return card;
    }

    private VBox buildUpcomingCoursesCard() {
        VBox card = baseCard("soft-card-side", 20);
        Label title = new Label("Prochains cours");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);

        if (selectedStudent == null) {
            card.getChildren().add(new Label("Aucun cours."));
            return card;
        }

        Classe classe = SchoolService.getClasseForEleve(selectedStudent.getMatricule());
        List<Seance> seances = classe == null ? List.of() : SchoolService.getSeancesByClasse(classe.getIdClasse());
        seances.stream().sorted(Comparator.comparing(Seance::getJour).thenComparing(Seance::getHeureDebut)).limit(4)
            .forEach(seance -> card.getChildren().add(courseItem(seance)));

        Button button = new Button("Voir tout l'emploi du temps");
        button.getStyleClass().add("soft-link-button");
        button.setOnAction(e -> navigateToEmploi());
        card.getChildren().add(button);
        return card;
    }

    private VBox buildMessagesCard() {
        VBox card = baseCard("soft-card-wide", 20);
        HBox head = new HBox();
        Label title = new Label("Messages des enseignants");
        title.getStyleClass().add("soft-section-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button button = new Button("Voir tous les messages");
        button.getStyleClass().add("soft-link-button");
        button.setOnAction(e -> navigateToCommunications());
        head.getChildren().addAll(title, spacer, button);
        card.getChildren().add(head);

        if (selectedStudent == null) {
            card.getChildren().add(new Label("Aucun message."));
            return card;
        }

        card.getChildren().add(messageItem("M. Bennani", "Devoir de mathematiques", "Le devoir maison sur les equations est a rendre pour lundi prochain.", "Il y a 2 h"));
        card.getChildren().add(messageItem("Mme Idrissi", "Projet histoire", "N'oubliez pas le projet de groupe sur les grandes civilisations.", "Il y a 1 j"));
        card.getChildren().add(messageItem("M. Alaoui", "Absence en classe", "Merci de justifier l'absence en SVT si necessaire.", "Il y a 2 j"));
        return card;
    }

    private VBox buildRealMessagesCard() {
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Messagerie interne");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);

        List<MessageInterne> messages = SchoolService.getMessagesForUser(
            "PARENT",
            currentParent != null ? currentParent.getIdParent() : null,
            selectedStudent != null ? selectedStudent.getMatricule() : null
        );
        if (messages.isEmpty()) {
            card.getChildren().add(new Label("Aucun message pour le moment."));
        } else {
            messages.stream().limit(8).forEach(message ->
                card.getChildren().add(messageItem(
                    message.getExpediteurNom(),
                    message.getSujet(),
                    message.getContenu(),
                    message.getDateEnvoi() != null ? message.getDateEnvoi().toLocalDate().toString() : "-"
                ))
            );
        }

        TextField sujetField = new TextField();
        sujetField.setPromptText("Sujet");
        TextArea contenuArea = new TextArea();
        contenuArea.setPromptText("Votre message a la direction ou au professeur principal...");
        contenuArea.setPrefRowCount(3);
        Button adminButton = new Button("Envoyer a l'administration");
        adminButton.getStyleClass().add("soft-outline-button");
        adminButton.setOnAction(e -> sendParentMessage("ADMIN", 1, sujetField, contenuArea));
        Button teacherButton = new Button("Envoyer au professeur principal");
        teacherButton.getStyleClass().add("soft-outline-button");
        teacherButton.setOnAction(e -> {
            Classe classe = selectedStudent != null ? SchoolService.getClasseForEleve(selectedStudent.getMatricule()) : null;
            if (classe == null || classe.getMatriculeEnseignant() == null) {
                AlertUtils.showWarning("Classe manquante", "Impossible d'identifier le professeur principal.");
                return;
            }
            Enseignant principal = SchoolService.getAllEnseignants().stream()
                .filter(enseignant -> classe.getMatriculeEnseignant().equals(enseignant.getMatricule()))
                .findFirst()
                .orElse(null);
            if (principal == null || principal.getIdUtilisateur() == null) {
                AlertUtils.showWarning("Enseignant manquant", "Aucun compte enseignant relie a cette classe.");
                return;
            }
            sendParentMessage("ENSEIGNANT", principal.getIdUtilisateur(), sujetField, contenuArea);
        });
        HBox actions = new HBox(12, adminButton, teacherButton);
        card.getChildren().addAll(sujetField, contenuArea, actions);
        return card;
    }

    private VBox buildEventsCard() {
        VBox card = baseCard("soft-card-side", 20);
        Label title = new Label("Evenements a venir");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        card.getChildren().add(eventItem("24", "MAI", "Reunion Parents-Professeurs", "15:00 - 17:00"));
        card.getChildren().add(eventItem("28", "MAI", "Sortie pedagogique", "08:00 - 16:00"));
        card.getChildren().add(eventItem("05", "JUIN", "Examens du 3eme trimestre", "Toute la journee"));
        return card;
    }

    private VBox buildLinkedStudentsOverview() {
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Mes enfants");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        for (Eleve eleve : linkedStudents) {
            Classe classe = SchoolService.getClasseForEleve(eleve.getMatricule());
            card.getChildren().add(messageItem(
                eleve.getNomComplet(),
                "Classe " + (classe != null ? classe.getNomComplet() : "-"),
                "Matricule : " + eleve.getMatricule(),
                eleve.getNiveau()
            ));
        }
        return card;
    }

    private VBox buildInvitationLinkCard() {
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Lier un enfant avec un code");
        title.getStyleClass().add("soft-section-title");
        Label hint = new Label("Saisissez le code fourni par l'administration pour ajouter un enfant a votre compte.");
        hint.getStyleClass().add("soft-muted");
        TextField codeField = new TextField();
        codeField.setPromptText("INV-XXXXXXXX");
        codeField.setPrefWidth(220);
        Button linkButton = new Button("Lier le compte");
        linkButton.getStyleClass().add("soft-outline-button");
        linkButton.setOnAction(e -> {
            if (currentParent == null) {
                AlertUtils.showError("Compte parent manquant", "Aucun profil parent n'est associe a ce compte.");
                return;
            }
            String result = SchoolService.linkParentWithInvitation(currentParent.getIdParent(), codeField.getText());
            if (result == null || "DEMANDE_EN_ATTENTE".equals(result)) {
                linkedStudents = SchoolService.getElevesByParent(currentParent.getIdParent());
                selectedStudent = linkedStudents.isEmpty() ? null : linkedStudents.get(0);
                codeField.clear();
                AlertUtils.showInfo("Demande envoyee", "Votre demande de liaison est en attente de validation par l'administration.");
                showElevesView();
            } else {
                AlertUtils.showWarning("Code invalide", result);
            }
        });
        HBox row = new HBox(12, codeField, linkButton);
        row.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().addAll(title, hint, row);
        return card;
    }

    private VBox buildCoursesSummaryCard() {
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Matieres suivies");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        if (selectedStudent == null) {
            card.getChildren().add(new Label("Aucune information disponible."));
            return card;
        }
        Classe classe = SchoolService.getClasseForEleve(selectedStudent.getMatricule());
        List<Note> notes = classe == null ? List.of() : SchoolService.getNotesByClasseAndTrimestre(classe.getIdClasse(), 1).stream()
            .filter(note -> selectedStudent.getMatricule().equals(note.getMatricule()))
            .toList();
        if (notes.isEmpty()) {
            card.getChildren().add(new Label("Aucune matiere disponible pour le moment."));
        } else {
            notes.forEach(note -> card.getChildren().add(detailLine(note.getCodeMatiere(), getAppreciation(note.getMoyenne()))));
        }
        return card;
    }

    private VBox buildAbsencesCard() {
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Absences et retards");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        if (selectedStudent == null) {
            card.getChildren().add(new Label("Aucun eleve lie."));
            return card;
        }
        List<PresenceRecord> presences = SchoolService.getPresencesByEleve(selectedStudent.getMatricule());
        List<PresenceRecord> incidents = presences.stream()
            .filter(presence -> !"PRESENT".equalsIgnoreCase(presence.getStatut()))
            .toList();
        if (incidents.isEmpty()) {
            card.getChildren().add(new Label("Aucune absence ni retard a signaler."));
            return card;
        }
        incidents.forEach(presence -> {
            VBox line = baseCard("soft-list-item", 12);
            line.getChildren().add(detailLine(
                presence.getMatiereLibelle() + " - " + presence.getJourSeance(),
                presence.getStatut() + "  " + (presence.getHoraireSeance() == null ? "" : presence.getHoraireSeance())
            ));
            line.getChildren().add(new Label("Remarque : " + (presence.getRemarque() == null || presence.getRemarque().isBlank() ? "-" : presence.getRemarque())));
            if (presence.getJustificationParent() == null || presence.getJustificationParent().isBlank()) {
                TextField justificationField = new TextField();
                justificationField.setPromptText("Justifier en ligne");
                Button justifyBtn = new Button("Envoyer la justification");
                justifyBtn.getStyleClass().add("soft-outline-button");
                justifyBtn.setOnAction(e -> {
                    if (justificationField.getText().isBlank()) {
                        AlertUtils.showWarning("Justification vide", "Saisissez une justification.");
                        return;
                    }
                    if (SchoolService.justifyAbsence(presence.getId(), justificationField.getText().trim())) {
                        AlertUtils.showInfo("Succes", "Justification enregistree.");
                        showAbsencesView();
                    } else {
                        AlertUtils.showError("Erreur", "Impossible d'enregistrer la justification.");
                    }
                });
                line.getChildren().add(new HBox(10, justificationField, justifyBtn));
            } else {
                line.getChildren().add(new Label("Justification : " + presence.getJustificationParent()));
            }
            card.getChildren().add(line);
        });
        return card;
    }

    private VBox buildFacturationCard() {
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Frais de scolarite");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        if (selectedStudent == null) {
            card.getChildren().add(new Label("Aucun eleve selectionne."));
            return card;
        }
        List<FraisScolarite> frais = SchoolService.getFraisByEleve(selectedStudent.getMatricule());
        if (frais.isEmpty()) {
            card.getChildren().add(new Label("Aucun frais enregistre pour cet eleve."));
            return card;
        }
        frais.forEach(item -> {
            VBox row = baseCard("soft-list-item", 12);
            row.getChildren().add(detailLine(item.getLibelle(), String.format(Locale.US, "%.2f MAD", item.getMontant())));
            row.getChildren().add(detailLine("Echeance", item.getDateEcheance() != null ? item.getDateEcheance().toString() : "-"));
            row.getChildren().add(detailLine("Statut", item.getStatut()));
            if (!"PAYE".equalsIgnoreCase(item.getStatut())) {
                Button payBtn = new Button("Marquer comme paye");
                payBtn.getStyleClass().add("soft-outline-button");
                payBtn.setOnAction(e -> {
                    if (SchoolService.markFraisAsPaid(item.getId())) {
                        AlertUtils.showInfo("Paiement enregistre", "Le suivi du paiement a ete mis a jour.");
                        showFacturationView();
                    } else {
                        AlertUtils.showError("Erreur", "Impossible de mettre a jour le paiement.");
                    }
                });
                row.getChildren().add(payBtn);
            }
            card.getChildren().add(row);
        });
        return card;
    }

    private VBox buildFullScheduleCard() {
        VBox card = baseCard("soft-card-wide", 20);
        Label title = new Label("Emploi du temps complet");
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        if (selectedStudent == null) {
            card.getChildren().add(new Label("Aucun emploi du temps disponible."));
            return card;
        }
        Classe classe = SchoolService.getClasseForEleve(selectedStudent.getMatricule());
        List<Seance> seances = classe == null ? List.of() : SchoolService.getSeancesByClasse(classe.getIdClasse());
        seances.stream()
            .sorted(Comparator.comparing(Seance::getJour).thenComparing(Seance::getHeureDebut))
            .forEach(seance -> card.getChildren().add(messageItem(
                seance.getJour(),
                seance.getMatiereLibelle(),
                seance.getHeureDebut() + " - " + seance.getHeureFin(),
                seance.getClasseNom()
            )));
        return card;
    }

    private void refreshCurrentView() {
        switch (currentView) {
            case "eleves" -> showElevesView();
            case "cours" -> showCoursView();
            case "notes" -> showNotesView();
            case "devoirs" -> showDevoirsView();
            case "emploi" -> showEmploiView();
            case "absences" -> showAbsencesView();
            case "communications" -> showCommunicationsView();
            case "facturation" -> showFacturationView();
            case "parametres" -> showParametresView();
            case "search" -> showSearchView();
            case "notifications" -> showNotificationsView();
            default -> showDashboard();
        }
    }

    private VBox baseCard(String styleClass, double padding) {
        VBox box = new VBox(16);
        box.getStyleClass().add(styleClass);
        box.setPadding(new Insets(padding));
        return box;
    }

    private Region sectionDivider() {
        Region region = new Region();
        region.setMinHeight(1);
        region.getStyleClass().add("soft-divider");
        return region;
    }

    private HBox detailLine(String labelText, String valueText) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.getStyleClass().add("soft-detail-label");
        Label value = new Label(valueText);
        value.getStyleClass().add("soft-detail-value");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(label, spacer, value);
        return row;
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

    private HBox appreciationCell(String text) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label dot = new Label("•");
        dot.getStyleClass().add("soft-dot");
        Label label = bodyCell(text);
        row.getChildren().addAll(dot, label);
        return row;
    }

    private VBox summaryPill(String labelText, String valueText) {
        VBox pill = new VBox(4);
        pill.getStyleClass().add("soft-summary-pill");
        Label label = new Label(labelText);
        label.getStyleClass().add("soft-summary-label");
        Label value = new Label(valueText);
        value.getStyleClass().add("soft-summary-value");
        pill.getChildren().addAll(label, value);
        return pill;
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

    private HBox messageItem(String author, String subject, String body, String when) {
        HBox row = new HBox(14);
        row.getStyleClass().add("soft-message-item");
        StackPane avatar = new StackPane(new Label(author.substring(0, 1)));
        avatar.getStyleClass().add("soft-avatar-small");
        VBox content = new VBox(new Label(author), new Label(subject), new Label(body));
        content.getStyleClass().add("soft-message-content");
        Label date = new Label(when);
        date.getStyleClass().add("soft-muted");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(avatar, content, spacer, date);
        return row;
    }

    private HBox eventItem(String day, String month, String titleText, String time) {
        HBox row = new HBox(14);
        row.getStyleClass().add("soft-event-item");
        VBox dateBox = new VBox(new Label(day), new Label(month));
        dateBox.getStyleClass().add("soft-event-date");
        VBox content = new VBox(new Label(titleText), new Label(time));
        content.getStyleClass().add("soft-event-content");
        row.getChildren().addAll(dateBox, content);
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

    private String computeRankText(Eleve eleve, Classe classe, int trimestre) {
        if (eleve == null || classe == null) {
            return "-";
        }
        List<Eleve> classStudents = SchoolService.getElevesByClasse(classe.getIdClasse());
        List<Note> notes = SchoolService.getNotesByClasseAndTrimestre(classe.getIdClasse(), trimestre);
        List<Double> averages = new ArrayList<>();
        double currentAverage = 0.0;
        for (Eleve student : classStudents) {
            List<Note> studentNotes = notes.stream().filter(n -> student.getMatricule().equals(n.getMatricule())).toList();
            double avg = studentNotes.isEmpty() ? 0.0 : studentNotes.stream().mapToDouble(Note::getMoyenne).average().orElse(0.0);
            averages.add(avg);
            if (student.getMatricule().equals(eleve.getMatricule())) {
                currentAverage = avg;
            }
        }
        averages.sort(Comparator.reverseOrder());
        int rank = averages.indexOf(currentAverage) + 1;
        return rank + " / " + classStudents.size();
    }

    private void sendParentMessage(String destinataireRole, Integer destinataireId, TextField sujetField, TextArea contenuArea) {
        if (currentParent == null || sujetField.getText().isBlank() || contenuArea.getText().isBlank()) {
            AlertUtils.showWarning("Champs requis", "Saisissez un sujet et un message.");
            return;
        }
        boolean success = SchoolService.sendMessage(
            "PARENT",
            currentParent.getIdParent(),
            currentParent.getNomComplet(),
            destinataireRole,
            destinataireId,
            selectedStudent != null ? selectedStudent.getMatricule() : null,
            sujetField.getText().trim(),
            contenuArea.getText().trim()
        );
        if (success) {
            sujetField.clear();
            contenuArea.clear();
            AlertUtils.showInfo("Succes", "Message envoye.");
            showCommunicationsView();
        } else {
            AlertUtils.showError("Erreur", "Impossible d'envoyer le message.");
        }
    }

    private void activate(Button active) {
        for (Button button : List.of(dashboardNavButton, elevesNavButton, coursNavButton, notesNavButton, devoirsNavButton,
            emploisNavButton, absencesNavButton, communicationsNavButton, facturationNavButton, parametresNavButton)) {
            button.getStyleClass().remove("soft-nav-button-active");
        }
        if (active != null && !active.getStyleClass().contains("soft-nav-button-active")) {
            active.getStyleClass().add("soft-nav-button-active");
        }
    }
}
