package fr.cda.immobilier.model.metier;

public class Lieu {
    private int id;
    private String libelle;

    public Lieu(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return "Type{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
