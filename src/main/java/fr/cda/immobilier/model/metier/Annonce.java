package fr.cda.immobilier.model.metier;

public class Annonce {
    private int id;
    private String titre;
    private String prix;
    private String surface;
    private String description;
    private String image;
    private String lien;
    private String adresse;

    /**
     * Constructeur d'annonces
     * @param titre
     * @param prix
     * @param surface
     * @param description
     * @param lieuBien
     * @param lien
     */
    public Annonce(String titre, String prix, String surface, String description, String lieuBien, String lien) {
        this.titre = titre;
        this.prix = prix;
        this.surface = surface;
        this.description = description;
        this.adresse = lieuBien;
        this.lien = lien;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "Annonce{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", prix=" + prix +
                ", surface='" + surface + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", lien='" + lien + '\'' +
                '}';
    }
}