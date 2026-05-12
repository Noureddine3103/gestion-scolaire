package com.school.gestion.model;

import java.time.LocalDateTime;

public class InvitationCode {
    private int id;
    private String code;
    private String matriculeEleve;
    private String eleveNomComplet;
    private String lienParente;
    private boolean utilise;
    private Integer utilisePar;
    private LocalDateTime dateCreation;
    private LocalDateTime dateExpiration;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMatriculeEleve() { return matriculeEleve; }
    public void setMatriculeEleve(String matriculeEleve) { this.matriculeEleve = matriculeEleve; }

    public String getEleveNomComplet() { return eleveNomComplet; }
    public void setEleveNomComplet(String eleveNomComplet) { this.eleveNomComplet = eleveNomComplet; }

    public String getLienParente() { return lienParente; }
    public void setLienParente(String lienParente) { this.lienParente = lienParente; }

    public boolean isUtilise() { return utilise; }
    public void setUtilise(boolean utilise) { this.utilise = utilise; }

    public Integer getUtilisePar() { return utilisePar; }
    public void setUtilisePar(Integer utilisePar) { this.utilisePar = utilisePar; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDateTime dateExpiration) { this.dateExpiration = dateExpiration; }

    public String getStatutLabel() {
        if (utilise) {
            return "Utilise";
        }
        if (dateExpiration != null && dateExpiration.isBefore(LocalDateTime.now())) {
            return "Expire";
        }
        return "Disponible";
    }
}
