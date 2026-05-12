package com.school.gestion.model;

import java.time.LocalDateTime;

public class MessageInterne {
    private int id;
    private String expediteurRole;
    private int expediteurId;
    private String expediteurNom;
    private String destinataireRole;
    private Integer destinataireId;
    private String matriculeEleve;
    private String sujet;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private boolean lu;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getExpediteurRole() { return expediteurRole; }
    public void setExpediteurRole(String expediteurRole) { this.expediteurRole = expediteurRole; }
    public int getExpediteurId() { return expediteurId; }
    public void setExpediteurId(int expediteurId) { this.expediteurId = expediteurId; }
    public String getExpediteurNom() { return expediteurNom; }
    public void setExpediteurNom(String expediteurNom) { this.expediteurNom = expediteurNom; }
    public String getDestinataireRole() { return destinataireRole; }
    public void setDestinataireRole(String destinataireRole) { this.destinataireRole = destinataireRole; }
    public Integer getDestinataireId() { return destinataireId; }
    public void setDestinataireId(Integer destinataireId) { this.destinataireId = destinataireId; }
    public String getMatriculeEleve() { return matriculeEleve; }
    public void setMatriculeEleve(String matriculeEleve) { this.matriculeEleve = matriculeEleve; }
    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }
}
