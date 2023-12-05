package fr.cda.immobilier.scraping;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import fr.cda.immobilier.model.metier.Annonce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scraping {

    private WebClient webClient;

    public Scraping() {
        this.webClient = new WebClient();
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    public List<Annonce> scrape(String url, String priceXPath, String titleXPath, String surfaceXPath, String descriptionXPath,
                                String addressXPath, String linkXPath) throws IOException {
        HtmlPage htmlPage = webClient.getPage(url);

        List<HtmlElement> prices = htmlPage.getByXPath(priceXPath);
        List<HtmlElement> titles = htmlPage.getByXPath(titleXPath);
        List<HtmlElement> surfaces = htmlPage.getByXPath(surfaceXPath);
        List<HtmlElement> descriptions = htmlPage.getByXPath(descriptionXPath);
        List<HtmlElement> addresses = htmlPage.getByXPath(addressXPath);
        List<HtmlElement> links = htmlPage.getByXPath(linkXPath);

        // Trouver la taille maximale parmi toutes les listes
        int maxSize = Math.max(Math.max(Math.max(Math.max(Math.max(prices.size(), titles.size()), surfaces.size()), descriptions.size()), addresses.size()), links.size());

        List<Annonce> annonces = new ArrayList<>();

        for (int i = 0; i < maxSize; i++) {
            String title = i < titles.size() ? titles.get(i).getTextContent() : "";
            String price = i < prices.size() ? prices.get(i).getTextContent() : "";
            String surface = i < surfaces.size() ? surfaces.get(i).getTextContent() : "";

            String description;
            if (i < descriptions.size()) {
                description = descriptions.get(i).getTextContent();
            } else {
                description = "Description non disponible";
            }

            String address = i < addresses.size() ? addresses.get(i).getTextContent() : "";

            // Utiliser getAttribute("href") pour récupérer l'URL du lien
            String link = i < links.size() ? links.get(i).getAttribute("href") : "";

            Annonce annonce = new Annonce(title, price, surface, description, address, link);
            annonces.add(annonce);
        }
        return annonces;
    }


    public List<Annonce> scrapeAndPrint(String url, String priceXPath, String titleXPath, String surfaceXPath, String descriptionXPath,
                                        String addressXPath, String linkXPath) throws IOException {
        List<Annonce> annonces = scrape(url, priceXPath, titleXPath, surfaceXPath, descriptionXPath, addressXPath, linkXPath);

        for (Annonce annonce : annonces) {
            System.out.println("Title: " + annonce.getTitre());
            System.out.println("Price: " + annonce.getPrix());
            System.out.println("Surface: " + annonce.getSurface());
            System.out.println("Description: " + annonce.getDescription());
            System.out.println("Address: " + annonce.getLieuBien());
            System.out.println("Link: " + annonce.getLien());
            System.out.println("--------------------");
        }

        return annonces;
    }
}