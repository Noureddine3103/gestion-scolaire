package com.school.gestion.model;

public class Matiere {
    private String code;
    private String libelle;
    private double coefficient;

    public Matiere() {}

    public Matiere(String code, String libelle, double coefficient) {
        this.code = code;
        this.libelle = libelle;
        this.coefficient = coefficient;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
public double getCoefficient() { return coefficient; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }

    @Override
    public String toString() { return libelle + " (" + code + ")"; }
}
