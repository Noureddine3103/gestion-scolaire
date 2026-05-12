package com.school.gestion.model;

public class AffectationEnseignement {
    private int id;
    private String matriculeEnseignant;
    private String enseignantNomComplet;
    private int idClasse;
    private String classeNom;
    private String codeMatiere;
    private String matiereLibelle;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMatriculeEnseignant() { return matriculeEnseignant; }
    public void setMatriculeEnseignant(String matriculeEnseignant) { this.matriculeEnseignant = matriculeEnseignant; }
    public String getEnseignantNomComplet() { return enseignantNomComplet; }
    public void setEnseignantNomComplet(String enseignantNomComplet) { this.enseignantNomComplet = enseignantNomComplet; }
    public int getIdClasse() { return idClasse; }
    public void setIdClasse(int idClasse) { this.idClasse = idClasse; }
    public String getClasseNom() { return classeNom; }
    public void setClasseNom(String classeNom) { this.classeNom = classeNom; }
    public String getCodeMatiere() { return codeMatiere; }
    public void setCodeMatiere(String codeMatiere) { this.codeMatiere = codeMatiere; }
    public String getMatiereLibelle() { return matiereLibelle; }
    public void setMatiereLibelle(String matiereLibelle) { this.matiereLibelle = matiereLibelle; }

    @Override
    public String toString() {
        return (enseignantNomComplet == null ? matriculeEnseignant : enseignantNomComplet)
            + " - "
            + (classeNom == null ? idClasse : classeNom)
            + " - "
            + (matiereLibelle == null ? codeMatiere : matiereLibelle);
    }
}
