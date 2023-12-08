package fr.cda.immobilier.controller;

import fr.cda.immobilier.SoutenanceApplication;
import fr.cda.immobilier.model.DAO.AnnonceDAO;
import fr.cda.immobilier.model.DAO.DaoFactory;
import fr.cda.immobilier.model.metier.Annonce;
import fr.cda.immobilier.scraping.Scraping;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> villeComboBox;
    @FXML
    private Button search;
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

    @FXML
    private void initialize() {
        // Créer un BooleanBinding qui est vrai si au moins une case à cocher est sélectionnée
        BooleanBinding isAnyCheckBoxSelected = Bindings.or(chooseSeLoger.selectedProperty(), chooseOuestFrance.selectedProperty());

        // Lier la disponibilité du bouton de recherche au BooleanBinding
        search.disableProperty().bind(isAnyCheckBoxSelected.not());
    }

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

    /**
     * Methode qui ouvre une fenetre modale avec le mode d'emploi
     */
    public void HelpScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SoutenanceApplication.class.getResource("modalHelp.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = new Stage();
            stage.setTitle("Mode d'emploi de l'application");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            stage.show();
        } catch (Exception e) {
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

            String selectedVille = villeComboBox.getSelectionModel().getSelectedItem();
            String codeVille = choisirVille(selectedVille);

            String selectedType = typeComboBox.getSelectionModel().getSelectedItem();
            String idType = choisirType(selectedType);
            // Vérifier les cases cochées et choisir le site en conséquence
            if (chooseSeLoger.isSelected()) {
                // URL SeLoger
                url = "https://www.seloger.com/list.htm?tri=initial&enterprise=0&idtypebien=&idtt=" + idType + "2,5&naturebien=1,2,4&ci=" + codeVille + "&m=search_hp_new";
//              url = "https://www.seloger.com/list.htm?tri=initial&enterprise=0&idtypebien=1&idtt=2,5&naturebien=1,2,4&ci=560260&m=search_hp_new";
//              url = "https://www.seloger.com/list.htm?tri=initial&enterprise=0&idtypebien=2&idtt=2,5&naturebien=1,2,4&ci=560260&m=search_hp_new";

                priceXPath = ".//div[@data-test='sl.price-label']";
                titleXPath = ".//div[@data-test='sl.title']";
                surfaceXPath = ".//ul[@data-test='sl.tagsLine']";
                descriptionXPath = ".//div[@data-testid='sl.explore.card-description']";
                addressXPath = ".//div[@data-test='sl.address']";
                linkXPath = ".//a[@data-testid='sl.explore.coveringLink']";
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

    @FXML
    public void onSaveFileClick() {
        // Création d'une instance de FileChooser
        FileChooser fileChooser = new FileChooser();

        // Définissez le répertoire initial sur le répertoire "Documents" de l'utilisateur
        String userHome = System.getProperty("user.home");
        String documentsPath = userHome + "/Documents";
        fileChooser.setInitialDirectory(new File(documentsPath));

        // Affichez la boîte de dialogue pour choisir un fichier à sauvegarder
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            // Faites quelque chose avec le fichier sélectionné (dans cet exemple, imprimez simplement le chemin absolu du fichier)
            System.out.println("Fichier sélectionné : " + selectedFile.getAbsolutePath());

            // Appel à la méthode pour sauvegarder les annonces dans le fichier sélectionné
            saveToFile(selectedFile);
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

    /**
     * Méthode pour choisir le code de la ville en fonction de la sélection dans la ComboBox
     */
    private String choisirVille(String choixVille) {
        if (choixVille != null && chooseSeLoger.isSelected()) {
            switch (choixVille) {
                case "vannes":
                    return "560260";
                case "lorient":
                    return "560121";
                case "brest":
                    return "290019";
                case "quimper":
                    return "290232";
                case "guingamp":
                    return "220070";
                case "saint-brieuc":
                    return "220278";
            }
        } else {
            return null;
        }
        return choixVille;
    }

    /**
     *
     */
    @FXML
    private void onVilleComboBoxSelected() {
        String selectedVille = villeComboBox.getSelectionModel().getSelectedItem();
        String codeVille = choisirVille(selectedVille);
        // Utilisez le code de la ville comme nécessaire
        System.out.println("Code de la ville sélectionnée : " + codeVille);
    }

    /**
     * Méthode pour choisir le code de le type en fonction de la sélection dans la ComboBox
     */
    private String choisirType(String choixType) {
        if (choixType != null && chooseSeLoger.isSelected()) {
            switch (choixType) {
                case "appartement":
                    return "1";
                case "maison":
                    return "2";
                case "parking/box":
                    return "3";
                case "terrain":
                    return "4";
            }
        } else {
            return null;
        }
        return choixType;
    }

    /**
     *
     */
    @FXML
    private void onTypeComboBoxSelected() {
        String selectedType = typeComboBox.getSelectionModel().getSelectedItem();
        String idType = choisirType(selectedType);
        // Utilisez le code de la ville comme nécessaire
        System.out.println("Type sélectionné : " + idType);
    }
}