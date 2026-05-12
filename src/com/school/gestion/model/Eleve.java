package com.school.gestion.model;

import java.time.LocalDate;

public class Eleve {
    private String matricule;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String sexe;
    private String adresse;
    private String telephone;
    private byte[] photo;
    private String niveau;
    private Integer idUtilisateur;
    private LocalDate dateInscription;
    private String email;
    private String parentNom;
    private String parentPrenom;
    private String parentEmail;
    private String parentTelephone;
    private String parentAdresse;

    public Eleve() {}

    public Eleve(String matricule, String nom, String prenom, LocalDate dateNaissance,
                 String sexe, String niveau) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
        this.niveau = niveau;
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
    public String getNiveau() { return cleanLabel(niveau); }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public Integer getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Integer idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getParentNom() { return parentNom; }
    public void setParentNom(String parentNom) { this.parentNom = parentNom; }
    public String getParentPrenom() { return parentPrenom; }
    public void setParentPrenom(String parentPrenom) { this.parentPrenom = parentPrenom; }
    public String getParentEmail() { return parentEmail; }
    public void setParentEmail(String parentEmail) { this.parentEmail = parentEmail; }
    public String getParentTelephone() { return parentTelephone; }
    public void setParentTelephone(String parentTelephone) { this.parentTelephone = parentTelephone; }
    public String getParentAdresse() { return parentAdresse; }
    public void setParentAdresse(String parentAdresse) { this.parentAdresse = parentAdresse; }

    public String getNomComplet() {
        String full = ((prenom == null ? "" : prenom) + " " + (nom == null ? "" : nom)).trim();
        return full.isBlank() ? matricule : full;
    }
    public String getParentNomComplet() {
        String full = ((parentPrenom == null ? "" : parentPrenom) + " " + (parentNom == null ? "" : parentNom)).trim();
        return full.isBlank() ? "-" : full;
    }

    @Override
    public String toString() {
        return getNomComplet();
    }

    private String cleanLabel(String value) {
        if (value == null) {
            return null;
        }
        return value
            .replace("1??re Ann??e", "1ere Annee")
            .replace("2??me Ann??e", "2eme Annee")
            .replace("3??me Ann??e", "3eme Annee")
            .replace("1Ã¨re AnnÃ©e", "1ere Annee")
            .replace("2Ã¨me AnnÃ©e", "2eme Annee")
            .replace("3Ã¨me AnnÃ©e", "3eme Annee")
            .replace("1ÃƒÂ¨re AnnÃƒÂ©e", "1ere Annee")
            .replace("2ÃƒÂ¨me AnnÃƒÂ©e", "2eme Annee")
            .replace("3ÃƒÂ¨me AnnÃƒÂ©e", "3eme Annee")
            .replace("Ann??e", "Annee")
            .replace("AnnÃ©e", "Annee")
            .replace("Ã¨", "e")
            .replace("Ã©", "e");
    }
}
