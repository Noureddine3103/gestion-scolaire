package com.school.gestion.model;

import java.time.LocalDate;

public class Enseignant {
    private String matricule;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String sexe;
    private String adresse;
    private String telephone;
    private byte[] photo;
    private String grade;
    private Integer idUtilisateur;

    public Enseignant() {}

    public Enseignant(String matricule, String nom, String prenom, String grade) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.grade = grade;
    }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getNomComplet() {
        String full = ((prenom == null ? "" : prenom) + " " + (nom == null ? "" : nom)).trim();
        return full.isBlank() ? matricule : full;
    }

    @Override
    public String toString() {
        return getNomComplet();
    }
}
