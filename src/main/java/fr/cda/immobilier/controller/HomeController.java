package fr.cda.immobilier.controller;

import fr.cda.immobilier.SoutenanceApplication;
import fr.cda.immobilier.model.DAO.AnnonceDAO;
import fr.cda.immobilier.model.DAO.DaoFactory;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class HomeController {
    private static final Logger logger = Logger.getLogger(HomeController.class.getName());

    @FXML
    private CheckBox chooseSeLoger;
    @FXML
    private CheckBox chooseOuestFrance;

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

    /**
     * Methode pour fermer la fenetre
     */
    public void onCloseClick() {
        Platform.exit();
    }

    /**
     * Methode qui ouvre une fenetre modale pour envoyer un mail
     */
    public void MailScene() {
        try {
            // Création d'un chargeur FXMLLoader
            FXMLLoader loader = new FXMLLoader();
            // Spécification de l'emplacement du fichier FXML qui définit la vue de la fenêtre modale
            loader.setLocation(SoutenanceApplication.class.getResource("modalMail.fxml"));
            // Chargement de la vue à partir du fichier FXML, et création d'une nouvelle scène
            Scene scene = new Scene(loader.load(), 400, 300);
            // Création d'une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            // Définition du titre de la fenêtre modale
            stage.setTitle("Envoyer un mail");
            // Définition de la scène pour la fenêtre modale
            stage.setScene(scene);
            // Définition du type de modality de la fenêtre (fenêtre modale bloquant les interactions avec les autres fenêtres)
            stage.initModality(Modality.APPLICATION_MODAL);
            // Affichage de la fenêtre modale et attente de sa fermeture (showAndWait bloque l'exécution jusqu'à ce que la fenêtre soit fermée)
            stage.showAndWait();
            // Affichage de la fenêtre (peut être redondant ici)
            stage.show();
        } catch (Exception e) {
            // Gestion des exceptions : Affichage de la trace de la pile en cas d'erreur
            e.printStackTrace();
        }
    }

    /**
     * Methode qui ouvre une fenetre modale pour modifier les parametres de la base de donnees
     */
    public void SaveScene() {
        try {
            // Création d'un chargeur FXMLLoader
            FXMLLoader loader = new FXMLLoader();
            // Spécification de l'emplacement du fichier FXML qui définit la vue de la fenêtre modale
            loader.setLocation(SoutenanceApplication.class.getResource("modalSave.fxml"));
            // Chargement de la vue à partir du fichier FXML, et création d'une nouvelle scène
            Scene scene = new Scene(loader.load(), 600, 500);
            // Création d'une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            // Définition du titre de la fenêtre modale
            stage.setTitle("Sauvegarder les annonces dans une base de données");
            // Définition de la scène pour la fenêtre modale
            stage.setScene(scene);
            // Définition du type de modality de la fenêtre (fenêtre modale bloquant les interactions avec les autres fenêtres)
            stage.initModality(Modality.APPLICATION_MODAL);
            // Affichage de la fenêtre modale et attente de sa fermeture (showAndWait bloque l'exécution jusqu'à ce que la fenêtre soit fermée)
            stage.showAndWait();
            // Affichage de la fenêtre (peut être redondant ici)
            stage.show();
        } catch (Exception e) {
            // Gestion des exceptions : Affichage de la trace de la pile en cas d'erreur
            e.printStackTrace();
        }
    }

    /**
     * Methode pour effectuer une recherche d'annonces
     */
    @FXML
    private void onSearchButtonClick() {
        try {
            // URL de la page web à scraper
            String url = "";
            // XPath pour extraire les informations de la page web
            String priceXPath = "";
            String titleXPath = "";
            String surfaceXPath = "";
            String descriptionXPath = "";
            String addressXPath = "";
            String linkXPath = "";

            // Vérifier les cases cochées et choisir le site en conséquence
            if (chooseSeLoger.isSelected()) {
                // URL SeLoger
                url = "https://www.seloger.com/list.htm?tri=initial&enterprise=0&idtypebien=2&idtt=2,5&naturebien=1,2,4&ci=560260&m=search_hp_new";
                // Ajoutez les XPath spécifiques pour SeLoger si nécessaire
                priceXPath = ".//div[@data-test='sl.price-label']";
                titleXPath = ".//div[@data-test='sl.title']";
                surfaceXPath = ".//ul[@data-test='sl.tagsLine']";
                descriptionXPath = ".//div[@data-testid='sl.explore.card-description']";
                addressXPath = ".//div[@data-test='sl.address']";
                linkXPath = ".//a[@data-testid='sl.explore.coveringLink']";
            } else if (chooseOuestFrance.isSelected()) {
                // URL Ouest France Immobilier
                url = "https://www.ouestfrance-immo.com/acheter/maison/vannes-56-56000/";
                priceXPath = ".//span[@class='annPrix']";
                titleXPath = ".//span[@class='annTitre']";
                surfaceXPath = ".//span[@class='annCriteres']/div[contains(span[@class='unit'], 'm²')]";
                descriptionXPath = ".//span[@class='annTexte hidden-phone']";
                addressXPath = ".//span[@class='annVille']";
                linkXPath = "//a[@class='annLink  ']";
            }

            // Création d'une instance de la classe Scraping
            Scraping scraper = new Scraping();

            // Scraping de la page web et récupération des annonces
            List<Annonce> annonces = scraper.scrapeAndPrint(url, priceXPath, titleXPath, surfaceXPath, descriptionXPath, addressXPath, linkXPath);

            // Mettez à jour la TableView avec les annonces récupérées
            ObservableList<Annonce> annonceList = FXCollections.observableArrayList(annonces);

            // Liaison des données avec les colonnes de la TableView
            title.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
            price.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrix()));
            resultSurface.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSurface()));
            description.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
            place.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAdresse()));
            lien.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLien()));

            // Mise à jour des données dans la TableView
            searchResult.setItems(annonceList);
            searchResult.refresh();

            // Log d'information indiquant que les données ont été mises à jour dans la TableView
            logger.info("Données mises à jour dans la TableView.");
        } catch (IOException e) {
            // Gestion des erreurs : Affichage de la trace de la pile en cas d'erreur
            e.printStackTrace();
            // Gérer les erreurs de scraping ici
        }
    }

    /**
     * Methode pour sauvgarder les annonces trouvees dans un fichier en selectionnant l'emplacement
     */
    @FXML
    public void onSaveFileClick() {
        // Création d'une instance de FileChooser
        FileChooser fileChooser = new FileChooser();

        // Définissez le répertoire initial sur le répertoire "Documents" de l'utilisateur
        String userHome = System.getProperty("user.home");
        String documentsPath = userHome + "/Documents";
        fileChooser.setInitialDirectory(new File(documentsPath));

        // Affichez la boîte de dialogue pour choisir un fichier à sauvegarder
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            // Faites quelque chose avec le fichier sélectionné (dans cet exemple, imprimez simplement le chemin absolu du fichier)
            System.out.println("Fichier sélectionné : " + selectedFile.getAbsolutePath());
        }
    }

    /**
     * Methode qui fait la mise en forme de la sauvegarde dans un fichier
     *
     * @param file
     */
    private void saveToFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // Récupération des annonces depuis la TableView
            List<Annonce> annonces = searchResult.getItems();

            // Parcours de la liste des annonces et écriture dans le fichier
            for (Annonce annonce : annonces) {
                writer.write("Titre: " + annonce.getTitre() + "\n");
                writer.write("Prix: " + annonce.getPrix() + "\n");
                writer.write("Surface: " + annonce.getSurface() + "\n");
                writer.write("Description: " + annonce.getDescription() + "\n");
                writer.write("Adresse: " + annonce.getAdresse() + "\n");
                writer.write("Lien: " + annonce.getLien() + "\n");
                writer.write("--------------------\n");
            }

            // Vidage du tampon et fermeture du FileWriter
            writer.flush();
            writer.close();

            // Log d'information indiquant que les annonces ont été enregistrées dans le fichier
            logger.info("Annonces enregistrées dans le fichier : " + file.getAbsolutePath());
        } catch (IOException e) {
            // Gestion des exceptions d'entrée/sortie : Affichage de la trace de la pile en cas d'erreur
            e.printStackTrace();
            // Gérer les erreurs d'écriture du fichier ici
        }
    }

    /**
     * Methode pour sauvegader les annonces dans la base de donnees
     */
    @FXML
    private void onSaveDBClick() {
        try {
            // Obtention d'une instance de la factory DAO
            DaoFactory daoFactory = DaoFactory.getInstance();
            // Obtention d'une instance du DAO d'Annonce
            AnnonceDAO annonceDAO = daoFactory.getAnnonceDAO();

            // Récupération des annonces depuis la TableView
            List<Annonce> annonces = searchResult.getItems();

            // Boucle pour insérer chaque annonce dans la base de données
            for (Annonce annonce : annonces) {
                annonceDAO.create(annonce);
            }

            // Log d'information indiquant que les annonces ont été enregistrées dans la base de données
            logger.info("Annonces enregistrées dans la base de données.");
        } catch (SQLException e) {
            // Gestion des exceptions SQL : Affichage de la trace de la pile en cas d'erreur
            e.printStackTrace();
        }
    }
}