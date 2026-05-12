package com.school.gestion.model;

import java.time.LocalDateTime;

public class PresenceRecord {
    private int id;
    private int idSeance;
    private String matriculeEleve;
    private String eleveNomComplet;
    private String statut;
    private String remarque;
    private String justificationParent;
    private LocalDateTime dateSaisie;
    private String jourSeance;
    private String horaireSeance;
    private String matiereLibelle;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdSeance() { return idSeance; }
    public void setIdSeance(int idSeance) { this.idSeance = idSeance; }
    public String getMatriculeEleve() { return matriculeEleve; }
    public void setMatriculeEleve(String matriculeEleve) { this.matriculeEleve = matriculeEleve; }
    public String getEleveNomComplet() { return eleveNomComplet; }
    public void setEleveNomComplet(String eleveNomComplet) { this.eleveNomComplet = eleveNomComplet; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }
    public String getJustificationParent() { return justificationParent; }
    public void setJustificationParent(String justificationParent) { this.justificationParent = justificationParent; }
    public LocalDateTime getDateSaisie() { return dateSaisie; }
    public void setDateSaisie(LocalDateTime dateSaisie) { this.dateSaisie = dateSaisie; }
    public String getJourSeance() { return jourSeance; }
    public void setJourSeance(String jourSeance) { this.jourSeance = jourSeance; }
    public String getHoraireSeance() { return horaireSeance; }
    public void setHoraireSeance(String horaireSeance) { this.horaireSeance = horaireSeance; }
    public String getMatiereLibelle() { return matiereLibelle; }
    public void setMatiereLibelle(String matiereLibelle) { this.matiereLibelle = matiereLibelle; }
}
