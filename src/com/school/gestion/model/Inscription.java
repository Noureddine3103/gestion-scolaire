package com.school.gestion.model;

public class Inscription {
    private int idInscription;
    private String matricule;
    private int idAnnee;
    private int idClasse;
    private java.time.LocalDate dateInscription;
    private String statut;

    private Eleve eleve;
    private Classe classe;

    public Inscription() {}

    public Inscription(String matricule, int idAnnee, int idClasse) {
        this.matricule = matricule;
        this.idAnnee = idAnnee;
        this.idClasse = idClasse;
        this.statut = "ACTIF";
    }

    public int getIdInscription() { return idInscription; }
    public void setIdInscription(int idInscription) { this.idInscription = idInscription; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public int getIdAnnee() { return idAnnee; }
    public void setIdAnnee(int idAnnee) { this.idAnnee = idAnnee; }
    public int getIdClasse() { return idClasse; }
    public void setIdClasse(int idClasse) { this.idClasse = idClasse; }
    public java.time.LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(java.time.LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Eleve getEleve() { return eleve; }
    public void setEleve(Eleve eleve) { this.eleve = eleve; }
    public Classe getClasse() { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }
}
