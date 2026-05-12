package com.school.gestion.model;

import java.time.LocalDate;

public class AnneeScolaire {
    private int idAnnee;
    private String annee;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean estActive;

    public AnneeScolaire() {}

    public AnneeScolaire(String annee, LocalDate dateDebut, LocalDate dateFin) {
        this.annee = annee;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public int getIdAnnee() { return idAnnee; }
    public void setIdAnnee(int idAnnee) { this.idAnnee = idAnnee; }
    public String getAnnee() { return annee; }
    public void setAnnee(String annee) { this.annee = annee; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public boolean isEstActive() { return estActive; }
    public void setEstActive(boolean estActive) { this.estActive = estActive; }

    @Override
    public String toString() { return annee; }
}
