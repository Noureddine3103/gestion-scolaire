package com.school.gestion.model;

public class Parent {
    private int idParent;
    private Integer idUtilisateur;
    private String nom;
    private String prenom;
    private String telephone;
    private String profession;
    private String adresse;
    private String email;

    public int getIdParent() { return idParent; }
    public void setIdParent(int idParent) { this.idParent = idParent; }
    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNomComplet() {
        return ((prenom == null ? "" : prenom) + " " + (nom == null ? "" : nom)).trim();
    }
}
