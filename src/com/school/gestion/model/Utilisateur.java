package com.school.gestion.model;

public class Utilisateur {
    private int id;
    private String username;
    private String password;
    private String role;
    private int idPersonne;
    private boolean estActif;

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_ENSEIGNANT = "ENSEIGNANT";
    public static final String ROLE_PARENT = "PARENT";
    public static final String ROLE_ELEVE = "ELEVE";

    public Utilisateur() {}

    public Utilisateur(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.estActif = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getIdPersonne() { return idPersonne; }
    public void setIdPersonne(int idPersonne) { this.idPersonne = idPersonne; }
    public boolean getEstActif() { return estActif; }
    public boolean isEstActif() { return estActif; }
    public void setEstActif(boolean estActif) { this.estActif = estActif; }

    public boolean isAdmin() { return ROLE_ADMIN.equals(role); }
    public boolean isEnseignant() { return ROLE_ENSEIGNANT.equals(role); }
    public boolean isParent() { return ROLE_PARENT.equals(role); }
    public boolean isEleve() { return ROLE_ELEVE.equals(role); }
}
