package fr.cda.immobilier.controller;

import fr.cda.immobilier.SoutenanceApplication;
import fr.cda.immobilier.model.metier.Annonce;
import fr.cda.immobilier.scraping.Scraping;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class HomeController {
    private static final Logger logger = Logger.getLogger(HomeController.class.getName());
    @FXML
    private MenuItem saveInFile;
    @FXML
    private MenuItem sendMail;
    @FXML
    private MenuItem saveDB;
    @FXML
    private MenuItem close;

    @FXML
    private MenuItem dbSettings;

    @FXML
    private MenuItem userGuide;

    @FXML
    private ChoiceBox chooseType;
    @FXML
    private TextField minPrice;
    @FXML
    private TextField maxPrice;
    @FXML
    private TextField searchSurface;
    @FXML
    private TextField chooseLoc;

    @FXML
    private TableView<Annonce> searchResult;
    @FXML
    private TableColumn<Annonce, String> title;
    @FXML
    private TableColumn<Annonce, String> price;
    @FXML
    private TableColumn<Annonce, String> resultSurface;
    @FXML
    private TableColumn<Annonce, String> description;
    @FXML
    private TableColumn<Annonce, String> place;
    @FXML
    private TableColumn<Annonce, String> lien;

    public void onCloseClick() {
        Platform.exit();
    }

    public void MailScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SoutenanceApplication.class.getResource("modalMail.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            Stage stage = new Stage();
            stage.setTitle("Envoyer un mail");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SaveScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SoutenanceApplication.class.getResource("modalSave.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);
            Stage stage = new Stage();
            stage.setTitle("Sauvegarder les annonces dans une base de données");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSearchButtonClick() {
        try {
            String url = "https://www.seloger.com/list.htm?tri=initial&enterprise=0&idtypebien=2&idtt=2,5&naturebien=1,2,4&ci=560260&m=search_hp_new";
            String priceXPath = ".//div[@data-test='sl.price-label']";
            String titleXPath = ".//div[@data-test='sl.title']";
            String surfaceXPath = ".//ul[@data-test='sl.tagsLine']";
            String descriptionXPath = ".//div[@data-testid='sl.explore.card-description']";
            String addressXPath = ".//div[@data-test='sl.address']";
            String linkXPath = ".//a[@data-testid='sl.explore.coveringLink']";

            Scraping scraper = new Scraping();
            List<Annonce> annonces = scraper.scrapeAndPrint(url, priceXPath, titleXPath, surfaceXPath, descriptionXPath, addressXPath, linkXPath);

            // Mettez à jour la TableView avec les annonces récupérées
            ObservableList<Annonce> annonceList = FXCollections.observableArrayList(annonces);

            title.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
            price.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrix()));
            resultSurface.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSurface()));
            description.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
            place.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLieuBien()));
            lien.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLien()));

            searchResult.setItems(annonceList);
            searchResult.refresh();
            logger.info("Données mises à jour dans la TableView.");
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer les erreurs de scraping ici
        }

    }

    public void onSaveFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer les annonces dans un fichier texte");

        // Définir par défaut l'extension du fichier en tant que ".txt"
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers texte (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Définir le nom de fichier initial
        fileChooser.setInitialFileName("annonces");

        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            saveAnnouncementsToFile(selectedFile);
        }
    }

    private void saveAnnouncementsToFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            List<Annonce> annonces = searchResult.getItems();

            for (Annonce annonce : annonces) {
                writer.write("Titre: " + annonce.getTitre() + "\n");
                writer.write("Prix: " + annonce.getPrix() + "\n");
                writer.write("Surface: " + annonce.getSurface() + "\n");
                writer.write("Description: " + annonce.getDescription() + "\n");
                writer.write("Adresse: " + annonce.getLieuBien() + "\n");
                writer.write("Lien: " + annonce.getLien() + "\n");
                writer.write("--------------------\n");
            }

            writer.flush();
            writer.close();
            logger.info("Annonces enregistrées dans le fichier : " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer les erreurs d'écriture du fichier ici
        }
    }
}