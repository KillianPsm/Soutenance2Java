package fr.cda.immobilier.scraping;

import java.io.IOException;

public class ScraperSeloger {
    public static void main(String args[]) throws IOException {
        String url = "https://www.seloger.com/list.htm?tri=initial&enterprise=0&idtypebien=2&idtt=2,5&naturebien=1,2,4&ci=560260&m=search_hp_new";
        String priceXPath = "//div[@data-test='sl.price-label']";
        String titleXPath = "//div[@data-test='sl.title']";
        String surfaceXPath = "//ul[@data-test='sl.tagsLine']";
        String descriptionXPath = "//div[@data-testid='sl.explore.card-description']";
        String addressXPath = "//div[@data-test='sl.address']";
        String linkXPath = "//a[@data-testid='sl.explore.coveringLink']";

        Scraping scraper = new Scraping();
        scraper.scrapeAndPrint(url, priceXPath, titleXPath, surfaceXPath, descriptionXPath, addressXPath, linkXPath);
    }
}