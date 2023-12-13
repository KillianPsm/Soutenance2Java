package fr.cda.immobilier.scraping;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import fr.cda.immobilier.model.metier.Annonce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Définition de la classe Scraping
public class Scraping {

    // WebClient est utilisé pour effectuer les requêtes web
    private WebClient webClient;

    // Constructeur de la classe
    public Scraping() {
        // Initialisation du WebClient
        this.webClient = new WebClient();

        // Configuration du WebClient pour autoriser les connexions SSL non sécurisées
        webClient.getOptions().setUseInsecureSSL(true);

        // Désactivation du support CSS
        webClient.getOptions().setCssEnabled(false);

        // Désactivation de l'exécution du JavaScript
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    // Méthode pour effectuer le scraping des données à partir d'une page HTML

    /**
     * Methode de scraping
     *
     * @param url
     * @param priceXPath
     * @param titleXPath
     * @param surfaceXPath
     * @param descriptionXPath
     * @param addressXPath
     * @param linkXPath
     * @return
     * @throws IOException
     */
    public List<Annonce> scrape(String url, String priceXPath, String titleXPath, String surfaceXPath, String descriptionXPath,
                                String addressXPath, String linkXPath) throws IOException {
        // Récupération de la page HTML à partir de l'URL spécifié
        HtmlPage htmlPage = webClient.getPage(url);

        // Extraction des éléments HTML à l'aide des expressions XPath fournies
        List<HtmlElement> prices = htmlPage.getByXPath(priceXPath);
        List<HtmlElement> titles = htmlPage.getByXPath(titleXPath);
        List<HtmlElement> surfaces = htmlPage.getByXPath(surfaceXPath);
        List<HtmlElement> descriptions = htmlPage.getByXPath(descriptionXPath);
        List<HtmlElement> addresses = htmlPage.getByXPath(addressXPath);
        List<HtmlElement> links = htmlPage.getByXPath(linkXPath);

        // Trouver la taille maximale parmi toutes les listes extraites
        int maxSize = Math.max(Math.max(Math.max(Math.max(Math.max(prices.size(), titles.size()), surfaces.size()), descriptions.size()), addresses.size()), links.size());

        // Création d'une liste d'objets Annonce pour stocker les données extraites
        List<Annonce> annonces = new ArrayList<>();

        // Parcours des listes extraites et création des objets Annonce
        for (int i = 0; i < maxSize; i++) {
            // On vérifie la taille des différentes parties des annonces pour mettre une valeur par defaut si vide
            String title;
            if (i < titles.size()) {
                title = titles.get(i).getTextContent().trim();
            } else {
                title = "Titre non disponible";
            }

            String price;
            if (i < prices.size()) {
                price = prices.get(i).getTextContent().trim();
            } else {
                price = "Prix non disponible";
            }

            String surface;
            if (i < surfaces.size()) {
                surface = surfaces.get(i).getTextContent().trim();
            } else {
                surface = "Surface non disponible";
            }

            String description;
            if (i < descriptions.size()) {
                description = descriptions.get(i).getTextContent().trim();
            } else {
                description = "Description non disponible";
            }

            String address;
            if (i < addresses.size()) {
                address = addresses.get(i).getTextContent().trim();
            } else {
                address = "Adresse non disponible";
            }

            // Utilisation de getAttribute("href") pour récupérer l'URL du lien
            String link = i < links.size() ? links.get(i).getAttribute("href").trim() : "";

            // Création de l'objet Annonce et ajout à la liste
            Annonce annonce = new Annonce(title, price, surface, description, address, link);
            annonces.add(annonce);
        }
        // Retourne la liste d'annonces extraites
        return annonces;
    }

    /**
     * Methode pour l'ecriture des annonces dans un fichier
     *
     * @param url
     * @param priceXPath
     * @param titleXPath
     * @param surfaceXPath
     * @param descriptionXPath
     * @param addressXPath
     * @param linkXPath
     * @return
     * @throws IOException
     */
    // Méthode pour effectuer le scraping et afficher les données extraites
    public List<Annonce> scrapeAndPrint(String url, String priceXPath, String titleXPath, String surfaceXPath, String descriptionXPath, String addressXPath, String linkXPath) throws IOException {
        // Appel de la méthode scrape pour obtenir la liste d'annonces
        List<Annonce> annonces = scrape(url, priceXPath, titleXPath, surfaceXPath, descriptionXPath, addressXPath, linkXPath);

        // Affichage des données extraites pour chaque annonce
        for (Annonce annonce : annonces) {
            System.out.println("Title: " + annonce.getTitre());
            System.out.println("Price: " + annonce.getPrix());
            System.out.println("Surface: " + annonce.getSurface());
            System.out.println("Description: " + annonce.getDescription());
            System.out.println("Address: " + annonce.getAdresse());
            System.out.println("Link: " + annonce.getLien());
            System.out.println("--------------------");
        }

        // Retourne la liste d'annonces extraites
        return annonces;
    }
}
