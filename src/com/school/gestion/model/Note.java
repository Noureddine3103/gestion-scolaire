package com.school.gestion.model;

public class Note {
    private int idNote;
    private String matricule;
    private int idAnnee;
    private int idClasse;
    private String codeMatiere;
    private int trimestre;
    private Double noteDevoir;
    private Double noteExamens;
    private Double noteComposition;
    private java.time.LocalDate dateSaisie;
    private String matriculeEnseignant;

    public Note() {}

    public Note(String matricule, int idAnnee, int idClasse, String codeMatiere, int trimestre) {
        this.matricule = matricule;
        this.idAnnee = idAnnee;
        this.idClasse = idClasse;
        this.codeMatiere = codeMatiere;
        this.trimestre = trimestre;
    }

    public int getIdNote() { return idNote; }
    public void setIdNote(int idNote) { this.idNote = idNote; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public int getIdAnnee() { return idAnnee; }
    public void setIdAnnee(int idAnnee) { this.idAnnee = idAnnee; }
    public int getIdClasse() { return idClasse; }
    public void setIdClasse(int idClasse) { this.idClasse = idClasse; }
    public String getCodeMatiere() { return codeMatiere; }
    public void setCodeMatiere(String codeMatiere) { this.codeMatiere = codeMatiere; }
    public int getTrimestre() { return trimestre; }
    public void setTrimestre(int trimestre) { this.trimestre = trimestre; }
    public Double getNoteDevoir() { return noteDevoir; }
    public void setNoteDevoir(Double noteDevoir) { this.noteDevoir = noteDevoir; }
    public Double getNoteExamens() { return noteExamens; }
    public void setNoteExamens(Double noteExamens) { this.noteExamens = noteExamens; }
    public Double getNoteComposition() { return noteComposition; }
    public void setNoteComposition(Double noteComposition) { this.noteComposition = noteComposition; }
    public java.time.LocalDate getDateSaisie() { return dateSaisie; }
    public void setDateSaisie(java.time.LocalDate dateSaisie) { this.dateSaisie = dateSaisie; }
    public String getMatriculeEnseignant() { return matriculeEnseignant; }
    public void setMatriculeEnseignant(String matriculeEnseignant) { this.matriculeEnseignant = matriculeEnseignant; }

    public double getMoyenne() {
        int count = 0;
        double sum = 0;
        if (noteDevoir != null) { sum += noteDevoir; count++; }
        if (noteExamens != null) { sum += noteExamens; count++; }
        if (noteComposition != null) { sum += noteComposition; count++; }
        return count > 0 ? sum / count : 0;
    }
}
