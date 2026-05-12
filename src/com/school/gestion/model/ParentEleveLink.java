package com.school.gestion.model;

import java.time.LocalDateTime;

public class ParentEleveLink {
    private int id;
    private int parentId;
    private String parentNomComplet;
    private String parentEmail;
    private String matriculeEleve;
    private String eleveNomComplet;
    private String lienParente;
    private boolean valideParAdmin;
    private LocalDateTime dateLiaison;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
    public String getParentNomComplet() { return parentNomComplet; }
    public void setParentNomComplet(String parentNomComplet) { this.parentNomComplet = parentNomComplet; }
    public String getParentEmail() { return parentEmail; }
    public void setParentEmail(String parentEmail) { this.parentEmail = parentEmail; }
    public String getMatriculeEleve() { return matriculeEleve; }
    public void setMatriculeEleve(String matriculeEleve) { this.matriculeEleve = matriculeEleve; }
    public String getEleveNomComplet() { return eleveNomComplet; }
    public void setEleveNomComplet(String eleveNomComplet) { this.eleveNomComplet = eleveNomComplet; }
    public String getLienParente() { return lienParente; }
    public void setLienParente(String lienParente) { this.lienParente = lienParente; }
    public boolean isValideParAdmin() { return valideParAdmin; }
    public void setValideParAdmin(boolean valideParAdmin) { this.valideParAdmin = valideParAdmin; }
    public LocalDateTime getDateLiaison() { return dateLiaison; }
    public void setDateLiaison(LocalDateTime dateLiaison) { this.dateLiaison = dateLiaison; }

    public String getStatutLabel() {
        return valideParAdmin ? "Validee" : "En attente";
    }
}
