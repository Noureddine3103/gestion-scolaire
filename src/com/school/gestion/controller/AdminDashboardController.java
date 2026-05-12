package com.school.gestion.controller;

import com.school.gestion.database.DatabaseConnection;
import com.school.gestion.model.AnneeScolaire;
import com.school.gestion.model.AffectationEnseignement;
import com.school.gestion.model.Classe;
import com.school.gestion.model.ConfigurationScolaire;
import com.school.gestion.model.Eleve;
import com.school.gestion.model.Enseignant;
import com.school.gestion.model.FraisScolarite;
import com.school.gestion.model.Inscription;
import com.school.gestion.model.InvitationCode;
import com.school.gestion.model.Matiere;
import com.school.gestion.model.Note;
import com.school.gestion.model.Parent;
import com.school.gestion.model.ParentEleveLink;
import com.school.gestion.model.PeriodeScolaire;
import com.school.gestion.model.Seance;
import com.school.gestion.model.Utilisateur;
import com.school.gestion.service.DashboardAssistantService;
import com.school.gestion.service.SchoolService;
import com.school.gestion.util.AlertUtils;
import com.school.gestion.util.SessionManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.transform.Scale;
import javafx.util.converter.DoubleStringConverter;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminDashboardController {

    private static class CalendarState {
        private String mode = "MOIS";
        private YearMonth currentMonth = YearMonth.now();
        private LocalDate selectedDate = LocalDate.now();
    }

    public Label headerTitle;
    public Label contextBadge;
    public Label userLabel;
    public VBox contentArea;
    public VBox assistantMessages;
    public FlowPane assistantQuickActions;
    public TextField assistantInput;
    public Button dashboardNavButton;
    public Button elevesNavButton;
    public Button enseignantsNavButton;
    public Button usersNavButton;
    public Button classesNavButton;
    public Button matieresNavButton;
    public Button emploisNavButton;
    public Button inscriptionsNavButton;
    public Button notesNavButton;
    public Button bulletinsNavButton;

    public void initialize() {
        contentArea.setFillWidth(true);
        if (SessionManager.getCurrentUser() != null) {
            userLabel.setText("Administrateur  " + SessionManager.getCurrentUser().getUsername());
        }
        configureAssistant();
        showDashboardImageStyle();
        headerTitle.setText("Bonjour Administrateur");
        activateNav(dashboardNavButton, "Tableau de bord");
    }

    public void navigateToDashboard() {
        headerTitle.setText("Bonjour Administrateur");
        showDashboardImageStyle();
        activateNav(dashboardNavButton, "Tableau de bord");
    }

    public void navigateToEleves() {
        headerTitle.setText("Gestion des eleves");
        showElevesImageStyle();
        activateNav(elevesNavButton, "Eleves");
    }

    public void navigateToEnseignants() {
        headerTitle.setText("Gestion des enseignants");
        showEnseignantsImageStyle();
        activateNav(enseignantsNavButton, "Enseignants");
    }

    public void navigateToUsers() {
        headerTitle.setText("Comptes utilisateurs");
        showUsersView();
        activateNav(usersNavButton, "Comptes");
    }

    public void navigateToClasses() {
        headerTitle.setText("Classes et niveaux");
        showClassesImageStyle();
        activateNav(classesNavButton, "Classes");
    }

    public void navigateToMatieres() {
        headerTitle.setText("Catalogue des matieres");
        showMatieresImageStyle();
        activateNav(matieresNavButton, "Matieres");
    }

    public void navigateToInscriptions() {
        headerTitle.setText("Inscriptions et permutations");
        showInscriptionsView();
        activateNav(inscriptionsNavButton, "Inscriptions");
    }

    public void navigateToEmplois() {
        headerTitle.setText("Emplois du temps");
        showEmploisImageStyle();
        activateNav(emploisNavButton, "Emploi du temps");
    }

    public void navigateToNotes() {
        headerTitle.setText("Pilotage des notes");
        showNotesView();
        activateNav(notesNavButton, "Notes");
    }

    public void navigateToBulletins() {
        headerTitle.setText("Edition des bulletins");
        showBulletinsView();
        activateNav(bulletinsNavButton, "Bulletins");
    }

    public void handleLogout() {
        SessionManager.logout();
    }

    public void handleAssistantSend() {
        postAssistantQuestion(assistantInput == null ? "" : assistantInput.getText());
    }

    public void handleQuickSearch() {
        headerTitle.setText("Recherche globale");
        showQuickSearchView();
        activateNav(null, "Recherche");
    }

    public void handleQuickNotifications() {
        headerTitle.setText("Notifications");
        showQuickNotificationsView();
        activateNav(null, "Notifications");
    }

    public void handleQuickMessages() {
        handleQuickNotifications();
    }

    private void showQuickSearchView() {
        VBox page = new VBox(18);
        page.getStyleClass().add("erp-page");
        Label title = new Label("Recherche globale");
        title.getStyleClass().add("erp-page-title");

        VBox card = createPanel("Recherche", "Trouvez rapidement un eleve, un enseignant, une classe ou une matiere.");
        TextField searchField = new TextField();
        searchField.setPromptText("Tapez un nom, matricule, classe ou matiere...");
        VBox results = new VBox(10);
        results.getStyleClass().add("erp-card-lite");

        Runnable refresh = () -> {
            results.getChildren().clear();
            String q = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
            if (q.isBlank()) {
                results.getChildren().add(createErpHint("Commencez a taper pour afficher les resultats."));
                return;
            }
            SchoolService.getAllEleves().stream()
                .filter(e -> containsAny(q, e.getMatricule(), e.getNomComplet(), e.getNiveau()))
                .limit(6)
                .forEach(e -> results.getChildren().add(createSearchResult("Eleve", e.getMatricule() + " - " + e.getNomComplet(), () -> showEleveDetailsDialog(e))));
            SchoolService.getAllEnseignants().stream()
                .filter(e -> containsAny(q, e.getMatricule(), e.getNomComplet(), e.getGrade()))
                .limit(6)
                .forEach(e -> results.getChildren().add(createSearchResult("Enseignant", e.getMatricule() + " - " + e.getNomComplet(), () -> showEnseignantDetailsDialog(e))));
            SchoolService.getAllClasses().stream()
                .filter(c -> containsAny(q, c.getNomComplet(), c.getNiveau()))
                .limit(6)
                .forEach(c -> results.getChildren().add(createSearchResult("Classe", c.getNomComplet(), this::navigateToClasses)));
            SchoolService.getAllMatieres().stream()
                .filter(m -> containsAny(q, m.getCode(), m.getLibelle()))
                .limit(6)
                .forEach(m -> results.getChildren().add(createSearchResult("Matiere", m.getCode() + " - " + m.getLibelle(), this::navigateToMatieres)));
            if (results.getChildren().isEmpty()) {
                results.getChildren().add(createErpHint("Aucun resultat trouve."));
            }
        };
        searchField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        refresh.run();
        card.getChildren().addAll(searchField, results);
        page.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(page);
    }

    private void showQuickNotificationsView() {
        VBox page = new VBox(18);
        page.getStyleClass().add("erp-page");
        Label title = new Label("Centre de notifications");
        title.getStyleClass().add("erp-page-title");

        int invitations = SchoolService.getAllInvitationCodes().size();
        int demandesParent = SchoolService.getPendingParentEleveLinks().size();
        int seances = SchoolService.getAllSeances().size();
        int frais = SchoolService.getAllFraisScolarite().size();
        int messages = SessionManager.getCurrentUser() == null ? 0 : SchoolService.getMessagesForUser("ADMIN", SessionManager.getCurrentUser().getId(), null).size();

        VBox card = createPanel("Notifications administratives", "Synthese sans fenetre popup.");
        card.getChildren().addAll(
            createNotificationLine("Codes d'invitation generes", invitations, this::navigateToInscriptions),
            createNotificationLine("Demandes parent en attente", demandesParent, this::navigateToInscriptions),
            createNotificationLine("Seances planifiees", seances, this::navigateToEmplois),
            createNotificationLine("Frais de scolarite suivis", frais, this::navigateToBulletins),
            createNotificationLine("Messages direction", messages, this::navigateToInscriptions)
        );
        page.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(page);
    }

    private boolean containsAny(String query, String... values) {
        for (String value : values) {
            if (value != null && value.toLowerCase(Locale.ROOT).contains(query)) {
                return true;
            }
        }
        return false;
    }

    private HBox createSearchResult(String type, String text, Runnable action) {
        Label badge = new Label(type);
        badge.getStyleClass().add("soft-badge");
        Label label = new Label(text);
        label.getStyleClass().add("erp-section");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button open = new Button("Ouvrir");
        open.getStyleClass().add("btn-secondary");
        open.setOnAction(e -> action.run());
        HBox row = new HBox(12, badge, label, spacer, open);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox createNotificationLine(String labelText, int count, Runnable action) {
        Label countLabel = new Label(String.valueOf(count));
        countLabel.getStyleClass().add("soft-user-avatar");
        Label label = new Label(labelText);
        label.getStyleClass().add("erp-section");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button open = new Button("Voir");
        open.getStyleClass().add("btn-secondary");
        open.setOnAction(e -> action.run());
        HBox row = new HBox(14, countLabel, label, spacer, open);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void showDashboard() {
        contentArea.getChildren().clear();

        AnneeScolaire activeYear = SchoolService.getActiveAnneeScolaire();
        List<Eleve> eleves = SchoolService.getAllEleves();
        List<Classe> classes = SchoolService.getAllClasses();
        List<Enseignant> enseignants = SchoolService.getAllEnseignants();

        VBox hero = createHeroBlock(
            "Tableau de commandement",
            "Une vue d'ensemble plus executive du cycle scolaire, des ressources pedagogiques et des classes a risque.",
            activeYear != null ? "Annee active  " + activeYear.getAnnee() : "Aucune annee active"
        );

        HBox metrics = new HBox(18,
            createMetricCard("Eleves", String.valueOf(eleves.size()), "Population scolaire totale"),
            createMetricCard("Classes", String.valueOf(classes.size()), "Cartographie pedagogique"),
            createMetricCard("Enseignants", String.valueOf(enseignants.size()), "Ressource encadrante")
        );

        VBox classPressure = createPanel("Capacite des classes", "Reperez en un coup d'oeil les sections chargees.");
        FlowPane classCards = new FlowPane();
        classCards.setHgap(14);
        classCards.setVgap(14);
        classes.stream()
            .sorted(Comparator.comparingDouble(Classe::getRemplissage).reversed())
            .limit(6)
            .forEach(classe -> classCards.getChildren().add(createClasseCard(classe)));
        if (classCards.getChildren().isEmpty()) {
            classCards.getChildren().add(createEmptyState("Aucune classe disponible."));
        }
        classPressure.getChildren().add(classCards);

        VBox quickInsights = createPanel("Insights d'administration", "Trois angles de lecture pour prioriser les actions.");
        HBox insightRow = new HBox(16,
            createMiniInsight("Classes quasi pleines", String.valueOf(classes.stream().filter(c -> c.getRemplissage() >= 0.85).count())),
            createMiniInsight("Niveaux configures", String.valueOf(SchoolService.getAllNiveaux().size())),
            createMiniInsight("Matieres actives", String.valueOf(SchoolService.getAllMatieres().size()))
        );
        quickInsights.getChildren().add(insightRow);

        VBox exportsPanel = createPanel("Exports et sauvegardes", "Produisez rapidement des fichiers CSV et une sauvegarde logique.");
        Button exportElevesBtn = new Button("Exporter les eleves (CSV)");
        exportElevesBtn.getStyleClass().add("btn");
        Button exportNotesBtn = new Button("Exporter les notes (CSV)");
        exportNotesBtn.getStyleClass().add("btn-secondary");
        Button backupBtn = new Button("Sauvegarder les donnees");
        backupBtn.getStyleClass().add("btn-secondary");
        exportElevesBtn.setOnAction(e -> exportAdminData("eleves"));
        exportNotesBtn.setOnAction(e -> exportAdminData("notes"));
        backupBtn.setOnAction(e -> exportAdminData("backup"));
        exportsPanel.getChildren().add(new HBox(12, exportElevesBtn, exportNotesBtn, backupBtn));

        contentArea.getChildren().addAll(hero, metrics, quickInsights, classPressure, exportsPanel);
    }

    private void showDashboardImageStyle() {
        contentArea.getChildren().clear();

        AnneeScolaire activeYear = SchoolService.getActiveAnneeScolaire();
        List<Eleve> eleves = SchoolService.getAllEleves();
        List<Classe> classes = SchoolService.getAllClasses();
        List<Enseignant> enseignants = SchoolService.getAllEnseignants();
        List<Matiere> matieres = SchoolService.getAllMatieres();

        VBox page = new VBox(18);
        page.getStyleClass().add("erp-page");

        HBox statsRow = new HBox(12);
        statsRow.getChildren().addAll(
            createErpStatCard("Eleves", String.valueOf(eleves.size()), "student"),
            createErpStatCard("Enseignants", String.valueOf(enseignants.size()), "employee"),
            createErpStatCard("Matieres", String.valueOf(matieres.size()), "subject"),
            createErpStatCard("Classes", String.valueOf(classes.size()), "classroom")
        );

        HBox mainRow = new HBox(16);
        VBox leftColumn = new VBox(16);
        leftColumn.setPrefWidth(820);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        leftColumn.getChildren().add(createErpCalendarCard(activeYear));

        VBox rightColumn = new VBox(16);
        rightColumn.setPrefWidth(320);
        rightColumn.getChildren().addAll(
            createErpActionGrid(),
            createErpDistributionCard(eleves)
        );

        mainRow.getChildren().addAll(leftColumn, rightColumn);
        page.getChildren().addAll(statsRow, mainRow);
        contentArea.getChildren().setAll(page);
    }

    private VBox createErpStatCard(String title, String value, String styleSuffix) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("erp-stat-card", "erp-stat-" + styleSuffix);
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("erp-stat-value");
        Label titleLabel = new Label("Total " + title);
        titleLabel.getStyleClass().add("erp-stat-title");
        card.getChildren().addAll(valueLabel, titleLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox createErpCalendarCard(AnneeScolaire activeYear) {
        CalendarState state = new CalendarState();
        List<Seance> seances = SchoolService.getAllSeances();

        VBox card = new VBox(16);
        card.getStyleClass().add("erp-card");

        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Calendrier academique");
        title.getStyleClass().add("erp-card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label periodLabel = new Label();
        periodLabel.getStyleClass().add("erp-card-subtitle");
        Button previousBtn = new Button("<");
        previousBtn.getStyleClass().add("btn-secondary");
        Button nextBtn = new Button(">");
        nextBtn.getStyleClass().add("btn-secondary");
        titleRow.getChildren().addAll(title, spacer, previousBtn, periodLabel, nextBtn);

        HBox chips = new HBox(8);
        Button monthBtn = createCalendarModeButton("Mois");
        Button weekBtn = createCalendarModeButton("Semaine");
        Button dayBtn = createCalendarModeButton("Jour");
        chips.getChildren().addAll(monthBtn, weekBtn, dayBtn);

        Label subtitle = new Label();
        subtitle.getStyleClass().add("erp-card-subtitle");
        VBox body = new VBox(12);

        previousBtn.setOnAction(e -> {
            if ("MOIS".equals(state.mode)) {
                state.currentMonth = state.currentMonth.minusMonths(1);
                state.selectedDate = state.currentMonth.atDay(1);
            } else if ("SEMAINE".equals(state.mode)) {
                state.selectedDate = state.selectedDate.minusWeeks(1);
                state.currentMonth = YearMonth.from(state.selectedDate);
            } else {
                state.selectedDate = state.selectedDate.minusDays(1);
                state.currentMonth = YearMonth.from(state.selectedDate);
            }
            refreshCalendarContent(body, subtitle, periodLabel, state, seances, activeYear, monthBtn, weekBtn, dayBtn);
        });

        nextBtn.setOnAction(e -> {
            if ("MOIS".equals(state.mode)) {
                state.currentMonth = state.currentMonth.plusMonths(1);
                state.selectedDate = state.currentMonth.atDay(1);
            } else if ("SEMAINE".equals(state.mode)) {
                state.selectedDate = state.selectedDate.plusWeeks(1);
                state.currentMonth = YearMonth.from(state.selectedDate);
            } else {
                state.selectedDate = state.selectedDate.plusDays(1);
                state.currentMonth = YearMonth.from(state.selectedDate);
            }
            refreshCalendarContent(body, subtitle, periodLabel, state, seances, activeYear, monthBtn, weekBtn, dayBtn);
        });

        monthBtn.setOnAction(e -> {
            state.mode = "MOIS";
            refreshCalendarContent(body, subtitle, periodLabel, state, seances, activeYear, monthBtn, weekBtn, dayBtn);
        });
        weekBtn.setOnAction(e -> {
            state.mode = "SEMAINE";
            refreshCalendarContent(body, subtitle, periodLabel, state, seances, activeYear, monthBtn, weekBtn, dayBtn);
        });
        dayBtn.setOnAction(e -> {
            state.mode = "JOUR";
            refreshCalendarContent(body, subtitle, periodLabel, state, seances, activeYear, monthBtn, weekBtn, dayBtn);
        });

        refreshCalendarContent(body, subtitle, periodLabel, state, seances, activeYear, monthBtn, weekBtn, dayBtn);
        card.getChildren().addAll(titleRow, chips, subtitle, body);
        return card;
    }

    private Button createCalendarModeButton(String text) {
        Button chip = new Button(text);
        chip.getStyleClass().add("erp-chip-button");
        return chip;
    }

    private void refreshCalendarContent(VBox body, Label subtitle, Label periodLabel, CalendarState state,
                                        List<Seance> seances, AnneeScolaire activeYear,
                                        Button monthBtn, Button weekBtn, Button dayBtn) {
        body.getChildren().clear();
        setCalendarModeStyles(state.mode, monthBtn, weekBtn, dayBtn);

        if ("MOIS".equals(state.mode)) {
            periodLabel.setText(capitalize(state.currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH)) + " " + state.currentMonth.getYear());
            subtitle.setText(activeYear != null
                ? "Annee scolaire " + activeYear.getAnnee() + "  -  cliquez sur un jour pour ouvrir le detail"
                : "Cliquez sur un jour pour ouvrir le detail");
            body.getChildren().add(buildErpMonthGrid(state.currentMonth, state, body, subtitle, periodLabel, seances, activeYear, monthBtn, weekBtn, dayBtn));
        } else if ("SEMAINE".equals(state.mode)) {
            LocalDate start = state.selectedDate.minusDays(state.selectedDate.getDayOfWeek().getValue() - 1L);
            periodLabel.setText("Semaine du " + start + " au " + start.plusDays(6));
            subtitle.setText("Vue hebdomadaire du planning");
            body.getChildren().add(buildWeekAgenda(start, seances));
        } else {
            periodLabel.setText(capitalize(state.selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH)) + " " + state.selectedDate);
            subtitle.setText("Vue journaliere du planning");
            body.getChildren().add(buildDayAgenda(state.selectedDate, seances));
        }
    }

    private void setCalendarModeStyles(String mode, Button monthBtn, Button weekBtn, Button dayBtn) {
        monthBtn.getStyleClass().setAll("erp-chip-button");
        weekBtn.getStyleClass().setAll("erp-chip-button");
        dayBtn.getStyleClass().setAll("erp-chip-button");
        if ("MOIS".equals(mode)) {
            monthBtn.getStyleClass().setAll("erp-chip-active-button");
        } else if ("SEMAINE".equals(mode)) {
            weekBtn.getStyleClass().setAll("erp-chip-active-button");
        } else {
            dayBtn.getStyleClass().setAll("erp-chip-active-button");
        }
    }

    private GridPane buildErpMonthGrid(YearMonth month, CalendarState state, VBox body, Label subtitle, Label periodLabel,
                                       List<Seance> seances, AnneeScolaire activeYear,
                                       Button monthBtn, Button weekBtn, Button dayBtn) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("erp-calendar-grid");

        DayOfWeek[] days = {
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        };

        for (int col = 0; col < days.length; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / days.length);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }

        for (int col = 0; col < days.length; col++) {
            StackPane headerCell = new StackPane(new Label(capitalize(days[col].getDisplayName(TextStyle.FULL, Locale.FRENCH))));
            headerCell.getStyleClass().add("erp-calendar-header");
            headerCell.setPrefHeight(40);
            grid.add(headerCell, col, 0);
        }

        LocalDate firstDay = month.atDay(1);
        int startColumn = firstDay.getDayOfWeek().getValue() % 7;
        int dayCounter = 1;

        for (int row = 1; row <= 6; row++) {
            for (int col = 0; col < 7; col++) {
                VBox cell = new VBox(4);
                cell.getStyleClass().add("erp-calendar-day");
                cell.setPadding(new Insets(8));
                cell.setPrefHeight(88);

                int slot = (row - 1) * 7 + col;
                if (slot >= startColumn && dayCounter <= month.lengthOfMonth()) {
                    LocalDate date = month.atDay(dayCounter);
                    Label dayLabel = new Label(String.valueOf(dayCounter));
                    dayLabel.getStyleClass().add("erp-calendar-day-number");
                    cell.getChildren().add(dayLabel);

                    List<Seance> daySeances = findSeancesForDate(date, seances);
                    if (activeYear != null && date.equals(activeYear.getDateDebut())) {
                        cell.getChildren().add(createErpEvent("Rentree"));
                    } else if (activeYear != null && date.equals(activeYear.getDateFin())) {
                        cell.getChildren().add(createErpEvent("Cloture"));
                    } else if (!daySeances.isEmpty()) {
                        Seance first = daySeances.get(0);
                        cell.getChildren().add(createErpEvent(safeValue(first.getMatiereLibelle())));
                    }
                    cell.setOnMouseClicked(event -> {
                        state.selectedDate = date;
                        state.currentMonth = YearMonth.from(date);
                        state.mode = "JOUR";
                        refreshCalendarContent(body, subtitle, periodLabel, state, seances, activeYear, monthBtn, weekBtn, dayBtn);
                    });
                    dayCounter++;
                }
                grid.add(cell, col, row);
            }
        }

        return grid;
    }

    private VBox buildWeekAgenda(LocalDate startDate, List<Seance> seances) {
        VBox weekBox = new VBox(10);
        for (int index = 0; index < 7; index++) {
            LocalDate date = startDate.plusDays(index);
            VBox dayCard = new VBox(8);
            dayCard.getStyleClass().add("erp-calendar-agenda-card");
            Label title = new Label(capitalize(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH)) + "  " + date);
            title.getStyleClass().add("erp-card-title");
            dayCard.getChildren().add(title);

            List<Seance> daySeances = findSeancesForDate(date, seances);
            if (daySeances.isEmpty()) {
                dayCard.getChildren().add(createEmptyState("Aucune seance planifiee."));
            } else {
                for (Seance seance : daySeances) {
                    dayCard.getChildren().add(createAgendaLine(seance));
                }
            }
            weekBox.getChildren().add(dayCard);
        }
        return weekBox;
    }

    private VBox buildDayAgenda(LocalDate date, List<Seance> seances) {
        VBox dayBox = new VBox(10);
        VBox dayCard = new VBox(8);
        dayCard.getStyleClass().add("erp-calendar-agenda-card");
        Label title = new Label(capitalize(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH)) + "  " + date);
        title.getStyleClass().add("erp-card-title");
        dayCard.getChildren().add(title);

        List<Seance> daySeances = findSeancesForDate(date, seances);
        if (daySeances.isEmpty()) {
            dayCard.getChildren().add(createEmptyState("Aucune seance prevue ce jour."));
        } else {
            for (Seance seance : daySeances) {
                dayCard.getChildren().add(createAgendaLine(seance));
            }
        }
        dayBox.getChildren().add(dayCard);
        return dayBox;
    }

    private HBox createAgendaLine(Seance seance) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("erp-agenda-line");
        Label time = new Label(seance.getPlageHoraire());
        time.getStyleClass().add("erp-agenda-time");
        Label detail = new Label(
            safeValue(seance.getClasseNom()) + "  -  " +
            safeValue(seance.getMatiereLibelle()) + "  -  Salle " + safeValue(seance.getSalle())
        );
        detail.setWrapText(true);
        row.getChildren().addAll(time, detail);
        return row;
    }

    private List<Seance> findSeancesForDate(LocalDate date, List<Seance> seances) {
        DayOfWeek target = date.getDayOfWeek();
        return seances.stream()
            .filter(seance -> mapJourToDayOfWeek(seance.getJour()) == target)
            .sorted(Comparator.comparing(Seance::getHeureDebut))
            .toList();
    }

    private DayOfWeek mapJourToDayOfWeek(String jour) {
        if (jour == null) {
            return DayOfWeek.MONDAY;
        }
        return switch (jour.toLowerCase(Locale.ROOT)) {
            case "lundi" -> DayOfWeek.MONDAY;
            case "mardi" -> DayOfWeek.TUESDAY;
            case "mercredi" -> DayOfWeek.WEDNESDAY;
            case "jeudi" -> DayOfWeek.THURSDAY;
            case "vendredi" -> DayOfWeek.FRIDAY;
            case "samedi" -> DayOfWeek.SATURDAY;
            case "dimanche" -> DayOfWeek.SUNDAY;
            default -> DayOfWeek.MONDAY;
        };
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private Label createErpEvent(String text) {
        Label event = new Label(text);
        event.getStyleClass().add("erp-calendar-event");
        return event;
    }

    private GridPane createErpActionGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.add(createErpActionButton("Ajouter un eleve", this::navigateToEleves), 0, 0);
        grid.add(createErpActionButton("Ajouter un enseignant", this::navigateToEnseignants), 1, 0);
        grid.add(createErpActionButton("Organiser les classes", this::navigateToClasses), 0, 1);
        grid.add(createErpActionButton("Generer un bulletin", this::navigateToBulletins), 1, 1);
        return grid;
    }

    private Button createErpActionButton(String title, Runnable action) {
        Button button = new Button(title);
        button.getStyleClass().add("erp-action-button");
        button.setPrefHeight(108);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());
        GridPane.setHgrow(button, Priority.ALWAYS);
        return button;
    }

    private VBox createErpDistributionCard(List<Eleve> eleves) {
        VBox card = new VBox(12);
        card.getStyleClass().add("erp-card");
        Label title = new Label("Repartition des eleves par niveau");
        title.getStyleClass().add("erp-card-title");

        PieChart chart = new PieChart();
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Eleve eleve : eleves) {
            String level = eleve.getNiveau() == null || eleve.getNiveau().isBlank() ? "Non defini" : eleve.getNiveau();
            counts.put(level, counts.getOrDefault(level, 0) + 1);
        }
        if (counts.isEmpty()) {
            counts.put("Aucune donnee", 1);
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            chart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        chart.setLabelsVisible(false);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("erp-pie-chart");

        card.getChildren().addAll(title, chart);
        return card;
    }

    private void showElevesView() {
        VBox root = new VBox(18);

        VBox actions = createPanel("Registre eleves", "Consultez, recherchez et ajoutez rapidement des eleves.");
        HBox toolbar = new HBox(14);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Button addBtn = new Button("Nouvel eleve");
        addBtn.getStyleClass().add("btn");
        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().add("btn-secondary");
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("btn-secondary");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher par matricule, nom ou niveau");
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        toolbar.getChildren().addAll(addBtn, editBtn, deleteBtn, searchField);
        actions.getChildren().add(toolbar);

        VBox tablePanel = createPanel("Base eleves", "Vision filtrable des inscriptions eleves.");
        TableView<Eleve> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        table.getColumns().addAll(
            createColumn("Matricule", "matricule", 110),
            createColumn("Nom", "nom", 140),
            createColumn("Prenom", "prenom", 140),
            createColumn("Date naissance", "dateNaissance", 150),
            createColumn("Sexe", "sexe", 70),
            createColumn("Niveau", "niveau", 140)
        );
        FilteredList<Eleve> filtered = new FilteredList<>(FXCollections.observableArrayList(SchoolService.getAllEleves()), value -> true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String normalized = newValue == null ? "" : newValue.toLowerCase(Locale.ROOT).trim();
            filtered.setPredicate(eleve -> normalized.isBlank()
                || eleve.getMatricule().toLowerCase(Locale.ROOT).contains(normalized)
                || eleve.getNom().toLowerCase(Locale.ROOT).contains(normalized)
                || eleve.getPrenom().toLowerCase(Locale.ROOT).contains(normalized)
                || eleve.getNiveau().toLowerCase(Locale.ROOT).contains(normalized));
        });
        table.setItems(filtered);
        tablePanel.getChildren().add(table);

        addBtn.setOnAction(e -> showAddEleveDialog());
        editBtn.setOnAction(e -> showEditEleveDialog(table.getSelectionModel().getSelectedItem()));
        deleteBtn.setOnAction(e -> deleteEleve(table.getSelectionModel().getSelectedItem()));
        root.getChildren().addAll(actions, tablePanel);
        contentArea.getChildren().setAll(root);
    }

    private void showElevesImageStyle() {
        VBox page = new VBox(18);
        page.getStyleClass().add("erp-page");
        page.setFillWidth(true);

        Label title = new Label("Ajouter un eleve");
        title.getStyleClass().add("erp-page-title");

        VBox formCard = new VBox(20);
        formCard.getStyleClass().add("erp-card");

        HBox columns = new HBox(28);
        VBox leftColumn = new VBox(16);
        VBox rightColumn = new VBox(16);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);
        leftColumn.setMinWidth(420);
        rightColumn.setMinWidth(420);

        TextField matriculeField = new TextField(SchoolService.generateNextStudentMatricule());
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        DatePicker dobPicker = new DatePicker();
        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton maleRadio = new RadioButton("Garcon");
        maleRadio.setToggleGroup(genderGroup);
        maleRadio.setSelected(true);
        RadioButton femaleRadio = new RadioButton("Fille");
        femaleRadio.setToggleGroup(genderGroup);
        HBox genderRow = new HBox(14, maleRadio, femaleRadio);
        ComboBox<String> bloodGroupCombo = new ComboBox<>();
        bloodGroupCombo.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        TextField nationalityField = new TextField();
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Regulier", "Boursier", "Transfert");
        ComboBox<String> religionCombo = new ComboBox<>();
        religionCombo.getItems().addAll("Islam", "Christianisme", "Autre");
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField mobileField = new TextField();
        TextField photoField = new TextField();
        TextArea addressArea = new TextArea();
        addressArea.setPrefRowCount(3);

        leftColumn.getChildren().addAll(
            createErpSection("Details de l'eleve"),
            createErpSplitField("Nom de l'eleve", firstNameField, "Prenom", lastNameField, "Nom"),
            createErpField("Date de naissance", dobPicker),
            createErpField("Sexe", genderRow),
            createErpField("Groupe sanguin", bloodGroupCombo),
            createErpField("Nationalite", nationalityField),
            createErpField("Categorie", categoryCombo),
            createErpField("Religion", religionCombo),
            createErpSection("Coordonnees"),
            createErpField("Email eleve", emailField),
            createErpSplitField("Telephone", phoneField, "Telephone", mobileField, "Mobile"),
            createErpField("Photo", photoField),
            createErpField("Adresse eleve", addressArea)
        );

        DatePicker joiningDatePicker = new DatePicker(LocalDate.now());
        TextField rollNoField = new TextField();
        ComboBox<String> niveauCombo = new ComboBox<>(FXCollections.observableArrayList(SchoolService.getAllNiveaux()));
        TextField parentFirstNameField = new TextField();
        TextField parentLastNameField = new TextField();
        ComboBox<String> relationCombo = new ComboBox<>();
        relationCombo.getItems().addAll("Pere", "Mere", "Tuteur");
        TextField occupationField = new TextField();
        TextField parentEmailField = new TextField();
        TextField parentPhoneField = new TextField();
        TextField parentMobileField = new TextField();
        TextArea parentAddressArea = new TextArea();
        parentAddressArea.setPrefRowCount(3);

        rightColumn.getChildren().addAll(
            createErpSection("Details officiels"),
            createErpField("Matricule", matriculeField),
            createErpField("Date d'inscription", joiningDatePicker),
            createErpField("Numero interne", rollNoField),
            createErpField("Niveau scolaire", niveauCombo),
            createErpSection("Details du parent"),
            createErpSplitField("Nom du parent", parentFirstNameField, "Prenom", parentLastNameField, "Nom"),
            createErpField("Lien", relationCombo),
            createErpField("Profession", occupationField),
            createErpField("Email parent", parentEmailField),
            createErpSplitField("Telephone parent", parentPhoneField, "Telephone", parentMobileField, "Mobile"),
            createErpField("Adresse parent", parentAddressArea)
        );

        columns.getChildren().addAll(leftColumn, rightColumn);

        Button submitButton = new Button("Enregistrer l'eleve");
        submitButton.getStyleClass().add("btn");
        submitButton.setOnAction(e -> {
            if (matriculeField.getText().isBlank() || firstNameField.getText().isBlank() || lastNameField.getText().isBlank()
                || dobPicker.getValue() == null || niveauCombo.getValue() == null) {
                AlertUtils.showWarning("Champs requis", "Veuillez completer le matricule, le nom, la date de naissance et le niveau.");
                return;
            }

            Eleve eleve = new Eleve();
            eleve.setMatricule(matriculeField.getText().trim());
            eleve.setPrenom(firstNameField.getText().trim());
            eleve.setNom(lastNameField.getText().trim());
            eleve.setDateNaissance(dobPicker.getValue());
            eleve.setSexe(maleRadio.isSelected() ? "M" : "F");
            eleve.setTelephone(!mobileField.getText().isBlank() ? mobileField.getText().trim() : phoneField.getText().trim());
            eleve.setAdresse(addressArea.getText().trim());
            eleve.setNiveau(niveauCombo.getValue());
            eleve.setDateInscription(joiningDatePicker.getValue());
            eleve.setEmail(emailField.getText().trim());
            eleve.setParentPrenom(parentFirstNameField.getText().trim());
            eleve.setParentNom(parentLastNameField.getText().trim());
            eleve.setParentEmail(parentEmailField.getText().trim());
            eleve.setParentTelephone(!parentMobileField.getText().isBlank() ? parentMobileField.getText().trim() : parentPhoneField.getText().trim());
            eleve.setParentAdresse(parentAddressArea.getText().trim());
            eleve.setPhoto(readPhotoBytes(photoField.getText()));

            InvitationCode invitationCode = SchoolService.saveEleveAndGenerateInvitation(
                eleve,
                relationCombo.getValue() == null ? "TUTEUR" : relationCombo.getValue().toUpperCase(),
                LocalDate.now().plusMonths(3)
            );
            if (invitationCode != null) {
                AlertUtils.showInfo(
                    "Eleve ajoute",
                    "Eleve ajoute avec succes.\n\nCode d'invitation parent : " + invitationCode.getCode()
                        + "\nStatut : a remettre au parent, puis validation admin apres saisie."
                );
                navigateToEleves();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter l'eleve.");
            }
        });

        formCard.getChildren().addAll(columns, submitButton);

        TableView<Eleve> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        table.getColumns().addAll(
            createColumn("Matricule", "matricule", 120),
            createColumn("Nom complet", "nomComplet", 220),
            createColumn("Date de naissance", "dateNaissance", 140),
            createColumn("Sexe", "sexe", 100),
            createColumn("Niveau", "niveau", 130),
            createColumn("Telephone", "telephone", 160)
        );
        table.setItems(FXCollections.observableArrayList(SchoolService.getAllEleves()));
        configureDataTable(table, "Aucun eleve trouve.", 420);
        table.setRowFactory(tv -> {
            javafx.scene.control.TableRow<Eleve> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() >= 1 && !row.isEmpty()) {
                    showEleveDetailsDialog(row.getItem());
                }
            });
            return row;
        });

        VBox tableCard = new VBox(12);
        tableCard.getStyleClass().add("erp-card");
        tableCard.setMinHeight(500);
        tableCard.getChildren().addAll(createErpSection("Liste des eleves"), table);

        VBox parentsCard = new VBox(12);
        parentsCard.getStyleClass().add("erp-card");
        TextField parentNomField = new TextField();
        TextField parentPrenomField = new TextField();
        TextField parentTelephoneField = new TextField();
        TextField parentProfessionField = new TextField();
        TextField parentMailField = new TextField();
        TextField parentAdresseField = new TextField();
        Button saveParentBtn = new Button("Enregistrer le parent");
        saveParentBtn.getStyleClass().add("btn");
        HBox parentForm = new HBox(12,
            labelledField("Nom", parentNomField),
            labelledField("Prenom", parentPrenomField),
            labelledField("Telephone", parentTelephoneField),
            labelledField("Profession", parentProfessionField),
            labelledField("Email", parentMailField),
            labelledField("Adresse", parentAdresseField),
            saveParentBtn
        );
        TableView<Parent> parentsTable = new TableView<>();
        parentsTable.getStyleClass().add("premium-table");
        parentsTable.getColumns().addAll(
            createColumn("Nom complet", "nomComplet", 220),
            createColumn("Telephone", "telephone", 150),
            createColumn("Profession", "profession", 180),
            createColumn("Email", "email", 200)
        );
        parentsTable.setItems(FXCollections.observableArrayList(SchoolService.getAllParents()));
        configureDataTable(parentsTable, "Aucun parent enregistre.", 220);
        Button deleteParentBtn = new Button("Supprimer le parent");
        deleteParentBtn.getStyleClass().add("btn-secondary");
        deleteParentBtn.setOnAction(e -> {
            Parent selected = parentsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez un parent.");
                return;
            }
            if (SchoolService.deleteParent(selected.getIdParent())) {
                navigateToEleves();
            } else {
                AlertUtils.showError("Erreur", "Impossible de supprimer ce parent.");
            }
        });
        saveParentBtn.setOnAction(e -> {
            if (parentNomField.getText().isBlank() || parentPrenomField.getText().isBlank()) {
                AlertUtils.showWarning("Champs requis", "Le nom et le prenom du parent sont obligatoires.");
                return;
            }
            Parent parent = new Parent();
            parent.setNom(parentNomField.getText().trim());
            parent.setPrenom(parentPrenomField.getText().trim());
            parent.setTelephone(parentTelephoneField.getText().trim());
            parent.setProfession(parentProfessionField.getText().trim());
            parent.setEmail(parentMailField.getText().trim());
            parent.setAdresse(parentAdresseField.getText().trim());
            if (SchoolService.saveParent(parent)) {
                navigateToEleves();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'enregistrer le parent.");
            }
        });
        parentsCard.getChildren().addAll(createErpSection("Parents / tuteurs"), parentForm, deleteParentBtn, parentsTable);

        page.getChildren().addAll(title, formCard, tableCard, parentsCard);
        contentArea.getChildren().setAll(page);
    }

    private VBox createErpSection(String titleText) {
        VBox box = new VBox(2);
        Label title = new Label(titleText);
        title.getStyleClass().add("erp-section-title");
        box.getChildren().add(title);
        return box;
    }

    private VBox createErpField(String labelText, Node field) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.getStyleClass().add("erp-field-label");
        if (field instanceof Region region) {
            region.setMaxWidth(Double.MAX_VALUE);
        }
        box.getChildren().addAll(label, field);
        return box;
    }

    private VBox createErpSplitField(String labelText, Node leftField, String leftHint, Node rightField, String rightHint) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.getStyleClass().add("erp-field-label");
        HBox row = new HBox(12);
        VBox leftBox = new VBox(4, leftField, createErpHint(leftHint));
        VBox rightBox = new VBox(4, rightField, createErpHint(rightHint));
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        if (leftField instanceof Region leftRegion) {
            leftRegion.setMaxWidth(Double.MAX_VALUE);
        }
        if (rightField instanceof Region rightRegion) {
            rightRegion.setMaxWidth(Double.MAX_VALUE);
        }
        row.getChildren().addAll(leftBox, rightBox);
        box.getChildren().addAll(label, row);
        return box;
    }

    private Label createErpHint(String text) {
        Label hint = new Label(text);
        hint.getStyleClass().add("erp-hint");
        return hint;
    }

    private void showEnseignantsImageStyle() {
        VBox page = new VBox(18);
        page.getStyleClass().add("erp-page");
        page.setFillWidth(true);

        Label title = new Label("Ajouter un enseignant");
        title.getStyleClass().add("erp-page-title");

        VBox formCard = new VBox(20);
        formCard.getStyleClass().add("erp-card");

        HBox columns = new HBox(28);
        VBox leftColumn = new VBox(16);
        VBox rightColumn = new VBox(16);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        HBox.setHgrow(rightColumn, Priority.ALWAYS);

        TextField matriculeField = new TextField();
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        DatePicker dobPicker = new DatePicker();
        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton maleRadio = new RadioButton("Homme");
        maleRadio.setToggleGroup(genderGroup);
        maleRadio.setSelected(true);
        RadioButton femaleRadio = new RadioButton("Femme");
        femaleRadio.setToggleGroup(genderGroup);
        HBox genderRow = new HBox(14, maleRadio, femaleRadio);
        TextField phoneField = new TextField();
        TextArea addressArea = new TextArea();
        addressArea.setPrefRowCount(3);
        TextField gradeField = new TextField();
        TextField emailField = new TextField();
        TextField specializationField = new TextField();
        TextField photoField = new TextField();
        photoField.setPromptText("C:\\chemin\\photo.jpg");

        leftColumn.getChildren().addAll(
            createErpSection("Details de l'enseignant"),
            createErpField("Matricule", matriculeField),
            createErpSplitField("Nom de l'enseignant", firstNameField, "Prenom", lastNameField, "Nom"),
            createErpField("Date de naissance", dobPicker),
            createErpField("Sexe", genderRow),
            createErpField("Telephone", phoneField),
            createErpField("Adresse", addressArea)
        );

        rightColumn.getChildren().addAll(
            createErpSection("Details professionnels"),
            createErpField("Grade", gradeField),
            createErpField("Email officiel", emailField),
            createErpField("Specialite", specializationField),
            createErpField("Photo", photoField)
        );

        columns.getChildren().addAll(leftColumn, rightColumn);

        Button submitButton = new Button("Enregistrer l'enseignant");
        submitButton.getStyleClass().add("btn");
        submitButton.setOnAction(e -> {
            if (matriculeField.getText().isBlank() || firstNameField.getText().isBlank() || lastNameField.getText().isBlank()
                || dobPicker.getValue() == null || gradeField.getText().isBlank()) {
                AlertUtils.showWarning("Champs requis", "Veuillez completer le matricule, le nom, la date de naissance et le grade.");
                return;
            }
            Enseignant enseignant = new Enseignant();
            enseignant.setMatricule(matriculeField.getText().trim());
            enseignant.setPrenom(firstNameField.getText().trim());
            enseignant.setNom(lastNameField.getText().trim());
            enseignant.setDateNaissance(dobPicker.getValue());
            enseignant.setSexe(maleRadio.isSelected() ? "M" : "F");
            enseignant.setTelephone(phoneField.getText().trim());
            enseignant.setAdresse(addressArea.getText().trim());
            enseignant.setGrade(gradeField.getText().trim());
            enseignant.setPhoto(readPhotoBytes(photoField.getText()));
            if (SchoolService.saveEnseignant(enseignant)) {
                AlertUtils.showInfo("Succes", "Enseignant ajoute avec succes.");
                navigateToEnseignants();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter l'enseignant.");
            }
        });

        TableView<Enseignant> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        table.getColumns().addAll(
            createColumn("Matricule", "matricule", 120),
            createColumn("Nom complet", "nomComplet", 220),
            createColumn("Grade", "grade", 160),
            createColumn("Telephone", "telephone", 160)
        );
        table.setItems(FXCollections.observableArrayList(SchoolService.getAllEnseignants()));
        configureDataTable(table, "Aucun enseignant trouve.", 250);
        table.setRowFactory(tv -> {
            javafx.scene.control.TableRow<Enseignant> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() >= 1 && !row.isEmpty()) {
                    showEnseignantDetailsDialog(row.getItem());
                }
            });
            return row;
        });

        VBox tableCard = new VBox(12);
        tableCard.getStyleClass().add("erp-card");
        tableCard.getChildren().addAll(createErpSection("Liste des enseignants"), table);

        formCard.getChildren().addAll(columns, submitButton);
        VBox assignmentsCard = new VBox(12);
        assignmentsCard.getStyleClass().add("erp-card");
        ComboBox<Enseignant> assignTeacherCombo = new ComboBox<>(FXCollections.observableArrayList(SchoolService.getAllEnseignants()));
        ComboBox<Classe> assignClasseCombo = new ComboBox<>(FXCollections.observableArrayList(SchoolService.getAllClasses()));
        ComboBox<Matiere> assignMatiereCombo = new ComboBox<>(FXCollections.observableArrayList(SchoolService.getAllMatieres()));
        Button assignBtn = new Button("Ajouter l'affectation");
        assignBtn.getStyleClass().add("btn");
        TableView<AffectationEnseignement> assignmentsTable = new TableView<>();
        assignmentsTable.getStyleClass().add("premium-table");
        assignmentsTable.getColumns().addAll(
            createColumn("Enseignant", "enseignantNomComplet", 220),
            createColumn("Classe", "classeNom", 180),
            createColumn("Matiere", "matiereLibelle", 180)
        );
        assignmentsTable.setItems(FXCollections.observableArrayList(SchoolService.getAllAffectations()));
        configureDataTable(assignmentsTable, "Aucune affectation enregistree.", 220);
        Button removeAssignBtn = new Button("Supprimer l'affectation");
        removeAssignBtn.getStyleClass().add("btn-secondary");
        removeAssignBtn.setOnAction(e -> {
            AffectationEnseignement selected = assignmentsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez une affectation.");
                return;
            }
            if (SchoolService.deleteAffectation(selected.getId())) {
                navigateToEnseignants();
            } else {
                AlertUtils.showError("Erreur", "Impossible de supprimer l'affectation.");
            }
        });
        assignBtn.setOnAction(e -> {
            if (assignTeacherCombo.getValue() == null || assignClasseCombo.getValue() == null || assignMatiereCombo.getValue() == null) {
                AlertUtils.showWarning("Champs requis", "Choisissez un enseignant, une classe et une matiere.");
                return;
            }
            if (SchoolService.saveAffectation(assignTeacherCombo.getValue().getMatricule(), assignClasseCombo.getValue().getIdClasse(), assignMatiereCombo.getValue().getCode())) {
                navigateToEnseignants();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'enregistrer l'affectation.");
            }
        });
        assignmentsCard.getChildren().addAll(
            createErpSection("Affectations enseignant / classe / matiere"),
            new HBox(12, labelledField("Enseignant", assignTeacherCombo), labelledField("Classe", assignClasseCombo), labelledField("Matiere", assignMatiereCombo), assignBtn),
            removeAssignBtn,
            assignmentsTable
        );

        page.getChildren().addAll(title, formCard, tableCard, assignmentsCard);
        contentArea.getChildren().setAll(page);
    }

    private void showUsersView() {
        VBox page = new VBox(18);
        page.getStyleClass().add("erp-page");
        page.setFillWidth(true);

        Label title = new Label("Comptes utilisateurs");
        title.getStyleClass().add("erp-page-title");

        VBox usersCard = new VBox(12);
        usersCard.getStyleClass().add("erp-card");
        TextField usernameField = new TextField();
        TextField passwordField = new TextField("admin123");
        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList("ADMIN", "ENSEIGNANT", "PARENT", "ELEVE"));
        TextField personIdField = new TextField();
        Button createUserBtn = new Button("Creer le compte");
        createUserBtn.getStyleClass().add("btn");
        TableView<Utilisateur> usersTable = new TableView<>();
        usersTable.getStyleClass().add("premium-table");
        usersTable.getColumns().addAll(
            createColumn("Username", "username", 180),
            createColumn("Role", "role", 120),
            createColumn("Id personne", "idPersonne", 120),
            createColumn("Actif", "estActif", 100)
        );
        usersTable.setItems(FXCollections.observableArrayList(SchoolService.getAllUtilisateurs()));
        configureDataTable(usersTable, "Aucun compte utilisateur.", 260);
        Button toggleUserBtn = new Button("Activer / desactiver");
        toggleUserBtn.getStyleClass().add("btn-secondary");
        Button resetUserBtn = new Button("Reinitialiser le mot de passe");
        resetUserBtn.getStyleClass().add("btn-secondary");
        createUserBtn.setOnAction(e -> {
            if (usernameField.getText().isBlank() || passwordField.getText().isBlank() || roleCombo.getValue() == null || personIdField.getText().isBlank()) {
                AlertUtils.showWarning("Champs requis", "Completer username, mot de passe, role et id personne.");
                return;
            }
            try {
                Utilisateur user = new Utilisateur();
                user.setUsername(usernameField.getText().trim());
                user.setRole(roleCombo.getValue());
                user.setIdPersonne(Integer.parseInt(personIdField.getText().trim()));
                user.setEstActif(true);
                if (SchoolService.saveUtilisateur(user, passwordField.getText().trim())) {
                    navigateToUsers();
                } else {
                    AlertUtils.showError("Erreur", "Impossible de creer le compte.");
                }
            } catch (NumberFormatException ex) {
                AlertUtils.showWarning("Id invalide", "L'id personne doit etre un nombre.");
            }
        });
        toggleUserBtn.setOnAction(e -> {
            Utilisateur selected = usersTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez un utilisateur.");
                return;
            }
            if (SchoolService.toggleUtilisateur(selected.getId(), !selected.isEstActif())) {
                navigateToUsers();
            } else {
                AlertUtils.showError("Erreur", "Impossible de modifier l'etat du compte.");
            }
        });
        resetUserBtn.setOnAction(e -> {
            Utilisateur selected = usersTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez un utilisateur.");
                return;
            }
            if (SchoolService.resetUtilisateurPassword(selected.getId(), "admin123")) {
                AlertUtils.showInfo("Succes", "Mot de passe reinitialise a admin123.");
            } else {
                AlertUtils.showError("Erreur", "Impossible de reinitialiser le mot de passe.");
            }
        });
        usersCard.getChildren().addAll(
            createErpSection("Creation et gestion des comptes"),
            new HBox(12, labelledField("Username", usernameField), labelledField("Mot de passe", passwordField), labelledField("Role", roleCombo), labelledField("Id personne", personIdField), createUserBtn),
            new HBox(12, toggleUserBtn, resetUserBtn),
            usersTable
        );

        page.getChildren().addAll(title, usersCard);
        contentArea.getChildren().setAll(page);
    }

    private ScrollPane createFormScroll(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefViewportHeight(520);
        scrollPane.getStyleClass().add("inner-form-scroll");
        return scrollPane;
    }

    private void showClassesImageStyle() {
        VBox page = new VBox(18);
        page.getStyleClass().add("erp-page");
        Label title = new Label("Classes");
        title.getStyleClass().add("erp-page-title");

        HBox split = new HBox(16);
        VBox listCard = new VBox(12);
        listCard.getStyleClass().add("erp-card");
        listCard.setPrefWidth(320);
        VBox detailCard = new VBox(12);
        detailCard.getStyleClass().add("erp-card");
        HBox.setHgrow(detailCard, Priority.ALWAYS);

        TextField nomClasseField = new TextField();
        ComboBox<String> niveauClasseCombo = new ComboBox<>(FXCollections.observableArrayList(SchoolService.getAllNiveaux()));
        TextField capaciteField = new TextField("20");
        Button addButton = new Button("Enregistrer la classe");
        addButton.getStyleClass().add("btn");
        addButton.setOnAction(e -> {
            if (nomClasseField.getText().isBlank() || niveauClasseCombo.getValue() == null || capaciteField.getText().isBlank()) {
                AlertUtils.showWarning("Champs requis", "Veuillez completer le nom, le niveau et la capacite.");
                return;
            }
            Classe classe = new Classe();
            classe.setNom(nomClasseField.getText().trim());
            classe.setNiveau(niveauClasseCombo.getValue());
            classe.setCapacite(Integer.parseInt(capaciteField.getText().trim()));
            if (SchoolService.saveClasse(classe)) {
                AlertUtils.showInfo("Succes", "Classe ajoutee avec succes.");
                navigateToClasses();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter la classe.");
            }
        });
        listCard.getChildren().addAll(
            createErpSection("Nouvelle classe"),
            createErpField("Nom de la classe", nomClasseField),
            createErpField("Niveau", niveauClasseCombo),
            createErpField("Capacite", capaciteField),
            addButton,
            createErpSection("Liste des classes")
        );

        VBox classButtons = new VBox(10);
        List<Classe> classes = SchoolService.getAllClasses();
        if (!classes.isEmpty()) {
            populateImageClassDetail(detailCard, classes.get(0));
        }
        for (Classe classe : classes) {
            Button button = new Button(classe.getNomComplet() + "\n" + classe.getEffectifActuel() + " eleves");
            button.getStyleClass().add("class-list-button");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(e -> populateImageClassDetail(detailCard, classe));
            classButtons.getChildren().add(button);
        }
        listCard.getChildren().add(classButtons);
        split.getChildren().addAll(listCard, detailCard);

        VBox yearsCard = new VBox(12);
        yearsCard.getStyleClass().add("erp-card");
        TextField anneeField = new TextField();
        DatePicker debutYearPicker = new DatePicker(LocalDate.now());
        DatePicker finYearPicker = new DatePicker(LocalDate.now().plusMonths(9));
        Button addYearBtn = new Button("Ajouter l'annee scolaire");
        addYearBtn.getStyleClass().add("btn");
        TableView<AnneeScolaire> yearsTable = new TableView<>();
        yearsTable.getStyleClass().add("premium-table");
        yearsTable.getColumns().addAll(
            createColumn("Annee", "annee", 150),
            createColumn("Debut", "dateDebut", 150),
            createColumn("Fin", "dateFin", 150),
            createColumn("Active", "estActive", 100)
        );
        yearsTable.setItems(FXCollections.observableArrayList(SchoolService.getAllAnneesScolaires()));
        configureDataTable(yearsTable, "Aucune annee scolaire.", 220);
        Button activateYearBtn = new Button("Activer");
        activateYearBtn.getStyleClass().add("btn-secondary");
        addYearBtn.setOnAction(e -> {
            if (anneeField.getText().isBlank() || debutYearPicker.getValue() == null || finYearPicker.getValue() == null) {
                AlertUtils.showWarning("Champs requis", "Renseignez l'annee et les dates.");
                return;
            }
            AnneeScolaire annee = new AnneeScolaire();
            annee.setAnnee(anneeField.getText().trim());
            annee.setDateDebut(debutYearPicker.getValue());
            annee.setDateFin(finYearPicker.getValue());
            annee.setEstActive(false);
            if (SchoolService.saveAnneeScolaire(annee)) {
                navigateToClasses();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter l'annee scolaire.");
            }
        });
        activateYearBtn.setOnAction(e -> {
            AnneeScolaire selected = yearsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez une annee scolaire.");
                return;
            }
            if (SchoolService.setAnneeActive(selected.getIdAnnee())) {
                navigateToClasses();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'activer cette annee.");
            }
        });
        yearsCard.getChildren().addAll(
            createErpSection("Annees scolaires"),
            new HBox(12, labelledField("Annee", anneeField), labelledField("Debut", debutYearPicker), labelledField("Fin", finYearPicker), addYearBtn, activateYearBtn),
            yearsTable
        );

        page.getChildren().addAll(title, split, yearsCard);
        contentArea.getChildren().setAll(page);
    }

    private void populateImageClassDetail(VBox detailCard, Classe classe) {
        detailCard.getChildren().clear();
        Label title = new Label(classe.getNomComplet());
        title.getStyleClass().add("erp-page-title");
        GridPane info = new GridPane();
        info.getStyleClass().add("detail-grid");
        addDetailRow(info, 0, "Classe", classe.getNomComplet());
        addDetailRow(info, 1, "Niveau", classe.getNiveau());
        addDetailRow(info, 2, "Capacite", String.valueOf(classe.getCapacite()));
        addDetailRow(info, 3, "Effectif actuel", String.valueOf(classe.getEffectifActuel()));

        TableView<Eleve> students = new TableView<>();
        students.getStyleClass().add("premium-table");
        students.getColumns().addAll(
            createColumn("Matricule", "matricule", 150),
            createColumn("Nom complet", "nomComplet", 220),
            createColumn("Telephone", "telephone", 170),
            createColumn("Niveau", "niveau", 120)
        );
        students.setItems(FXCollections.observableArrayList(SchoolService.getElevesByClasse(classe.getIdClasse())));
        configureDataTable(students, "Aucun eleve dans cette classe.", 260);
        students.setRowFactory(tv -> {
            javafx.scene.control.TableRow<Eleve> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() >= 1 && !row.isEmpty()) {
                    showEleveDetailsDialog(row.getItem());
                }
            });
            return row;
        });

        detailCard.getChildren().addAll(title, info, createErpSection("Eleves"), students);
    }

    private void addDetailRow(GridPane grid, int row, String labelText, String valueText) {
        Label label = new Label(labelText);
        label.getStyleClass().add("detail-label");
        Label value = new Label(valueText != null ? valueText : "-");
        value.getStyleClass().add("detail-value");
        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private void showEleveDetailsDialog(Eleve eleve) {
        if (eleve == null) {
            return;
        }
        headerTitle.setText("Profil eleve");
        contentArea.getChildren().setAll(buildEleveProfilePage(eleve));
    }

    private void showEnseignantDetailsDialog(Enseignant enseignant) {
        if (enseignant == null) {
            return;
        }
        headerTitle.setText("Profil enseignant");
        contentArea.getChildren().setAll(buildEnseignantProfilePage(enseignant));
    }

    private VBox buildEleveProfilePage(Eleve eleve) {
        VBox page = new VBox(18);
        page.getStyleClass().add("profile-page");
        Button back = new Button("Retour aux eleves");
        back.getStyleClass().add("btn-secondary");
        back.setOnAction(e -> navigateToEleves());

        HBox hero = new HBox(22);
        hero.getStyleClass().add("erp-card");
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setPadding(new Insets(24));
        Node avatar = createPhotoAvatar(eleve.getPhoto(), initials(eleve.getPrenom(), eleve.getNom()), 116);
        VBox identity = new VBox(8);
        Label name = new Label(eleve.getNomComplet());
        name.getStyleClass().add("erp-page-title");
        Label meta = new Label("Matricule " + safeValue(eleve.getMatricule()) + "  -  " + safeValue(eleve.getNiveau()));
        meta.getStyleClass().add("soft-muted");
        HBox chips = new HBox(10, profileChip("Eleve"), profileChip(safeValue(eleve.getSexe())), profileChip("Actif"));
        identity.getChildren().addAll(name, meta, chips);
        hero.getChildren().addAll(avatar, identity);

        HBox stats = new HBox(14);
        stats.getChildren().addAll(
            profileStat("Date de naissance", String.valueOf(eleve.getDateNaissance())),
            profileStat("Telephone", safeValue(eleve.getTelephone())),
            profileStat("Email", safeValue(eleve.getEmail())),
            profileStat("Parent", safeValue(eleve.getParentNomComplet()))
        );

        HBox details = new HBox(18);
        details.getChildren().addAll(
            profileCard("Informations scolaires",
                detailLine("Niveau", safeValue(eleve.getNiveau())),
                detailLine("Adresse", safeValue(eleve.getAdresse())),
                detailLine("Date inscription", String.valueOf(eleve.getDateInscription()))
            ),
            profileCard("Responsable parent",
                detailLine("Nom", safeValue(eleve.getParentNomComplet())),
                detailLine("Email", safeValue(eleve.getParentEmail())),
                detailLine("Telephone", safeValue(eleve.getParentTelephone())),
                detailLine("Adresse", safeValue(eleve.getParentAdresse()))
            )
        );
        page.getChildren().addAll(back, hero, stats, details);
        return page;
    }

    private VBox buildEnseignantProfilePage(Enseignant enseignant) {
        VBox page = new VBox(18);
        page.getStyleClass().add("profile-page");
        Button back = new Button("Retour aux enseignants");
        back.getStyleClass().add("btn-secondary");
        back.setOnAction(e -> navigateToEnseignants());

        List<Classe> classes = SchoolService.getTeacherClasses(enseignant.getMatricule());
        List<Matiere> matieres = SchoolService.getTeacherMatieres(enseignant.getMatricule());
        List<Seance> seances = SchoolService.getSeancesByEnseignant(enseignant.getMatricule());
        String classesText = classes.isEmpty() ? "-" : classes.stream().map(Classe::getNomComplet).distinct().sorted().reduce((a, b) -> a + ", " + b).orElse("-");
        String matieresText = matieres.isEmpty() ? "-" : matieres.stream().map(Matiere::getLibelle).distinct().sorted().reduce((a, b) -> a + ", " + b).orElse("-");

        HBox hero = new HBox(22);
        hero.getStyleClass().add("erp-card");
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.setPadding(new Insets(24));
        Node avatar = createPhotoAvatar(enseignant.getPhoto(), initials(enseignant.getPrenom(), enseignant.getNom()), 116);
        VBox identity = new VBox(8);
        Label name = new Label(enseignant.getNomComplet());
        name.getStyleClass().add("erp-page-title");
        Label meta = new Label("Matricule " + safeValue(enseignant.getMatricule()) + "  -  " + safeValue(enseignant.getGrade()));
        meta.getStyleClass().add("soft-muted");
        HBox chips = new HBox(10, profileChip("Enseignant"), profileChip(matieresText), profileChip(seances.size() + " seances"));
        identity.getChildren().addAll(name, meta, chips);
        hero.getChildren().addAll(avatar, identity);

        HBox stats = new HBox(14);
        stats.getChildren().addAll(
            profileStat("Matiere", matieresText),
            profileStat("Classes", classesText),
            profileStat("Seances", String.valueOf(seances.size())),
            profileStat("Telephone", safeValue(enseignant.getTelephone()))
        );

        HBox details = new HBox(18);
        details.getChildren().addAll(
            profileCard("Identite",
                detailLine("Date de naissance", String.valueOf(enseignant.getDateNaissance())),
                detailLine("Sexe", safeValue(enseignant.getSexe())),
                detailLine("Adresse", safeValue(enseignant.getAdresse()))
            ),
            profileCard("Charge pedagogique",
                detailLine("Grade", safeValue(enseignant.getGrade())),
                detailLine("Matiere assuree", matieresText),
                detailLine("Classes affectees", classesText),
                detailLine("Nombre de seances", String.valueOf(seances.size()))
            )
        );
        page.getChildren().addAll(back, hero, stats, details);
        return page;
    }

    private Node createPhotoAvatar(byte[] photo, String fallback, double size) {
        StackPane avatar = new StackPane();
        avatar.setMinSize(size, size);
        avatar.setPrefSize(size, size);
        avatar.setMaxSize(size, size);
        avatar.setStyle("-fx-background-color: linear-gradient(to bottom right, #E98257, #C64D37); -fx-background-radius: " + (size / 2) + "; -fx-border-color: white; -fx-border-width: 4; -fx-border-radius: " + (size / 2) + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 14, 0, 0, 4);");
        if (photo != null && photo.length > 0) {
            try {
                ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(photo)));
                imageView.setFitWidth(size);
                imageView.setFitHeight(size);
                imageView.setPreserveRatio(false);
                imageView.setStyle("-fx-background-radius: " + (size / 2) + ";");
                avatar.getChildren().add(imageView);
                return avatar;
            } catch (Exception ignored) {
            }
        }
        Label initials = new Label(fallback);
        initials.setStyle("-fx-text-fill: white; -fx-font-size: " + Math.max(22, size / 3) + "px; -fx-font-weight: 800;");
        avatar.getChildren().add(initials);
        return avatar;
    }

    private byte[] readPhotoBytes(String rawPath) {
        String path = rawPath == null ? "" : rawPath.trim();
        if (path.isBlank()) {
            return null;
        }
        try {
            java.nio.file.Path imagePath = java.nio.file.Paths.get(path);
            if (!java.nio.file.Files.exists(imagePath)) {
                AlertUtils.showWarning("Photo introuvable", "Le fichier photo n'existe pas : " + path);
                return null;
            }
            return java.nio.file.Files.readAllBytes(imagePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtils.showWarning("Photo non chargee", "Impossible de lire la photo indiquee.");
            return null;
        }
    }

    private String initials(String prenom, String nom) {
        String first = prenom == null || prenom.isBlank() ? "" : prenom.substring(0, 1);
        String last = nom == null || nom.isBlank() ? "" : nom.substring(0, 1);
        String value = (first + last).toUpperCase(Locale.ROOT);
        return value.isBlank() ? "SE" : value;
    }

    private Label profileChip(String text) {
        Label chip = new Label(text == null || text.isBlank() ? "-" : text);
        chip.setStyle("-fx-background-color: #F6E8D8; -fx-text-fill: #9E432B; -fx-font-weight: 700; -fx-padding: 7 12; -fx-background-radius: 999;");
        return chip;
    }

    private VBox profileStat(String labelText, String valueText) {
        VBox stat = new VBox(6);
        stat.getStyleClass().add("erp-card");
        stat.setPadding(new Insets(16));
        stat.setMinWidth(180);
        HBox.setHgrow(stat, Priority.ALWAYS);
        Label label = new Label(labelText);
        label.getStyleClass().add("detail-label");
        Label value = new Label(valueText == null || valueText.isBlank() ? "-" : valueText);
        value.getStyleClass().add("detail-value");
        value.setWrapText(true);
        stat.getChildren().addAll(label, value);
        return stat;
    }

    private VBox profileCard(String titleText, Node... rows) {
        VBox card = new VBox(12);
        card.getStyleClass().add("erp-card");
        card.setPadding(new Insets(20));
        HBox.setHgrow(card, Priority.ALWAYS);
        Label title = new Label(titleText);
        title.getStyleClass().add("soft-section-title");
        card.getChildren().add(title);
        card.getChildren().addAll(rows);
        return card;
    }

    private HBox detailLine(String labelText, String valueText) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.getStyleClass().add("detail-label");
        label.setMinWidth(150);
        Label value = new Label(valueText == null || valueText.isBlank() ? "-" : valueText);
        value.getStyleClass().add("detail-value");
        value.setWrapText(true);
        HBox.setHgrow(value, Priority.ALWAYS);
        row.getChildren().addAll(label, value);
        return row;
    }

    private String getAppreciation(double moyenne) {
        if (moyenne >= 16) return "Excellent";
        if (moyenne >= 14) return "Tres bien";
        if (moyenne >= 12) return "Bien";
        if (moyenne >= 10) return "Assez bien";
        return "A ameliorer";
    }

    private void showMatieresImageStyle() {
        VBox page = new VBox(18);
        page.getStyleClass().add("erp-page");
        Label title = new Label("Matieres");
        title.getStyleClass().add("erp-page-title");

        VBox card = new VBox(16);
        card.getStyleClass().add("erp-card");
        HBox toolbar = new HBox(12);
        TextField codeField = new TextField();
        TextField libelleField = new TextField();
        TextField coefficientField = new TextField("1");
        Button addBtn = new Button("Enregistrer la matiere");
        addBtn.getStyleClass().add("btn");
        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().add("btn-secondary");
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("btn-secondary");
        toolbar.getChildren().addAll(
            labelledField("Code", codeField),
            labelledField("Libelle", libelleField),
            labelledField("Coefficient", coefficientField),
            addBtn,
            editBtn,
            deleteBtn
        );

        TableView<Matiere> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        table.getColumns().addAll(
            createColumn("Code", "code", 120),
            createColumn("Libelle", "libelle", 280),
            createColumn("Coefficient", "coefficient", 120)
        );
        table.setItems(FXCollections.observableArrayList(SchoolService.getAllMatieres()));
        configureDataTable(table, "Aucune matiere trouvee.", 240);

        addBtn.setOnAction(e -> {
            if (codeField.getText().isBlank() || libelleField.getText().isBlank() || coefficientField.getText().isBlank()) {
                AlertUtils.showWarning("Champs requis", "Veuillez completer le code, le libelle et le coefficient.");
                return;
            }
            Matiere matiere = new Matiere();
            matiere.setCode(codeField.getText().trim());
            matiere.setLibelle(libelleField.getText().trim());
            matiere.setCoefficient(Double.parseDouble(coefficientField.getText().trim()));
            if (SchoolService.saveMatiere(matiere)) {
                AlertUtils.showInfo("Succes", "Matiere ajoutee avec succes.");
                navigateToMatieres();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter la matiere.");
            }
        });
        editBtn.setOnAction(e -> showEditMatiereDialog(table.getSelectionModel().getSelectedItem()));
        deleteBtn.setOnAction(e -> deleteMatiere(table.getSelectionModel().getSelectedItem()));

        card.getChildren().addAll(createErpSection("Catalogue des matieres"), toolbar, table);
        VBox configCard = new VBox(12);
        configCard.getStyleClass().add("erp-card");
        TextField configKeyField = new TextField();
        TextField configValueField = new TextField();
        TextField configCategoryField = new TextField();
        TextField configDescriptionField = new TextField();
        Button saveConfigBtn = new Button("Enregistrer le parametre");
        saveConfigBtn.getStyleClass().add("btn");
        TableView<ConfigurationScolaire> configTable = new TableView<>();
        configTable.getStyleClass().add("premium-table");
        configTable.getColumns().addAll(
            createColumn("Cle", "cle", 180),
            createColumn("Valeur", "valeur", 150),
            createColumn("Categorie", "categorie", 140),
            createColumn("Description", "description", 260)
        );
        configTable.setItems(FXCollections.observableArrayList(SchoolService.getConfigurations()));
        configureDataTable(configTable, "Aucune configuration.", 220);
        saveConfigBtn.setOnAction(e -> {
            if (configKeyField.getText().isBlank() || configValueField.getText().isBlank()) {
                AlertUtils.showWarning("Champs requis", "Cle et valeur obligatoires.");
                return;
            }
            if (SchoolService.saveConfiguration(configKeyField.getText().trim(), configValueField.getText().trim(), configCategoryField.getText().trim(), configDescriptionField.getText().trim())) {
                navigateToMatieres();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'enregistrer ce parametre.");
            }
        });
        configCard.getChildren().addAll(
            createErpSection("Configuration scolaire"),
            new HBox(12, labelledField("Cle", configKeyField), labelledField("Valeur", configValueField), labelledField("Categorie", configCategoryField), labelledField("Description", configDescriptionField), saveConfigBtn),
            configTable
        );

        VBox periodesCard = new VBox(12);
        periodesCard.getStyleClass().add("erp-card");
        TextField periodeLabelField = new TextField();
        ComboBox<String> periodeTypeCombo = new ComboBox<>(FXCollections.observableArrayList("TRIMESTRE", "SEMESTRE"));
        TextField periodeOrderField = new TextField("1");
        Button savePeriodeBtn = new Button("Ajouter la periode");
        savePeriodeBtn.getStyleClass().add("btn");
        TableView<PeriodeScolaire> periodesTable = new TableView<>();
        periodesTable.getStyleClass().add("premium-table");
        periodesTable.getColumns().addAll(
            createColumn("Libelle", "libelle", 180),
            createColumn("Type", "type", 120),
            createColumn("Ordre", "ordre", 100),
            createColumn("Active", "active", 100)
        );
        periodesTable.setItems(FXCollections.observableArrayList(SchoolService.getAllPeriodes()));
        configureDataTable(periodesTable, "Aucune periode scolaire.", 220);
        savePeriodeBtn.setOnAction(e -> {
            if (periodeLabelField.getText().isBlank() || periodeTypeCombo.getValue() == null) {
                AlertUtils.showWarning("Champs requis", "Libelle et type obligatoires.");
                return;
            }
            PeriodeScolaire periode = new PeriodeScolaire();
            periode.setLibelle(periodeLabelField.getText().trim());
            periode.setType(periodeTypeCombo.getValue());
            periode.setOrdre(Integer.parseInt(periodeOrderField.getText().trim()));
            periode.setActive(true);
            if (SchoolService.savePeriode(periode)) {
                navigateToMatieres();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter la periode.");
            }
        });
        periodesCard.getChildren().addAll(
            createErpSection("Periodes"),
            new HBox(12, labelledField("Libelle", periodeLabelField), labelledField("Type", periodeTypeCombo), labelledField("Ordre", periodeOrderField), savePeriodeBtn),
            periodesTable
        );

        VBox fraisCard = new VBox(12);
        fraisCard.getStyleClass().add("erp-card");
        ComboBox<Eleve> fraisEleveCombo = new ComboBox<>(FXCollections.observableArrayList(SchoolService.getAllEleves()));
        TextField fraisLibelleField = new TextField();
        TextField fraisMontantField = new TextField();
        DatePicker fraisEcheancePicker = new DatePicker(LocalDate.now().plusMonths(1));
        Button saveFraisBtn = new Button("Ajouter le frais");
        saveFraisBtn.getStyleClass().add("btn");
        TableView<FraisScolarite> fraisTable = new TableView<>();
        fraisTable.getStyleClass().add("premium-table");
        fraisTable.getColumns().addAll(
            createColumn("Eleve", "eleveNomComplet", 220),
            createColumn("Libelle", "libelle", 220),
            createColumn("Montant", "montant", 120),
            createColumn("Statut", "statut", 120)
        );
        fraisTable.setItems(FXCollections.observableArrayList(SchoolService.getAllFraisScolarite()));
        configureDataTable(fraisTable, "Aucun frais de scolarite.", 220);
        Button markPaidBtn = new Button("Marquer comme paye");
        markPaidBtn.getStyleClass().add("btn-secondary");
        markPaidBtn.setOnAction(e -> {
            FraisScolarite selected = fraisTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez une ligne de frais.");
                return;
            }
            if (SchoolService.markFraisAsPaid(selected.getId())) {
                navigateToMatieres();
            } else {
                AlertUtils.showError("Erreur", "Impossible de mettre a jour le frais.");
            }
        });
        saveFraisBtn.setOnAction(e -> {
            if (fraisEleveCombo.getValue() == null || fraisLibelleField.getText().isBlank() || fraisMontantField.getText().isBlank() || fraisEcheancePicker.getValue() == null) {
                AlertUtils.showWarning("Champs requis", "Renseignez l'eleve, le libelle, le montant et l'echeance.");
                return;
            }
            FraisScolarite frais = new FraisScolarite();
            frais.setMatriculeEleve(fraisEleveCombo.getValue().getMatricule());
            frais.setLibelle(fraisLibelleField.getText().trim());
            frais.setMontant(Double.parseDouble(fraisMontantField.getText().trim()));
            frais.setDateEcheance(fraisEcheancePicker.getValue());
            frais.setStatut("EN_ATTENTE");
            if (SchoolService.saveFrais(frais)) {
                navigateToMatieres();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter ce frais.");
            }
        });
        fraisCard.getChildren().addAll(
            createErpSection("Frais de scolarite"),
            new HBox(12, labelledField("Eleve", fraisEleveCombo), labelledField("Libelle", fraisLibelleField), labelledField("Montant", fraisMontantField), labelledField("Echeance", fraisEcheancePicker), saveFraisBtn, markPaidBtn),
            fraisTable
        );

        page.getChildren().addAll(title, card, configCard, periodesCard, fraisCard);
        contentArea.getChildren().setAll(page);
    }

    private void showEmploisImageStyle() {
        VBox page = new VBox(18);
        page.getStyleClass().add("erp-page");
        Label title = new Label("Emploi du temps");
        title.getStyleClass().add("erp-page-title");

        VBox card = new VBox(16);
        card.getStyleClass().add("erp-card");
        HBox toolbar = new HBox(12);
        ComboBox<String> jourCombo = new ComboBox<>();
        jourCombo.getItems().addAll("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi");
        TextField debutField = new TextField("08:00");
        TextField finField = new TextField("10:00");
        TextField salleField = new TextField();
        ComboBox<Classe> classeCombo = new ComboBox<>(FXCollections.observableArrayList(SchoolService.getAllClasses()));
        ComboBox<Matiere> matiereCombo = new ComboBox<>(FXCollections.observableArrayList(SchoolService.getAllMatieres()));
        ComboBox<Enseignant> enseignantCombo = new ComboBox<>(FXCollections.observableArrayList(SchoolService.getAllEnseignants()));
        Button addBtn = new Button("Enregistrer la seance");
        addBtn.getStyleClass().add("btn");
        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().add("btn-secondary");
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("btn-secondary");
        toolbar.getChildren().addAll(
            labelledField("Jour", jourCombo),
            labelledField("Debut", debutField),
            labelledField("Fin", finField),
            labelledField("Salle", salleField),
            labelledField("Classe", classeCombo),
            labelledField("Matiere", matiereCombo),
            labelledField("Enseignant", enseignantCombo),
            addBtn,
            editBtn,
            deleteBtn
        );

        TableView<Seance> table = createSeanceTable();
        table.getStyleClass().add("premium-table");
        table.setItems(FXCollections.observableArrayList(SchoolService.getAllSeances()));
        configureDataTable(table, "Aucune seance enregistree.", 240);

        addBtn.setOnAction(e -> {
            if (jourCombo.getValue() == null || classeCombo.getValue() == null || matiereCombo.getValue() == null || enseignantCombo.getValue() == null) {
                AlertUtils.showWarning("Champs requis", "Veuillez completer le jour, la classe, la matiere et l'enseignant.");
                return;
            }
            Seance seance = new Seance();
            seance.setJour(jourCombo.getValue());
            seance.setHeureDebut(LocalTime.parse(debutField.getText().trim()));
            seance.setHeureFin(LocalTime.parse(finField.getText().trim()));
            seance.setSalle(salleField.getText().trim());
            seance.setIdClasse(classeCombo.getValue().getIdClasse());
            seance.setCodeMatiere(matiereCombo.getValue().getCode());
            seance.setMatriculeEnseignant(enseignantCombo.getValue().getMatricule());
            if (SchoolService.saveSeance(seance)) {
                AlertUtils.showInfo("Succes", "Seance ajoutee avec succes.");
                navigateToEmplois();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter la seance.");
            }
        });
        editBtn.setOnAction(e -> showSeanceDialog(table.getSelectionModel().getSelectedItem()));
        deleteBtn.setOnAction(e -> deleteSeance(table.getSelectionModel().getSelectedItem()));

        card.getChildren().addAll(createErpSection("Planning hebdomadaire"), toolbar, table);
        page.getChildren().addAll(title, card);
        contentArea.getChildren().setAll(page);
    }

    private void showEnseignantsView() {
        VBox root = new VBox(18);

        VBox actions = createPanel("Registre enseignants", "Suivez le corps enseignant et ajoutez de nouveaux profils.");
        HBox toolbar = new HBox(14);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Button addBtn = new Button("Nouvel enseignant");
        addBtn.getStyleClass().add("btn");
        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().add("btn-secondary");
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("btn-secondary");
        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher par matricule, nom ou grade");
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        toolbar.getChildren().addAll(addBtn, editBtn, deleteBtn, searchField);
        actions.getChildren().add(toolbar);

        VBox tablePanel = createPanel("Equipe pedagogique", "Liste consolidee de l'encadrement.");
        TableView<Enseignant> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        table.getColumns().addAll(
            createColumn("Matricule", "matricule", 110),
            createColumn("Nom", "nom", 140),
            createColumn("Prenom", "prenom", 140),
            createColumn("Grade", "grade", 170),
            createColumn("Telephone", "telephone", 150)
        );
        FilteredList<Enseignant> filtered = new FilteredList<>(FXCollections.observableArrayList(SchoolService.getAllEnseignants()), value -> true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String normalized = newValue == null ? "" : newValue.toLowerCase(Locale.ROOT).trim();
            filtered.setPredicate(enseignant -> normalized.isBlank()
                || enseignant.getMatricule().toLowerCase(Locale.ROOT).contains(normalized)
                || enseignant.getNom().toLowerCase(Locale.ROOT).contains(normalized)
                || enseignant.getPrenom().toLowerCase(Locale.ROOT).contains(normalized)
                || enseignant.getGrade().toLowerCase(Locale.ROOT).contains(normalized));
        });
        table.setItems(filtered);
        tablePanel.getChildren().add(table);

        addBtn.setOnAction(e -> showAddEnseignantDialog());
        editBtn.setOnAction(e -> showEditEnseignantDialog(table.getSelectionModel().getSelectedItem()));
        deleteBtn.setOnAction(e -> deleteEnseignant(table.getSelectionModel().getSelectedItem()));
        root.getChildren().addAll(actions, tablePanel);
        contentArea.getChildren().setAll(root);
    }

    private void showClassesView() {
        VBox root = new VBox(18);

        VBox filters = createPanel("Structuration des classes", "Pilotez les niveaux et surveillez le remplissage.");
        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());
        niveauCombo.setPromptText("Filtrer par niveau");
        Button addClasseBtn = new Button("Nouvelle classe");
        addClasseBtn.getStyleClass().add("btn");
        Button editClasseBtn = new Button("Modifier la classe");
        editClasseBtn.getStyleClass().add("btn-secondary");
        Button deleteClasseBtn = new Button("Supprimer la classe");
        deleteClasseBtn.getStyleClass().add("btn-secondary");
        Button addNiveauBtn = new Button("Nouveau niveau");
        addNiveauBtn.getStyleClass().add("btn-secondary");
        topBar.getChildren().addAll(labelledField("Niveau", niveauCombo), addClasseBtn, editClasseBtn, deleteClasseBtn, addNiveauBtn);
        filters.getChildren().add(topBar);

        VBox board = createPanel("Portefeuille de classes", "Cartes de suivi et pression de capacite.");
        FlowPane grid = new FlowPane();
        grid.setHgap(14);
        grid.setVgap(14);
        board.getChildren().add(grid);

        VBox tablePanel = createPanel("Edition des classes", "Selectionnez une classe pour la modifier ou la supprimer.");
        TableView<Classe> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        table.getColumns().addAll(
            createColumn("Nom complet", "nomComplet", 240),
            createColumn("Niveau", "niveau", 160),
            createColumn("Nom", "nom", 140),
            createColumn("Capacite", "capacite", 120),
            createColumn("Effectif", "effectifActuel", 110)
        );
        tablePanel.getChildren().add(table);

        Runnable refresh = () -> {
            grid.getChildren().clear();
            List<Classe> classes = niveauCombo.getValue() == null
                ? SchoolService.getAllClasses()
                : SchoolService.getClassesByNiveau(niveauCombo.getValue());
        table.setItems(FXCollections.observableArrayList(classes));
        configureDataTable(table, "Aucune classe disponible.", 240);
            if (classes.isEmpty()) {
                grid.getChildren().add(createEmptyState("Aucune classe trouvee pour ce filtre."));
            } else {
                classes.forEach(classe -> grid.getChildren().add(createClasseCard(classe)));
            }
        };
        refresh.run();

        niveauCombo.setOnAction(e -> refresh.run());
        addClasseBtn.setOnAction(e -> showAddClasseDialog(niveauCombo.getValue()));
        editClasseBtn.setOnAction(e -> showEditClasseDialog(table.getSelectionModel().getSelectedItem()));
        deleteClasseBtn.setOnAction(e -> deleteClasse(table.getSelectionModel().getSelectedItem()));
        addNiveauBtn.setOnAction(e -> showAddNiveauDialog());

        root.getChildren().addAll(filters, board, tablePanel);
        contentArea.getChildren().setAll(root);
    }

    private void showMatieresView() {
        VBox root = new VBox(18);

        VBox actions = createPanel("Catalogue des matieres", "Organisez la grille de formation et les coefficients.");
        HBox toolbar = new HBox(14);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Button addBtn = new Button("Nouvelle matiere");
        addBtn.getStyleClass().add("btn");
        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().add("btn-secondary");
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("btn-secondary");
        toolbar.getChildren().addAll(addBtn, editBtn, deleteBtn);
        actions.getChildren().add(toolbar);

        VBox tablePanel = createPanel("Referentiel pedagogique", "Liste complete des matieres parametrees.");
        TableView<Matiere> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        table.getColumns().addAll(
            createColumn("Code", "code", 100),
            createColumn("Libelle", "libelle", 260),
            createColumn("Coefficient", "coefficient", 120)
        );
        table.setItems(FXCollections.observableArrayList(SchoolService.getAllMatieres()));
        configureDataTable(table, "Aucune matiere trouvee.", 240);
        tablePanel.getChildren().add(table);

        addBtn.setOnAction(e -> showAddMatiereDialog());
        editBtn.setOnAction(e -> showEditMatiereDialog(table.getSelectionModel().getSelectedItem()));
        deleteBtn.setOnAction(e -> deleteMatiere(table.getSelectionModel().getSelectedItem()));
        root.getChildren().addAll(actions, tablePanel);
        contentArea.getChildren().setAll(root);
    }

    private void showEmploisView() {
        VBox root = new VBox(18);

        VBox hero = createHeroBlock(
            "Orchestrateur des emplois du temps",
            "Planifiez les seances des enseignants et visualisez immediatement l'impact sur l'emploi du temps des classes.",
            "Planning hebdomadaire"
        );

        VBox actionPanel = createPanel("Gestion des seances", "Ajoutez, modifiez ou supprimez les plages horaires.");
        HBox actionRow = new HBox(14);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        Button addBtn = new Button("Nouvelle seance");
        addBtn.getStyleClass().add("btn");
        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().add("btn-secondary");
        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("btn-secondary");
        actionRow.getChildren().addAll(addBtn, editBtn, deleteBtn);
        actionPanel.getChildren().add(actionRow);

        TableView<Seance> table = createSeanceTable();
        table.setItems(FXCollections.observableArrayList(SchoolService.getAllSeances()));
        configureDataTable(table, "Aucune seance enregistree.", 240);

        VBox board = createPanel("Lecture croisee", "Consultez le planning par classe pour les eleves et par enseignant pour le corps professoral.");
        HBox filters = new HBox(14);
        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.getItems().addAll(SchoolService.getAllClasses());
        classeCombo.setPromptText("Classe");
        ComboBox<Enseignant> enseignantCombo = new ComboBox<>();
        enseignantCombo.getItems().addAll(SchoolService.getAllEnseignants());
        enseignantCombo.setPromptText("Enseignant");
        filters.getChildren().addAll(labelledField("Emploi des eleves", classeCombo), labelledField("Emploi des enseignants", enseignantCombo));

        VBox classeSchedulePanel = createPanel("Emploi du temps des eleves", "Planning hebdomadaire par classe.");
        TableView<Seance> classeTable = createSeanceTable();
        VBox teacherSchedulePanel = createPanel("Emploi du temps des enseignants", "Planning individuel enseignant.");
        TableView<Seance> teacherTable = createSeanceTable();

        classeCombo.setOnAction(e -> {
            Classe selected = classeCombo.getValue();
            classeTable.setItems(FXCollections.observableArrayList(
                selected == null ? List.of() : SchoolService.getSeancesByClasse(selected.getIdClasse())
            ));
        });
        enseignantCombo.setOnAction(e -> {
            Enseignant selected = enseignantCombo.getValue();
            teacherTable.setItems(FXCollections.observableArrayList(
                selected == null ? List.of() : SchoolService.getSeancesByEnseignant(selected.getMatricule())
            ));
        });

        classeSchedulePanel.getChildren().add(classeTable);
        teacherSchedulePanel.getChildren().add(teacherTable);
        HBox previewRow = new HBox(16, classeSchedulePanel, teacherSchedulePanel);
        HBox.setHgrow(classeSchedulePanel, Priority.ALWAYS);
        HBox.setHgrow(teacherSchedulePanel, Priority.ALWAYS);

        addBtn.setOnAction(e -> showSeanceDialog(null));
        editBtn.setOnAction(e -> showSeanceDialog(table.getSelectionModel().getSelectedItem()));
        deleteBtn.setOnAction(e -> deleteSeance(table.getSelectionModel().getSelectedItem()));

        board.getChildren().addAll(filters, previewRow);
        root.getChildren().addAll(hero, actionPanel, table, board);
        contentArea.getChildren().setAll(root);
    }

    private void showInscriptionsView() {
        contentArea.getChildren().clear();

        AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();
        if (annee == null) {
            contentArea.getChildren().add(createPanel("Inscriptions et permutations", "Aucune annee scolaire active configuree."));
            return;
        }

        VBox root = new VBox(18);

        VBox assignmentPanel = createPanel("Affectation initiale", "Inscrivez les eleves non affectes dans une classe disponible.");
        HBox assignmentFilters = new HBox(14);
        assignmentFilters.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> assignmentLevelCombo = new ComboBox<>();
        assignmentLevelCombo.getItems().addAll(SchoolService.getAllNiveaux());
        assignmentLevelCombo.setPromptText("Niveau");
        ComboBox<Classe> assignmentClasseCombo = new ComboBox<>();
        assignmentClasseCombo.setPromptText("Classe cible");
        Button assignBtn = new Button("Affecter");
        assignBtn.getStyleClass().add("btn");

        TableView<Eleve> waitingTable = new TableView<>();
        waitingTable.getStyleClass().add("premium-table");
        waitingTable.getColumns().addAll(
            createColumn("Matricule", "matricule", 110),
            createColumn("Nom", "nom", 140),
            createColumn("Prenom", "prenom", 140),
            createColumn("Niveau", "niveau", 140)
        );
        configureDataTable(waitingTable, "Aucun eleve en attente d'affectation.", 240);

        assignmentFilters.getChildren().addAll(
            labelledField("Niveau", assignmentLevelCombo),
            labelledField("Classe cible", assignmentClasseCombo),
            assignBtn
        );
        assignmentPanel.getChildren().addAll(assignmentFilters, waitingTable);

        assignmentLevelCombo.setOnAction(e -> {
            String level = assignmentLevelCombo.getValue();
            if (level != null) {
                assignmentClasseCombo.getItems().setAll(SchoolService.getClassesByNiveau(level));
        waitingTable.setItems(FXCollections.observableArrayList(SchoolService.getElevesNonInscrits(annee.getIdAnnee(), level)));
            }
        });

        assignBtn.setOnAction(e -> {
            Eleve selected = waitingTable.getSelectionModel().getSelectedItem();
            Classe target = assignmentClasseCombo.getValue();
            if (selected == null || target == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez un eleve et une classe cible.");
                return;
            }
            if (SchoolService.inscription(selected.getMatricule(), annee.getIdAnnee(), target.getIdClasse())) {
                AlertUtils.showInfo("Succes", "Eleve affecte avec succes.");
                waitingTable.getItems().remove(selected);
            } else {
                AlertUtils.showError("Erreur", "La classe cible est probablement a capacite maximale.");
            }
        });

        VBox permutationPanel = createPanel("Permutation et transfert", "Realisez des transferts simples ou des permutations entre deux classes.");
        HBox transferFilters = new HBox(14);
        transferFilters.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> levelCombo = new ComboBox<>();
        levelCombo.getItems().addAll(SchoolService.getAllNiveaux());
        levelCombo.setPromptText("Niveau");
        ComboBox<Classe> sourceClassCombo = new ComboBox<>();
        sourceClassCombo.setPromptText("Classe source");
        ComboBox<Classe> targetClassCombo = new ComboBox<>();
        targetClassCombo.setPromptText("Classe cible");

        transferFilters.getChildren().addAll(
            labelledField("Niveau", levelCombo),
            labelledField("Classe source", sourceClassCombo),
            labelledField("Classe cible", targetClassCombo)
        );

        TableView<Inscription> sourceTable = createInscriptionTable();
        TableView<Inscription> targetTable = createInscriptionTable();

        VBox sourcePanel = createPanel("Effectif source", "Selectionnez l'eleve a deplacer.");
        sourcePanel.getStyleClass().add("permutation-panel");
        sourcePanel.setMinWidth(420);
        sourcePanel.getChildren().add(sourceTable);
        VBox targetPanel = createPanel("Effectif cible", "Selectionnez un eleve si vous souhaitez faire une permutation.");
        targetPanel.getStyleClass().add("permutation-panel");
        targetPanel.setMinWidth(420);
        targetPanel.getChildren().add(targetTable);

        SplitPane tableRow = new SplitPane(sourcePanel, targetPanel);
        tableRow.getStyleClass().add("permutation-split");
        tableRow.setDividerPositions(0.5);
        tableRow.setPrefHeight(360);

        HBox actionRow = new HBox(14);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        Button transferBtn = new Button("Transferer vers la classe cible");
        transferBtn.getStyleClass().add("btn");
        Button swapBtn = new Button("Permuter les deux eleves");
        swapBtn.getStyleClass().add("btn-secondary");
        Button refreshBtn = new Button("Actualiser");
        refreshBtn.getStyleClass().add("btn-secondary");
        actionRow.getChildren().addAll(transferBtn, swapBtn, refreshBtn);

        Runnable reloadTables = () -> {
            sourceTable.getItems().clear();
            targetTable.getItems().clear();
            if (sourceClassCombo.getValue() != null) {
                sourceTable.setItems(FXCollections.observableArrayList(
                    SchoolService.getActiveInscriptionsByClasse(sourceClassCombo.getValue().getIdClasse(), annee.getIdAnnee())
                ));
            }
            if (targetClassCombo.getValue() != null) {
                targetTable.setItems(FXCollections.observableArrayList(
                    SchoolService.getActiveInscriptionsByClasse(targetClassCombo.getValue().getIdClasse(), annee.getIdAnnee())
                ));
            }
        };

        levelCombo.setOnAction(e -> {
            String level = levelCombo.getValue();
            if (level != null) {
                List<Classe> levelClasses = SchoolService.getClassesByNiveau(level);
                sourceClassCombo.getItems().setAll(levelClasses);
                targetClassCombo.getItems().setAll(levelClasses);
                sourceTable.getItems().clear();
                targetTable.getItems().clear();
            }
        });

        sourceClassCombo.setOnAction(e -> reloadTables.run());
        targetClassCombo.setOnAction(e -> reloadTables.run());
        refreshBtn.setOnAction(e -> reloadTables.run());

        transferBtn.setOnAction(e -> {
            Inscription selected = sourceTable.getSelectionModel().getSelectedItem();
            Classe sourceClass = sourceClassCombo.getValue();
            Classe targetClass = targetClassCombo.getValue();
            if (selected == null || sourceClass == null || targetClass == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez une classe source, une classe cible et un eleve.");
                return;
            }
            if (sourceClass.getIdClasse() == targetClass.getIdClasse()) {
                AlertUtils.showWarning("Operation invalide", "La classe source et la classe cible doivent etre differentes.");
                return;
            }
            if (SchoolService.transferEleveToClasse(selected.getMatricule(), annee.getIdAnnee(), sourceClass.getIdClasse(), targetClass.getIdClasse())) {
                AlertUtils.showInfo("Succes", "Transfert termine.");
                reloadTables.run();
            } else {
                AlertUtils.showError("Erreur", "Transfert impossible. Verifiez la capacite de la classe cible.");
            }
        });

        swapBtn.setOnAction(e -> {
            Inscription sourceSelected = sourceTable.getSelectionModel().getSelectedItem();
            Inscription targetSelected = targetTable.getSelectionModel().getSelectedItem();
            Classe sourceClass = sourceClassCombo.getValue();
            Classe targetClass = targetClassCombo.getValue();
            if (sourceSelected == null || targetSelected == null || sourceClass == null || targetClass == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez un eleve dans chaque classe pour la permutation.");
                return;
            }
            if (sourceClass.getIdClasse() == targetClass.getIdClasse()) {
                AlertUtils.showWarning("Operation invalide", "Selectionnez deux classes distinctes.");
                return;
            }
            if (SchoolService.swapElevesBetweenClasses(
                sourceSelected.getMatricule(),
                sourceClass.getIdClasse(),
                targetSelected.getMatricule(),
                targetClass.getIdClasse(),
                annee.getIdAnnee()
            )) {
                AlertUtils.showInfo("Succes", "Permutation enregistree.");
                reloadTables.run();
            } else {
                AlertUtils.showError("Erreur", "La permutation a echoue. Verifiez l'etat des inscriptions.");
            }
        });

        permutationPanel.getChildren().addAll(transferFilters, tableRow, actionRow);

        VBox invitationPanel = createPanel("Codes d'invitation parents", "Generez des codes pour lier un parent ou tuteur a un eleve.");
        HBox invitationBar = new HBox(14);
        invitationBar.setAlignment(Pos.CENTER_LEFT);
        ComboBox<Eleve> eleveCombo = new ComboBox<>(FXCollections.observableArrayList(SchoolService.getAllEleves()));
        eleveCombo.setPromptText("Eleve");
        eleveCombo.setPrefWidth(260);
        ComboBox<String> lienCombo = new ComboBox<>();
        lienCombo.getItems().addAll("MERE", "PERE", "TUTEUR");
        lienCombo.setValue("TUTEUR");
        DatePicker expirationPicker = new DatePicker(LocalDate.now().plusMonths(3));
        Button generateInviteBtn = new Button("Generer le code");
        generateInviteBtn.getStyleClass().add("btn");

        TableView<InvitationCode> invitationTable = new TableView<>();
        invitationTable.getStyleClass().add("premium-table");
        invitationTable.getColumns().addAll(
            createColumn("Code", "code", 170),
            createColumn("Matricule", "matriculeEleve", 120),
            createColumn("Eleve", "eleveNomComplet", 220),
            createColumn("Lien", "lienParente", 120),
            createColumn("Statut", "statutLabel", 120)
        );
        invitationTable.setItems(FXCollections.observableArrayList(SchoolService.getAllInvitationCodes()));
        configureDataTable(invitationTable, "Aucun code d'invitation.", 220);

        VBox validationPanel = createPanel("Demandes de liaison parents", "Validez les parents qui ont saisi un code d'invitation.");
        TableView<ParentEleveLink> validationTable = new TableView<>();
        validationTable.getStyleClass().add("premium-table");
        validationTable.getColumns().addAll(
            createColumn("Parent", "parentNomComplet", 220),
            createColumn("Email", "parentEmail", 220),
            createColumn("Eleve", "eleveNomComplet", 220),
            createColumn("Matricule", "matriculeEleve", 120),
            createColumn("Lien", "lienParente", 100),
            createColumn("Statut", "statutLabel", 120)
        );
        validationTable.setItems(FXCollections.observableArrayList(SchoolService.getAllParentEleveLinks()));
        configureDataTable(validationTable, "Aucune demande parent.", 240);

        Button validateLinkBtn = new Button("Valider la liaison");
        validateLinkBtn.getStyleClass().add("btn");
        Button rejectLinkBtn = new Button("Refuser");
        rejectLinkBtn.getStyleClass().add("btn-secondary");
        Button refreshLinksBtn = new Button("Actualiser");
        refreshLinksBtn.getStyleClass().add("btn-secondary");
        Runnable refreshLinks = () -> validationTable.setItems(FXCollections.observableArrayList(SchoolService.getAllParentEleveLinks()));
        refreshLinksBtn.setOnAction(e -> refreshLinks.run());
        validateLinkBtn.setOnAction(e -> {
            ParentEleveLink selected = validationTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez une demande de liaison.");
                return;
            }
            if (selected.isValideParAdmin()) {
                AlertUtils.showInfo("Deja validee", "Cette liaison est deja validee.");
                return;
            }
            if (SchoolService.validateParentEleveLink(selected.getId())) {
                AlertUtils.showInfo("Liaison validee", "Le parent peut maintenant voir l'enfant dans son espace.");
                refreshLinks.run();
            } else {
                AlertUtils.showError("Erreur", "Impossible de valider cette liaison.");
            }
        });
        rejectLinkBtn.setOnAction(e -> {
            ParentEleveLink selected = validationTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez une demande de liaison.");
                return;
            }
            if (selected.isValideParAdmin()) {
                AlertUtils.showWarning("Action bloquee", "Une liaison deja validee ne peut pas etre refusee ici.");
                return;
            }
            if (SchoolService.rejectParentEleveLink(selected.getId())) {
                AlertUtils.showInfo("Demande refusee", "La demande de liaison a ete supprimee.");
                refreshLinks.run();
            } else {
                AlertUtils.showError("Erreur", "Impossible de refuser cette demande.");
            }
        });
        HBox validationActions = new HBox(12, validateLinkBtn, rejectLinkBtn, refreshLinksBtn);
        validationActions.setAlignment(Pos.CENTER_LEFT);
        validationPanel.getChildren().addAll(validationActions, validationTable);

        Button refreshInvitesBtn = new Button("Actualiser");
        refreshInvitesBtn.getStyleClass().add("btn-secondary");
        refreshInvitesBtn.setOnAction(e -> invitationTable.setItems(FXCollections.observableArrayList(SchoolService.getAllInvitationCodes())));

        generateInviteBtn.setOnAction(e -> {
            Eleve eleve = eleveCombo.getValue();
            if (eleve == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez d'abord un eleve.");
                return;
            }
            InvitationCode invitationCode = SchoolService.generateInvitationCode(
                eleve.getMatricule(),
                lienCombo.getValue(),
                expirationPicker.getValue()
            );
            if (invitationCode == null) {
                AlertUtils.showError("Erreur", "Impossible de generer le code d'invitation.");
                return;
            }
            invitationTable.setItems(FXCollections.observableArrayList(SchoolService.getAllInvitationCodes()));
            invitationTable.getSelectionModel().selectFirst();
            AlertUtils.showInfo(
                "Code genere",
                "Code cree avec succes.\n\nEleve : " + eleve.getNomComplet() + "\nCode : " + invitationCode.getCode()
            );
        });

        invitationBar.getChildren().addAll(
            labelledField("Eleve", eleveCombo),
            labelledField("Lien", lienCombo),
            labelledField("Expiration", expirationPicker),
            generateInviteBtn,
            refreshInvitesBtn
        );
        invitationPanel.getChildren().addAll(invitationBar, invitationTable);

        root.getChildren().addAll(assignmentPanel, permutationPanel, invitationPanel, validationPanel);
        contentArea.getChildren().setAll(root);
    }

    private void showNotesView() {
        VBox root = new VBox(18);
        VBox filters = createPanel("Pilotage des notes", "Selectionnez la classe, la matiere et le trimestre pour piloter les evaluations.");
        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());
        niveauCombo.setPromptText("Niveau");
        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.setPromptText("Classe");
        ComboBox<Matiere> matiereCombo = new ComboBox<>();
        matiereCombo.getItems().addAll(SchoolService.getAllMatieres());
        matiereCombo.setPromptText("Matiere");
        ComboBox<Integer> trimestreCombo = new ComboBox<>();
        trimestreCombo.getItems().addAll(1, 2, 3);
        trimestreCombo.setPromptText("Trimestre");

        topBar.getChildren().addAll(
            labelledField("Niveau", niveauCombo),
            labelledField("Classe", classeCombo),
            labelledField("Matiere", matiereCombo),
            labelledField("Trimestre", trimestreCombo)
        );
        filters.getChildren().add(topBar);

        VBox editor = createPanel("Grille de saisie", "Modifiez les notes puis enregistrez en un bloc.");
        TableView<NoteEntry> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        table.setEditable(true);

        TableColumn<NoteEntry, String> studentCol = new TableColumn<>("Eleve");
        studentCol.setCellValueFactory(data -> data.getValue().nomProperty());
        studentCol.setPrefWidth(220);

        TableColumn<NoteEntry, Double> devoirCol = new TableColumn<>("Devoir");
        devoirCol.setCellValueFactory(data -> data.getValue().devoirProperty().asObject());
        devoirCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        devoirCol.setOnEditCommit(event -> event.getRowValue().setDevoir(event.getNewValue()));

        TableColumn<NoteEntry, Double> examenCol = new TableColumn<>("Examen");
        examenCol.setCellValueFactory(data -> data.getValue().examProperty().asObject());
        examenCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        examenCol.setOnEditCommit(event -> event.getRowValue().setExam(event.getNewValue()));

        TableColumn<NoteEntry, Double> compositionCol = new TableColumn<>("Composition");
        compositionCol.setCellValueFactory(data -> data.getValue().compositionProperty().asObject());
        compositionCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        compositionCol.setOnEditCommit(event -> event.getRowValue().setComposition(event.getNewValue()));

        table.getColumns().addAll(studentCol, devoirCol, examenCol, compositionCol);

        Button saveBtn = new Button("Enregistrer les notes");
        saveBtn.getStyleClass().add("btn");

        niveauCombo.setOnAction(e -> {
            if (niveauCombo.getValue() != null) {
                classeCombo.getItems().setAll(SchoolService.getClassesByNiveau(niveauCombo.getValue()));
            }
        });

        Runnable load = () -> {
            if (classeCombo.getValue() == null || matiereCombo.getValue() == null || trimestreCombo.getValue() == null) {
                return;
            }
            loadStudentsForNotes(table, classeCombo.getValue(), matiereCombo.getValue(), trimestreCombo.getValue());
        };
        classeCombo.setOnAction(e -> load.run());
        matiereCombo.setOnAction(e -> load.run());
        trimestreCombo.setOnAction(e -> load.run());

        saveBtn.setOnAction(e -> saveGradesAdmin(table, classeCombo.getValue(), matiereCombo.getValue(), trimestreCombo.getValue()));

        editor.getChildren().addAll(table, saveBtn);
        root.getChildren().addAll(filters, editor);
        contentArea.getChildren().setAll(root);
    }

    private void loadStudentsForNotes(TableView<NoteEntry> table, Classe classe, Matiere matiere, int trimestre) {
        AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();
        if (annee == null) {
            return;
        }

        List<NoteEntry> entries = new ArrayList<>();
        String query = "SELECT e.matricule, e.nom, e.prenom, n.noteDevoir, n.noteExamens, n.noteComposition " +
                      "FROM ELEVE e " +
                      "JOIN INSCRIPTION i ON e.matricule = i.matricule " +
                      "LEFT JOIN NOTE n ON e.matricule = n.matricule AND n.idClasse = ? AND n.codeMatiere = ? AND n.trimestre = ? AND n.idAnnee = ? " +
                      "WHERE i.idClasse = ? AND i.idAnnee = ? AND i.statut = 'ACTIF' " +
                      "ORDER BY e.nom, e.prenom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, classe.getIdClasse());
            stmt.setString(2, matiere.getCode());
            stmt.setInt(3, trimestre);
            stmt.setInt(4, annee.getIdAnnee());
            stmt.setInt(5, classe.getIdClasse());
            stmt.setInt(6, annee.getIdAnnee());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                NoteEntry entry = new NoteEntry(rs.getString("prenom") + " " + rs.getString("nom"));
                entry.setMatricule(rs.getString("matricule"));
                entry.setDevoir(rs.getObject("noteDevoir") != null ? rs.getDouble("noteDevoir") : null);
                entry.setExam(rs.getObject("noteExamens") != null ? rs.getDouble("noteExamens") : null);
                entry.setComposition(rs.getObject("noteComposition") != null ? rs.getDouble("noteComposition") : null);
                entries.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(FXCollections.observableArrayList(entries));
    }

    private void saveGradesAdmin(TableView<NoteEntry> table, Classe classe, Matiere matiere, Integer trimestre) {
        AnneeScolaire annee = SchoolService.getActiveAnneeScolaire();
        if (annee == null || classe == null || matiere == null || trimestre == null) {
            AlertUtils.showError("Erreur", "Veuillez selectionner tous les filtres.");
            return;
        }

        int saved = 0;
        for (NoteEntry entry : table.getItems()) {
            if (entry.getMatricule() == null || entry.getMatricule().isBlank()) {
                continue;
            }
            Note note = new Note();
            note.setMatricule(entry.getMatricule());
            note.setIdAnnee(annee.getIdAnnee());
            note.setIdClasse(classe.getIdClasse());
            note.setCodeMatiere(matiere.getCode());
            note.setTrimestre(trimestre);
            note.setMatriculeEnseignant(SchoolService.getEnseignantMatriculeByClasseAndMatiere(classe.getIdClasse(), matiere.getCode()));
            note.setNoteDevoir(entry.getDevoir());
            note.setNoteExamens(entry.getExam());
            note.setNoteComposition(entry.getComposition());
            if (SchoolService.saveNote(note)) {
                saved++;
            }
        }
        AlertUtils.showInfo("Succes", saved + " note(s) enregistree(s).");
    }

    private void showBulletinsView() {
        VBox root = new VBox(18);
        VBox filters = createPanel("Edition des bulletins", "Selectionnez un eleve pour generer, imprimer ou envoyer son bulletin individuel.");
        HBox topBar = new HBox(14);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());
        niveauCombo.setPromptText("Niveau");
        niveauCombo.setPrefWidth(240);
        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.setPromptText("Classe");
        classeCombo.setPrefWidth(280);
        ComboBox<Eleve> eleveCombo = new ComboBox<>();
        eleveCombo.setPromptText("Eleve");
        eleveCombo.setPrefWidth(280);
        ComboBox<Integer> trimestreCombo = new ComboBox<>();
        trimestreCombo.getItems().addAll(1, 2, 3);
        trimestreCombo.setPromptText("Trimestre");
        trimestreCombo.setPrefWidth(200);
        Button generateBtn = new Button("Generer");
        generateBtn.getStyleClass().add("btn");
        Button printBtn = new Button("Imprimer le bulletin");
        printBtn.getStyleClass().add("btn-secondary");
        printBtn.setDisable(true);
        Button sendMailBtn = new Button("Envoyer au parent");
        sendMailBtn.getStyleClass().add("btn-secondary");
        sendMailBtn.setDisable(true);

        topBar.getChildren().addAll(
            labelledField("Niveau", niveauCombo),
            labelledField("Classe", classeCombo),
            labelledField("Eleve", eleveCombo),
            labelledField("Trimestre", trimestreCombo),
            generateBtn,
            printBtn,
            sendMailBtn
        );
        filters.getChildren().add(topBar);

        VBox resultPanel = createPanel("Synthese trimestrielle", "Tableau multi-matieres par eleve.");
        final Node[] printableNode = new Node[1];
        final Eleve[] generatedEleve = new Eleve[1];
        final Classe[] generatedClasse = new Classe[1];

        Runnable loadAllBulletinChoices = () -> {
            List<Classe> classes = SchoolService.getAllClasses();
            List<Eleve> eleves = SchoolService.getAllEleves();
            classeCombo.getItems().setAll(classes);
            eleveCombo.getItems().setAll(eleves);
            if (!classes.isEmpty()) {
                classeCombo.getSelectionModel().selectFirst();
            }
            if (!eleves.isEmpty()) {
                eleveCombo.getSelectionModel().selectFirst();
            }
        };

        niveauCombo.setOnAction(e -> {
            if (niveauCombo.getValue() != null) {
                List<Classe> classes = SchoolService.getClassesByNiveau(niveauCombo.getValue());
                if (classes.isEmpty()) {
                    classes = SchoolService.getAllClasses();
                }
                classeCombo.getItems().setAll(classes);
                eleveCombo.getItems().clear();
                if (!classes.isEmpty()) {
                    classeCombo.getSelectionModel().selectFirst();
                } else {
                    eleveCombo.getItems().setAll(SchoolService.getAllEleves());
                }
            }
        });
        classeCombo.setOnAction(e -> {
            if (classeCombo.getValue() != null) {
                List<Eleve> eleves = SchoolService.getElevesByClasse(classeCombo.getValue().getIdClasse());
                if (eleves.isEmpty()) {
                    eleves = SchoolService.getAllEleves().stream()
                        .filter(eleve -> {
                            Classe classe = SchoolService.getClasseForEleve(eleve.getMatricule());
                            return classe != null && classe.getIdClasse() == classeCombo.getValue().getIdClasse();
                        })
                        .toList();
                }
                eleveCombo.getItems().setAll(eleves);
                if (!eleves.isEmpty()) {
                    eleveCombo.getSelectionModel().selectFirst();
                }
            } else {
                eleveCombo.getItems().setAll(SchoolService.getAllEleves());
            }
        });

        loadAllBulletinChoices.run();
        if (!niveauCombo.getItems().isEmpty()) {
            niveauCombo.getSelectionModel().selectFirst();
        }
        if (trimestreCombo.getValue() == null) {
            trimestreCombo.setValue(1);
        }

        generateBtn.setOnAction(e -> {
            Eleve selectedEleve = eleveCombo.getValue();
            Classe selectedClasse = classeCombo.getValue();
            if (selectedClasse == null && selectedEleve != null) {
                selectedClasse = SchoolService.getClasseForEleve(selectedEleve.getMatricule());
            }
            if (selectedClasse == null || selectedEleve == null || trimestreCombo.getValue() == null) {
                AlertUtils.showWarning("Selection requise", "Choisissez une classe, un eleve et un trimestre.");
                return;
            }
            resultPanel.getChildren().removeIf(node -> node instanceof ScrollPane || node instanceof TableView || node.getStyleClass().contains("profile-page"));
            Node bulletin = buildSingleBulletinNode(selectedEleve, selectedClasse, trimestreCombo.getValue());
            printableNode[0] = bulletin;
            generatedEleve[0] = selectedEleve;
            generatedClasse[0] = selectedClasse;
            resultPanel.getChildren().add(bulletin);
            printBtn.setDisable(false);
            sendMailBtn.setDisable(false);
        });

        printBtn.setOnAction(e -> {
            if (printableNode[0] == null) {
                AlertUtils.showWarning("Impression", "Generez d'abord un bulletin.");
                return;
            }
            String studentName = generatedEleve[0] == null ? "" : " - " + generatedEleve[0].getNomComplet();
            printNode(printableNode[0], "Bulletin" + studentName + " - Trimestre " + trimestreCombo.getValue());
        });

        sendMailBtn.setOnAction(e -> {
            if (generatedEleve[0] == null || generatedClasse[0] == null || trimestreCombo.getValue() == null) {
                AlertUtils.showWarning("Envoi", "Generez d'abord un bulletin.");
                return;
            }
            sendBulletinToParent(generatedEleve[0], generatedClasse[0], trimestreCombo.getValue());
        });

        root.getChildren().addAll(filters, resultPanel);
        contentArea.getChildren().setAll(root);
    }

    private void sendBulletinToParent(Eleve eleve, Classe classe, int trimestre) {
        if (eleve.getParentEmail() == null || eleve.getParentEmail().isBlank()) {
            AlertUtils.showWarning("Email parent manquant", "Aucun email parent n'est enregistre pour cet eleve.");
            return;
        }

        List<Note> notes = SchoolService.getNotesByClasseAndTrimestre(classe.getIdClasse(), trimestre);
        List<Note> studentNotes = notes.stream()
            .filter(note -> eleve.getMatricule().equals(note.getMatricule()))
            .toList();
        double moyenne = studentNotes.isEmpty() ? 0.0 : studentNotes.stream().mapToDouble(Note::getMoyenne).average().orElse(0.0);

        String subject = "Bulletin de " + eleve.getNomComplet() + " - " + classe.getNomComplet() + " - Trimestre " + trimestre;
        StringBuilder body = new StringBuilder();
        body.append("Bonjour,\r\n\r\n");
        body.append("Veuillez trouver ci-dessous le resume du bulletin de votre enfant.\r\n\r\n");
        body.append("Eleve : ").append(eleve.getNomComplet()).append("\r\n");
        body.append("Classe : ").append(classe.getNomComplet()).append("\r\n");
        body.append("Trimestre : ").append(trimestre).append("\r\n");
        body.append("Moyenne generale : ").append(String.format(Locale.US, "%.2f", moyenne)).append("\r\n\r\n");
        if (studentNotes.isEmpty()) {
            body.append("Aucune note disponible pour ce trimestre.\r\n");
        } else {
            body.append("Detail des notes :\r\n");
            for (Note note : studentNotes) {
                body.append("- ").append(note.getCodeMatiere())
                    .append(" : devoir ").append(formatNote(note.getNoteDevoir()))
                    .append(", examen ").append(formatNote(note.getNoteExamens()))
                    .append(", composition ").append(formatNote(note.getNoteComposition()))
                    .append(", moyenne ").append(String.format(Locale.US, "%.2f", note.getMoyenne()))
                    .append("\r\n");
            }
        }
        body.append("\r\nCordialement,\r\nAdministration scolaire");

        try {
            if (openGmailCompose(eleve.getParentEmail(), subject, body.toString())) {
                AlertUtils.showInfo("Gmail", "Gmail a ete ouvert pour envoyer le bulletin de " + eleve.getNomComplet() + " a " + eleve.getParentEmail() + ".");
            } else {
                AlertUtils.showWarning(
                    "Gmail indisponible",
                    "Impossible d'ouvrir Gmail automatiquement.\n\nDestinataire : " + eleve.getParentEmail()
                        + "\nObjet : " + subject
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtils.showError(
                "Erreur d'envoi",
                "Impossible d'ouvrir la messagerie pour envoyer le bulletin.\n\nDestinataire : " + eleve.getParentEmail()
            );
        }
    }

    private String encodeMailComponent(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private boolean openGmailCompose(String to, String subject, String body) {
        String gmailUrl = "https://mail.google.com/mail/?view=cm&fs=1"
            + "&to=" + encodeMailComponent(to)
            + "&su=" + encodeMailComponent(subject)
            + "&body=" + encodeMailComponent(body);
        return openBrowserUrl(gmailUrl);
    }

    private boolean openBrowserUrl(String url) {
        try {
            URI uri = URI.create(url);
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
                return true;
            }
        } catch (Exception ignored) {
        }

        try {
            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            return true;
        } catch (Exception ignored) {
        }

        try {
            new ProcessBuilder("cmd", "/c", "start", "", url).start();
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    private boolean openMailClient(String mailto) {
        try {
            URI uri = URI.create(mailto);
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.MAIL)) {
                    desktop.mail(uri);
                    return true;
                }
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri);
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        try {
            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", mailto).start();
            return true;
        } catch (Exception ignored) {
        }

        try {
            new ProcessBuilder("cmd", "/c", "start", "", mailto).start();
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    private String formatNote(Double value) {
        return value == null ? "-" : String.format(Locale.US, "%.2f", value);
    }

    private Node buildSingleBulletinNode(Eleve eleve, Classe classe, int trimestre) {
        VBox bulletin = new VBox(18);
        bulletin.getStyleClass().add("profile-page");
        bulletin.setPadding(new Insets(22));
        bulletin.setMaxWidth(Double.MAX_VALUE);

        HBox header = new HBox(18);
        header.setAlignment(Pos.CENTER_LEFT);
        Node avatar = createPhotoAvatar(eleve.getPhoto(), initials(eleve.getPrenom(), eleve.getNom()), 92);
        VBox identity = new VBox(6);
        Label title = new Label("Bulletin de " + eleve.getNomComplet());
        title.getStyleClass().add("erp-page-title");
        Label meta = new Label(classe.getNomComplet() + "  -  Trimestre " + trimestre + "  -  Matricule " + eleve.getMatricule());
        meta.getStyleClass().add("soft-muted");
        identity.getChildren().addAll(title, meta);
        header.getChildren().addAll(avatar, identity);

        List<Note> notes = SchoolService.getNotesByClasseAndTrimestre(classe.getIdClasse(), trimestre).stream()
            .filter(note -> eleve.getMatricule().equals(note.getMatricule()))
            .toList();

        TableView<Note> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        table.getColumns().addAll(
            createColumn("Matiere", "codeMatiere", 160),
            createColumn("Devoir", "noteDevoir", 110),
            createColumn("Examen", "noteExamens", 110),
            createColumn("Composition", "noteComposition", 130),
            createColumn("Moyenne", "moyenne", 120)
        );
        table.setItems(FXCollections.observableArrayList(notes));
        configureDataTable(table, "Aucune note pour cet eleve.", 260);

        double moyenne = notes.isEmpty() ? 0.0 : notes.stream().mapToDouble(Note::getMoyenne).average().orElse(0.0);
        HBox summary = new HBox(14);
        summary.getChildren().addAll(
            profileStat("Moyenne generale", String.format(Locale.US, "%.2f / 20", moyenne)),
            profileStat("Appreciation", getAppreciation(moyenne)),
            profileStat("Parent", safeValue(eleve.getParentNomComplet())),
            profileStat("Email parent", safeValue(eleve.getParentEmail()))
        );

        bulletin.getChildren().addAll(header, summary, table);
        return bulletin;
    }

    private TableView<Eleve> buildBulletinTable(Classe classe, int trimestre) {
        TableView<Eleve> table = new TableView<>();
        table.getStyleClass().add("premium-table");

        List<Eleve> eleves = SchoolService.getElevesByClasse(classe.getIdClasse());
        List<Note> notes = SchoolService.getNotesByClasseAndTrimestre(classe.getIdClasse(), trimestre);

        TableColumn<Eleve, String> studentCol = new TableColumn<>("Eleve");
        studentCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom() + " " + data.getValue().getPrenom()));
        studentCol.setPrefWidth(220);
        table.getColumns().add(studentCol);

        List<String> matieres = notes.stream().map(Note::getCodeMatiere).distinct().sorted().toList();
        for (String codeMatiere : matieres) {
            TableColumn<Eleve, String> col = new TableColumn<>(codeMatiere);
            col.setCellValueFactory(data -> {
                String matricule = data.getValue().getMatricule();
                return notes.stream()
                    .filter(note -> note.getMatricule().equals(matricule) && note.getCodeMatiere().equals(codeMatiere))
                    .findFirst()
                    .map(note -> {
                        double devoir = note.getNoteDevoir() != null ? note.getNoteDevoir() : 0.0;
                        double examen = note.getNoteExamens() != null ? note.getNoteExamens() : 0.0;
                        double composition = note.getNoteComposition() != null ? note.getNoteComposition() : 0.0;
                        return String.format(Locale.US, "D %.1f  E %.1f  C %.1f", devoir, examen, composition);
                    })
                    .map(SimpleStringProperty::new)
                    .orElse(new SimpleStringProperty("-"));
            });
            col.setPrefWidth(150);
            table.getColumns().add(col);
        }

        TableColumn<Eleve, String> moyenneCol = new TableColumn<>("Moyenne");
        moyenneCol.setCellValueFactory(data -> {
            List<Note> studentNotes = notes.stream().filter(note -> note.getMatricule().equals(data.getValue().getMatricule())).toList();
            if (studentNotes.isEmpty()) {
                return new SimpleStringProperty("-");
            }
            double sum = 0.0;
            int count = 0;
            for (Note note : studentNotes) {
                sum += note.getMoyenne();
                count++;
            }
            return new SimpleStringProperty(String.format(Locale.US, "%.2f", count == 0 ? 0.0 : sum / count));
        });
        moyenneCol.setPrefWidth(110);
        table.getColumns().add(moyenneCol);
        table.setItems(FXCollections.observableArrayList(eleves));
        return table;
    }

    private void configureAssistant() {
        if (assistantMessages == null || assistantQuickActions == null) {
            return;
        }
        assistantMessages.getChildren().clear();
        assistantQuickActions.getChildren().clear();
        addAssistantMessage(
            "Assistant",
            "Bienvenue dans la console premium. Je peux vous aider sur la vue globale, les classes chargees, les permutations et les notes.",
            false
        );
        for (String action : DashboardAssistantService.getQuickActions(true)) {
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
        String reply = DashboardAssistantService.answerForAdmin(value);
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

    private void exportAdminData(String type) {
        try {
            java.nio.file.Path exportDir = java.nio.file.Paths.get(System.getProperty("user.home"), "Documents", "gestion-scolaire-exports");
            java.nio.file.Path output;
            switch (type) {
                case "eleves" -> output = SchoolService.exportElevesCsv(exportDir);
                case "notes" -> output = SchoolService.exportNotesCsv(exportDir);
                default -> output = SchoolService.backupSnapshot(exportDir);
            }
            if (SessionManager.getCurrentUser() != null) {
                SchoolService.audit("EXPORT_" + type.toUpperCase(Locale.ROOT), output.toString(), SessionManager.getCurrentUser().getId(), SessionManager.getCurrentUser().getUsername());
            }
            AlertUtils.showInfo("Export termine", "Fichier genere : " + output.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertUtils.showError("Erreur d'export", "Impossible de generer le fichier.");
        }
    }

    private void activateNav(Button activeButton, String context) {
        if (contextBadge != null) {
            contextBadge.setText(context);
        }
        List<Button> navButtons = List.of(
            dashboardNavButton,
            elevesNavButton,
            enseignantsNavButton,
            usersNavButton,
            classesNavButton,
            matieresNavButton,
            emploisNavButton,
            inscriptionsNavButton,
            notesNavButton,
            bulletinsNavButton
        );
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

    private VBox createMiniInsight(String titleText, String valueText) {
        VBox card = new VBox(6);
        card.getStyleClass().add("mini-card");
        Label title = new Label(titleText);
        title.getStyleClass().add("mini-card-title");
        Label value = new Label(valueText);
        value.getStyleClass().add("mini-card-badge");
        card.getChildren().addAll(title, value);
        return card;
    }

    private VBox createClasseCard(Classe classe) {
        VBox card = new VBox(10);
        card.getStyleClass().add("class-card");
        card.setPrefWidth(235);
        card.setPadding(new Insets(18));

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(classe.getNomComplet());
        name.getStyleClass().add("class-card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = new Label(classe.getEffectifActuel() + " / " + classe.getCapacite());
        badge.getStyleClass().add("mini-card-badge");
        top.getChildren().addAll(name, spacer, badge);

        ProgressBar progress = new ProgressBar(classe.getRemplissage());
        progress.getStyleClass().add("class-progress");
        progress.setPrefWidth(190);
        if (classe.getRemplissage() >= 0.9) {
            progress.setStyle("-fx-accent: #d14d4d;");
        } else if (classe.getRemplissage() >= 0.7) {
            progress.setStyle("-fx-accent: #d99b37;");
        } else {
            progress.setStyle("-fx-accent: #2d8c74;");
        }

        Label subtitle = new Label("Remplissage " + Math.round(classe.getRemplissage() * 100) + " %");
        subtitle.getStyleClass().add("class-card-subtitle");
        card.getChildren().addAll(top, progress, subtitle);
        return card;
    }

    private TableView<Inscription> createInscriptionTable() {
        TableView<Inscription> table = new TableView<>();
        table.getStyleClass().addAll("premium-table", "permutation-table");
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        configureDataTable(table, "Aucun eleve dans cette classe.", 280);
        table.setMaxHeight(Double.MAX_VALUE);

        TableColumn<Inscription, String> matriculeCol = new TableColumn<>("Matricule");
        matriculeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMatricule()));
        matriculeCol.setPrefWidth(150);
        matriculeCol.setMinWidth(140);

        TableColumn<Inscription, String> nameCol = new TableColumn<>("Eleve");
        nameCol.setCellValueFactory(data -> {
            Eleve eleve = data.getValue().getEleve();
            String text = eleve == null ? "" : eleve.getPrenom() + " " + eleve.getNom();
            return new SimpleStringProperty(text);
        });
        nameCol.setPrefWidth(280);
        nameCol.setMinWidth(240);

        table.getColumns().addAll(matriculeCol, nameCol);
        return table;
    }

    private TableView<Seance> createSeanceTable() {
        TableView<Seance> table = new TableView<>();
        table.getStyleClass().add("premium-table");
        configureDataTable(table, "Aucune seance enregistree.", 240);
        table.getColumns().addAll(
            createColumn("Jour", "jour", 120),
            createColumn("Horaire", "plageHoraire", 150),
            createColumn("Classe", "classeNom", 180),
            createColumn("Matiere", "matiereLibelle", 180),
            createColumn("Enseignant", "enseignantNom", 170),
            createColumn("Salle", "salle", 100)
        );
        return table;
    }

    private void showSeanceDialog(Seance source) {
        boolean creation = source == null;
        Seance seanceSource = creation ? new Seance() : source;

        Dialog<Seance> dialog = new Dialog<>();
        dialog.setTitle(creation ? "Nouvelle seance" : "Modifier seance");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> jourCombo = new ComboBox<>();
        jourCombo.getItems().addAll("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi");
        jourCombo.setValue(seanceSource.getJour());

        TextField startField = new TextField(seanceSource.getHeureDebut() != null ? seanceSource.getHeureDebut().toString() : "08:00");
        TextField endField = new TextField(seanceSource.getHeureFin() != null ? seanceSource.getHeureFin().toString() : "10:00");
        TextField salleField = new TextField(seanceSource.getSalle());

        ComboBox<Classe> classeCombo = new ComboBox<>();
        classeCombo.getItems().addAll(SchoolService.getAllClasses());
        classeCombo.setValue(SchoolService.getAllClasses().stream().filter(c -> c.getIdClasse() == seanceSource.getIdClasse()).findFirst().orElse(null));

        ComboBox<Matiere> matiereCombo = new ComboBox<>();
        matiereCombo.getItems().addAll(SchoolService.getAllMatieres());
        matiereCombo.setValue(SchoolService.getAllMatieres().stream().filter(m -> m.getCode().equals(seanceSource.getCodeMatiere())).findFirst().orElse(null));

        ComboBox<Enseignant> enseignantCombo = new ComboBox<>();
        enseignantCombo.getItems().addAll(SchoolService.getAllEnseignants());
        enseignantCombo.setValue(SchoolService.getAllEnseignants().stream().filter(e -> e.getMatricule().equals(seanceSource.getMatriculeEnseignant())).findFirst().orElse(null));

        grid.add(new Label("Jour"), 0, 0); grid.add(jourCombo, 1, 0);
        grid.add(new Label("Heure debut"), 0, 1); grid.add(startField, 1, 1);
        grid.add(new Label("Heure fin"), 0, 2); grid.add(endField, 1, 2);
        grid.add(new Label("Salle"), 0, 3); grid.add(salleField, 1, 3);
        grid.add(new Label("Classe"), 0, 4); grid.add(classeCombo, 1, 4);
        grid.add(new Label("Matiere"), 0, 5); grid.add(matiereCombo, 1, 5);
        grid.add(new Label("Enseignant"), 0, 6); grid.add(enseignantCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Seance seance = new Seance();
                seance.setIdSeance(seanceSource.getIdSeance());
                seance.setJour(jourCombo.getValue());
                seance.setHeureDebut(LocalTime.parse(startField.getText().trim()));
                seance.setHeureFin(LocalTime.parse(endField.getText().trim()));
                seance.setSalle(salleField.getText());
                if (classeCombo.getValue() != null) {
                    seance.setIdClasse(classeCombo.getValue().getIdClasse());
                    seance.setClasseNom(classeCombo.getValue().getNomComplet());
                }
                if (matiereCombo.getValue() != null) {
                    seance.setCodeMatiere(matiereCombo.getValue().getCode());
                    seance.setMatiereLibelle(matiereCombo.getValue().getLibelle());
                }
                if (enseignantCombo.getValue() != null) {
                    seance.setMatriculeEnseignant(enseignantCombo.getValue().getMatricule());
                    seance.setEnseignantNom(enseignantCombo.getValue().getNomComplet());
                }
                return seance;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(seance -> {
            boolean success = creation ? SchoolService.saveSeance(seance) : SchoolService.updateSeance(seance);
            if (success) {
                AlertUtils.showInfo("Succes", creation ? "Seance ajoutee avec succes." : "Seance modifiee avec succes.");
                navigateToEmplois();
            } else {
                AlertUtils.showError("Erreur", creation ? "Impossible d'ajouter cette seance." : "Impossible de modifier cette seance.");
            }
        });
    }

    private void deleteSeance(Seance seance) {
        if (seance == null) {
            AlertUtils.showWarning("Selection requise", "Selectionnez une seance a supprimer.");
            return;
        }
        if (AlertUtils.showConfirmation("Confirmation", "Supprimer cette seance de " + seance.getJour() + " ?")) {
            if (SchoolService.deleteSeance(seance.getIdSeance())) {
                AlertUtils.showInfo("Succes", "Seance supprimee avec succes.");
                navigateToEmplois();
            } else {
                AlertUtils.showError("Erreur", "Impossible de supprimer cette seance.");
            }
        }
    }

    private Label createEmptyState(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("empty-state");
        return label;
    }

    private void printNode(Node node, String jobName) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            AlertUtils.showError("Impression", "Aucune imprimante n'est disponible.");
            return;
        }

        if (!job.showPrintDialog(SessionManager.getPrimaryStage())) {
            return;
        }

        job.getJobSettings().setJobName(jobName);
        PageLayout layout = job.getJobSettings().getPageLayout();
        double scaleX = layout.getPrintableWidth() / node.getBoundsInParent().getWidth();
        double scaleY = layout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        double scaleValue = Math.min(scaleX, scaleY);
        if (scaleValue > 1.0) {
            scaleValue = 1.0;
        }

        Scale scale = new Scale(scaleValue, scaleValue);
        node.getTransforms().add(scale);
        boolean success = job.printPage(node);
        node.getTransforms().remove(scale);

        if (success) {
            job.endJob();
            AlertUtils.showInfo("Impression", "Le bulletin a ete envoye a l'imprimante.");
        } else {
            AlertUtils.showError("Impression", "Echec de l'impression du bulletin.");
        }
    }

    private <T> TableColumn<T, Object> createColumn(String title, String property, double width) {
        TableColumn<T, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> {
            try {
                var method = data.getValue().getClass().getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
                Object val = method.invoke(data.getValue());
                return new SimpleObjectProperty<>(val != null ? val : "");
            } catch (Exception e) {
                return new SimpleObjectProperty<>("");
            }
        });
        col.setCellFactory(column -> new TableCell<>() {
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
        table.setPlaceholder(createEmptyState(emptyMessage));
        table.setFixedCellSize(42);
        table.setPrefHeight(prefHeight);
        table.setMinHeight(prefHeight);
    }

    private void showEditEleveDialog(Eleve eleve) {
        if (eleve == null) {
            AlertUtils.showWarning("Selection requise", "Selectionnez un eleve a modifier.");
            return;
        }
        showEleveDialog(eleve, false);
    }

    private void deleteEleve(Eleve eleve) {
        if (eleve == null) {
            AlertUtils.showWarning("Selection requise", "Selectionnez un eleve a supprimer.");
            return;
        }
        if (AlertUtils.showConfirmation("Confirmation", "Supprimer l'eleve " + eleve.getNomComplet() + " ?")) {
            if (SchoolService.deleteEleve(eleve.getMatricule())) {
                AlertUtils.showInfo("Succes", "Eleve supprime avec succes.");
                navigateToEleves();
            } else {
                AlertUtils.showError("Erreur", "Impossible de supprimer cet eleve. Il est peut-etre deja relie a des inscriptions.");
            }
        }
    }

    private void showEditEnseignantDialog(Enseignant enseignant) {
        if (enseignant == null) {
            AlertUtils.showWarning("Selection requise", "Selectionnez un enseignant a modifier.");
            return;
        }
        showEnseignantDialog(enseignant, false);
    }

    private void deleteEnseignant(Enseignant enseignant) {
        if (enseignant == null) {
            AlertUtils.showWarning("Selection requise", "Selectionnez un enseignant a supprimer.");
            return;
        }
        if (AlertUtils.showConfirmation("Confirmation", "Supprimer l'enseignant " + enseignant.getNomComplet() + " ?")) {
            if (SchoolService.deleteEnseignant(enseignant.getMatricule())) {
                AlertUtils.showInfo("Succes", "Enseignant supprime avec succes.");
                navigateToEnseignants();
            } else {
                AlertUtils.showError("Erreur", "Impossible de supprimer cet enseignant. Il est peut-etre relie a des classes ou des notes.");
            }
        }
    }

    private void showEditClasseDialog(Classe classe) {
        if (classe == null) {
            AlertUtils.showWarning("Selection requise", "Selectionnez une classe a modifier.");
            return;
        }
        showClasseDialog(classe, false);
    }

    private void deleteClasse(Classe classe) {
        if (classe == null) {
            AlertUtils.showWarning("Selection requise", "Selectionnez une classe a supprimer.");
            return;
        }
        if (AlertUtils.showConfirmation("Confirmation", "Supprimer la classe " + classe.getNomComplet() + " ?")) {
            if (SchoolService.deleteClasse(classe.getIdClasse())) {
                AlertUtils.showInfo("Succes", "Classe supprimee avec succes.");
                navigateToClasses();
            } else {
                AlertUtils.showError("Erreur", "Impossible de supprimer cette classe. Elle est probablement reliee a des inscriptions ou affectations.");
            }
        }
    }

    private void showEditMatiereDialog(Matiere matiere) {
        if (matiere == null) {
            AlertUtils.showWarning("Selection requise", "Selectionnez une matiere a modifier.");
            return;
        }
        showMatiereDialog(matiere, false);
    }

    private void deleteMatiere(Matiere matiere) {
        if (matiere == null) {
            AlertUtils.showWarning("Selection requise", "Selectionnez une matiere a supprimer.");
            return;
        }
        if (AlertUtils.showConfirmation("Confirmation", "Supprimer la matiere " + matiere.getLibelle() + " ?")) {
            if (SchoolService.deleteMatiere(matiere.getCode())) {
                AlertUtils.showInfo("Succes", "Matiere supprimee avec succes.");
                navigateToMatieres();
            } else {
                AlertUtils.showError("Erreur", "Impossible de supprimer cette matiere. Elle est probablement utilisee dans les notes ou les affectations.");
            }
        }
    }

    private void showAddEleveDialog() {
        showEleveDialog(new Eleve(), true);
    }

    private void showEleveDialog(Eleve source, boolean creation) {
        Dialog<Eleve> dialog = new Dialog<>();
        dialog.setTitle(creation ? "Nouvel eleve" : "Modifier eleve");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        String originalMatricule = source.getMatricule();
        TextField matricule = new TextField(source.getMatricule());
        TextField nom = new TextField(source.getNom());
        TextField prenom = new TextField(source.getPrenom());
        TextField telephone = new TextField(source.getTelephone());
        TextField adresse = new TextField(source.getAdresse());
        DatePicker dob = new DatePicker(source.getDateNaissance());
        ComboBox<String> sexeCombo = new ComboBox<>();
        sexeCombo.getItems().addAll("M", "F");
        sexeCombo.setValue(source.getSexe());
        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());
        niveauCombo.setValue(source.getNiveau());

        grid.add(new Label("Matricule"), 0, 0); grid.add(matricule, 1, 0);
        grid.add(new Label("Nom"), 0, 1); grid.add(nom, 1, 1);
        grid.add(new Label("Prenom"), 0, 2); grid.add(prenom, 1, 2);
        grid.add(new Label("Date naissance"), 0, 3); grid.add(dob, 1, 3);
        grid.add(new Label("Sexe"), 0, 4); grid.add(sexeCombo, 1, 4);
        grid.add(new Label("Telephone"), 0, 5); grid.add(telephone, 1, 5);
        grid.add(new Label("Adresse"), 0, 6); grid.add(adresse, 1, 6);
        grid.add(new Label("Niveau"), 0, 7); grid.add(niveauCombo, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Eleve e = new Eleve();
                e.setMatricule(matricule.getText());
                e.setNom(nom.getText());
                e.setPrenom(prenom.getText());
                e.setDateNaissance(dob.getValue() != null ? dob.getValue() : LocalDate.now());
                e.setSexe(sexeCombo.getValue());
                e.setTelephone(telephone.getText());
                e.setAdresse(adresse.getText());
                e.setNiveau(niveauCombo.getValue());
                return e;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(eleve -> {
            boolean success = creation
                ? SchoolService.saveEleve(eleve)
                : SchoolService.updateEleve(eleve, originalMatricule);
            if (success) {
                AlertUtils.showInfo("Succes", creation ? "Eleve ajoute avec succes." : "Eleve modifie avec succes.");
                navigateToEleves();
            } else {
                AlertUtils.showError("Erreur", creation ? "Impossible d'ajouter cet eleve." : "Impossible de modifier cet eleve.");
            }
        });
    }

    private void showAddEnseignantDialog() {
        showEnseignantDialog(new Enseignant(), true);
    }

    private void showEnseignantDialog(Enseignant source, boolean creation) {
        Dialog<Enseignant> dialog = new Dialog<>();
        dialog.setTitle(creation ? "Nouvel enseignant" : "Modifier enseignant");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        String originalMatricule = source.getMatricule();
        TextField matricule = new TextField(source.getMatricule());
        TextField nom = new TextField(source.getNom());
        TextField prenom = new TextField(source.getPrenom());
        TextField telephone = new TextField(source.getTelephone());
        TextField adresse = new TextField(source.getAdresse());
        TextField grade = new TextField(source.getGrade());
        DatePicker dob = new DatePicker(source.getDateNaissance());
        ComboBox<String> sexeCombo = new ComboBox<>();
        sexeCombo.getItems().addAll("M", "F");
        sexeCombo.setValue(source.getSexe());

        grid.add(new Label("Matricule"), 0, 0); grid.add(matricule, 1, 0);
        grid.add(new Label("Nom"), 0, 1); grid.add(nom, 1, 1);
        grid.add(new Label("Prenom"), 0, 2); grid.add(prenom, 1, 2);
        grid.add(new Label("Date naissance"), 0, 3); grid.add(dob, 1, 3);
        grid.add(new Label("Sexe"), 0, 4); grid.add(sexeCombo, 1, 4);
        grid.add(new Label("Telephone"), 0, 5); grid.add(telephone, 1, 5);
        grid.add(new Label("Adresse"), 0, 6); grid.add(adresse, 1, 6);
        grid.add(new Label("Grade"), 0, 7); grid.add(grade, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Enseignant en = new Enseignant();
                en.setMatricule(matricule.getText());
                en.setNom(nom.getText());
                en.setPrenom(prenom.getText());
                en.setDateNaissance(dob.getValue() != null ? dob.getValue() : LocalDate.now());
                en.setSexe(sexeCombo.getValue());
                en.setTelephone(telephone.getText());
                en.setAdresse(adresse.getText());
                en.setGrade(grade.getText());
                return en;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(enseignant -> {
            boolean success = creation
                ? SchoolService.saveEnseignant(enseignant)
                : SchoolService.updateEnseignant(enseignant, originalMatricule);
            if (success) {
                AlertUtils.showInfo("Succes", creation ? "Enseignant ajoute avec succes." : "Enseignant modifie avec succes.");
                navigateToEnseignants();
            } else {
                AlertUtils.showError("Erreur", creation ? "Impossible d'ajouter cet enseignant." : "Impossible de modifier cet enseignant.");
            }
        });
    }

    private void showAddClasseDialog(String niveauDefaut) {
        Classe classe = new Classe();
        classe.setNiveau(niveauDefaut);
        showClasseDialog(classe, true);
    }

    private void showClasseDialog(Classe source, boolean creation) {
        Dialog<Classe> dialog = new Dialog<>();
        dialog.setTitle(creation ? "Nouvelle classe" : "Modifier classe");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField nom = new TextField(source.getNom());
        TextField capacite = new TextField(source.getCapacite() > 0 ? String.valueOf(source.getCapacite()) : "20");
        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.getItems().addAll(SchoolService.getAllNiveaux());
        niveauCombo.setValue(source.getNiveau());

        grid.add(new Label("Nom"), 0, 0); grid.add(nom, 1, 0);
        grid.add(new Label("Niveau"), 0, 1); grid.add(niveauCombo, 1, 1);
        grid.add(new Label("Capacite"), 0, 2); grid.add(capacite, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Classe classe = new Classe();
                classe.setIdClasse(source.getIdClasse());
                classe.setNom(nom.getText());
                classe.setNiveau(niveauCombo.getValue());
                classe.setCapacite(Integer.parseInt(capacite.getText()));
                classe.setNomComplet((niveauCombo.getValue() != null ? niveauCombo.getValue() : "") + " - " + nom.getText());
                return classe;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(classe -> {
            boolean success = creation ? SchoolService.saveClasse(classe) : SchoolService.updateClasse(classe);
            if (success) {
                AlertUtils.showInfo("Succes", creation ? "Classe ajoutee avec succes." : "Classe modifiee avec succes.");
                navigateToClasses();
            } else {
                AlertUtils.showError("Erreur", creation ? "Impossible d'ajouter cette classe." : "Impossible de modifier cette classe.");
            }
        });
    }

    private void showAddNiveauDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Nouveau niveau");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField libelle = new TextField();
        TextField libelleCourt = new TextField();

        grid.add(new Label("Libelle"), 0, 0); grid.add(libelle, 1, 0);
        grid.add(new Label("Abreviation"), 0, 1); grid.add(libelleCourt, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> bt == ButtonType.OK ? new String[]{libelle.getText(), libelleCourt.getText()} : null);

        dialog.showAndWait().ifPresent(values -> {
            if (SchoolService.saveNiveau(values[0], values[1])) {
                AlertUtils.showInfo("Succes", "Niveau ajoute avec succes.");
                navigateToClasses();
            } else {
                AlertUtils.showError("Erreur", "Impossible d'ajouter ce niveau.");
            }
        });
    }

    private void showAddMatiereDialog() {
        showMatiereDialog(new Matiere(), true);
    }

    private void showMatiereDialog(Matiere source, boolean creation) {
        Dialog<Matiere> dialog = new Dialog<>();
        dialog.setTitle(creation ? "Nouvelle matiere" : "Modifier matiere");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        String originalCode = source.getCode();
        TextField code = new TextField(source.getCode());
        TextField libelle = new TextField(source.getLibelle());
        TextField coefficient = new TextField(source.getCoefficient() > 0 ? String.valueOf(source.getCoefficient()) : "1.0");

        grid.add(new Label("Code"), 0, 0); grid.add(code, 1, 0);
        grid.add(new Label("Libelle"), 0, 1); grid.add(libelle, 1, 1);
        grid.add(new Label("Coefficient"), 0, 2); grid.add(coefficient, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                Matiere matiere = new Matiere();
                matiere.setCode(code.getText().toUpperCase(Locale.ROOT));
                matiere.setLibelle(libelle.getText());
                matiere.setCoefficient(Double.parseDouble(coefficient.getText()));
                return matiere;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(matiere -> {
            boolean success = creation
                ? SchoolService.saveMatiere(matiere)
                : SchoolService.updateMatiere(matiere, originalCode);
            if (success) {
                AlertUtils.showInfo("Succes", creation ? "Matiere ajoutee avec succes." : "Matiere modifiee avec succes.");
                navigateToMatieres();
            } else {
                AlertUtils.showError("Erreur", creation ? "Impossible d'ajouter cette matiere." : "Impossible de modifier cette matiere.");
            }
        });
    }

    public static class NoteEntry {
        private final SimpleStringProperty nom = new SimpleStringProperty("");
        private final javafx.beans.property.SimpleDoubleProperty devoir = new javafx.beans.property.SimpleDoubleProperty(0.0);
        private final javafx.beans.property.SimpleDoubleProperty exam = new javafx.beans.property.SimpleDoubleProperty(0.0);
        private final javafx.beans.property.SimpleDoubleProperty composition = new javafx.beans.property.SimpleDoubleProperty(0.0);
        private String matricule;

        public NoteEntry(String nom) {
            this.nom.set(nom);
        }

        public String getMatricule() {
            return matricule;
        }

        public void setMatricule(String matricule) {
            this.matricule = matricule;
        }

        public String getNom() {
            return nom.get();
        }

        public SimpleStringProperty nomProperty() {
            return nom;
        }

        public Double getDevoir() {
            return devoir.get();
        }

        public void setDevoir(Double value) {
            devoir.set(value == null ? 0.0 : value);
        }

        public javafx.beans.property.SimpleDoubleProperty devoirProperty() {
            return devoir;
        }

        public Double getExam() {
            return exam.get();
        }

        public void setExam(Double value) {
            exam.set(value == null ? 0.0 : value);
        }

        public javafx.beans.property.SimpleDoubleProperty examProperty() {
            return exam;
        }

        public Double getComposition() {
            return composition.get();
        }

        public void setComposition(Double value) {
            composition.set(value == null ? 0.0 : value);
        }

        public javafx.beans.property.SimpleDoubleProperty compositionProperty() {
            return composition;
        }
    }
}
