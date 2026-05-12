package com.school.gestion.service;

import com.school.gestion.model.AnneeScolaire;
import com.school.gestion.model.Classe;
import com.school.gestion.model.Matiere;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardAssistantService {

    private DashboardAssistantService() {
    }

    public static List<String> getQuickActions(boolean admin) {
        List<String> actions = new ArrayList<>();
        if (admin) {
            actions.add("Vue globale");
            actions.add("Classes chargees");
            actions.add("Comment permuter");
            actions.add("Eleves sans classe");
            actions.add("Saisie des notes");
        } else {
            actions.add("Mes classes");
            actions.add("Mes matieres");
            actions.add("Mes eleves");
            actions.add("Comment saisir les notes");
            actions.add("Resume du jour");
        }
        return actions;
    }

    public static String answerForAdmin(String message) {
        String normalized = normalize(message);
        AnneeScolaire activeYear = SchoolService.getActiveAnneeScolaire();

        if (normalized.isBlank() || containsAny(normalized, "bonjour", "salut", "hello", "help", "aide")) {
            return "Bonjour. Je peux vous aider a piloter les classes, expliquer la permutation, "
                + "signaler les classes sous tension et resumer l'etat global de l'etablissement.";
        }

        if (containsAny(normalized, "vue globale", "global", "dashboard", "resume")) {
            return "Vous avez actuellement " + SchoolService.getAllEleves().size() + " eleves, "
                + SchoolService.getAllClasses().size() + " classes, "
                + SchoolService.getAllEnseignants().size() + " enseignants et "
                + SchoolService.getAllMatieres().size() + " matieres actives.";
        }

        if (containsAny(normalized, "annee", "active")) {
            return activeYear == null
                ? "Aucune annee scolaire active n'est configuree."
                : "L'annee scolaire active est " + activeYear.getAnnee() + ".";
        }

        if (containsAny(normalized, "classe", "chargee", "pleine", "capacite")) {
            List<Classe> classes = SchoolService.getAllClasses();
            List<String> alerts = new ArrayList<>();
            for (Classe classe : classes) {
                if (classe.getRemplissage() >= 0.85) {
                    alerts.add(classe.getNomComplet() + " (" + classe.getEffectifActuel() + "/" + classe.getCapacite() + ")");
                }
            }
            if (alerts.isEmpty()) {
                return "Aucune classe n'est au-dessus de 85 % de capacite.";
            }
            return "Classes a surveiller : " + String.join(", ", alerts) + ".";
        }

        if (containsAny(normalized, "permut", "transfer", "deplacer")) {
            return "Pour permuter, ouvrez Inscriptions et permutations, choisissez un niveau, une classe source "
                + "et une classe cible, puis selectionnez un eleve dans chaque liste. Pour un simple transfert, "
                + "selectionnez seulement l'eleve source puis cliquez sur Transferer.";
        }

        if (containsAny(normalized, "sans classe", "non inscrit", "non affecte")) {
            if (activeYear == null) {
                return "Activez d'abord une annee scolaire pour suivre les eleves non affectes.";
            }
            int total = 0;
            for (String niveau : SchoolService.getAllNiveaux()) {
                total += SchoolService.getElevesNonInscrits(activeYear.getIdAnnee(), niveau).size();
            }
            return "Il y a actuellement " + total + " eleve(s) non affecte(s) sur l'annee active.";
        }

        if (containsAny(normalized, "note", "notes", "evaluation")) {
            return "La saisie des notes se fait par classe, matiere et trimestre. Le module bulletins vous permet ensuite "
                + "de visualiser les moyennes consolidees par eleve.";
        }

        return "Je peux vous aider sur le pilotage global, les classes chargees, les permutations, "
            + "les eleves non affectes et la saisie des notes.";
    }

    public static String answerForTeacher(String message, String teacherMatricule) {
        String normalized = normalize(message);
        AnneeScolaire activeYear = SchoolService.getActiveAnneeScolaire();

        if (normalized.isBlank() || containsAny(normalized, "bonjour", "salut", "hello", "help", "aide")) {
            return "Bonjour. Je peux resumer vos classes, vos matieres, vos eleves actifs et vous guider pour la saisie des notes.";
        }

        if (containsAny(normalized, "resume", "jour", "dashboard", "vue")) {
            int classCount = SchoolService.getTeacherClassCount(teacherMatricule);
            int matiereCount = SchoolService.getTeacherMatiereCount(teacherMatricule);
            int studentCount = activeYear == null ? 0 : SchoolService.getTeacherStudentCount(teacherMatricule, activeYear.getIdAnnee());
            return "Vous suivez " + classCount + " classe(s), " + matiereCount + " matiere(s) et "
                + studentCount + " eleve(s) actif(s).";
        }

        if (containsAny(normalized, "classe", "mes classes")) {
            List<Classe> classes = SchoolService.getTeacherClasses(teacherMatricule);
            if (classes.isEmpty()) {
                return "Aucune classe n'est encore rattachee a ce profil.";
            }
            List<String> names = new ArrayList<>();
            for (Classe classe : classes) {
                names.add(classe.getNomComplet());
            }
            return "Vos classes : " + String.join(", ", names) + ".";
        }

        if (containsAny(normalized, "matiere", "mes matieres")) {
            List<Matiere> matieres = SchoolService.getTeacherMatieres(teacherMatricule);
            if (matieres.isEmpty()) {
                return "Aucune matiere n'est encore reliee a ce profil.";
            }
            List<String> names = new ArrayList<>();
            for (Matiere matiere : matieres) {
                names.add(matiere.getLibelle());
            }
            return "Vos matieres : " + String.join(", ", names) + ".";
        }

        if (containsAny(normalized, "eleve", "mes eleves", "effectif")) {
            int total = activeYear == null ? 0 : SchoolService.getTeacherStudentCount(teacherMatricule, activeYear.getIdAnnee());
            return "Vous avez actuellement " + total + " eleve(s) actif(s) sur les classes qui vous sont affectees.";
        }

        if (containsAny(normalized, "note", "notes", "saisir")) {
            return "Pour saisir les notes, choisissez le trimestre, la classe et la matiere, chargez la grille, "
                + "renseignez Devoir, Examen et Composition, puis cliquez sur Enregistrer les notes.";
        }

        return "Je peux vous aider sur vos classes, vos matieres, votre effectif et la procedure de saisie des notes.";
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }

    private static boolean containsAny(String text, String... tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }
}
