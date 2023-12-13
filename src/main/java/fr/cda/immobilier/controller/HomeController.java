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
import javafx.concurrent.Task;
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
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class HomeController {
    private static final Logger logger = Logger.getLogger(HomeController.class.getName());

    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> villeComboBox;
    @FXML
    private TextField minPrice;
    @FXML
    private TextField maxPrice;
    @FXML
    private TextField searchSurface;
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
    private Label annonceCountLabel;

    @FXML
    private ProgressBar progressBar;

    private ObservableList<Annonce> annonceList = FXCollections.observableArrayList();


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
    public void DBParamScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SoutenanceApplication.class.getResource("modalBDParam.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);
            Stage stage = new Stage();
            stage.setTitle("Sauvegarder les annonces dans une base de données");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
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
        // URL de la page web à scraper
        String url;
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

        String pMin = minPrice.getText();

        String pMax = maxPrice.getText();

        String pParam = "";

        String surface = searchSurface.getText();
        String surfaceParam = "";


        // Vérifier la case cochée et choisir le site en conséquence
        // Sur seloger
        if (chooseSeLoger.isSelected()) {
            // Vérifier si les champs de prix sont vide ou non
            // Mettre à jour l'url en fonction avec la/les valeur saisie
            if (!pMin.isEmpty() && !pMax.isEmpty()) {
                pParam = "&price=" + pMin + "/" + pMax;
            } else if (!pMin.isEmpty()) {
                pParam = "&price=" + pMin + "/NaN";
            } else if (!pMax.isEmpty()) {
                pParam = "&price=NaN/" + pMax;
            }

            // Vérifier si le champ surface est vide ou non
            if (!surface.isEmpty()) {
                // Mettre à jour l'url en fonction avec la valeur saisie
                surfaceParam = "&surface=NaN/" + surface;
            }

            // URL SeLoger
            url = "https://www.seloger.com/list.htm?projects=2,5&types=" + idType + "&natures=1,2,4&places=[{\"inseeCodes\":[" + codeVille + "]}]" + pParam + surfaceParam + "&mandatorycommodities=0&enterprise=0&qsVersion=1.0&m=search_refine-redirection-search_results";

            priceXPath = ".//div[@data-test='sl.price-label']";
            titleXPath = ".//div[@data-test='sl.title']";
            surfaceXPath = ".//ul[@data-test='sl.tagsLine']/li[3]";
            descriptionXPath = ".//div[@data-testid='sl.explore.card-description']";
            addressXPath = ".//div[@data-test='sl.address']";
            linkXPath = ".//a[@data-testid='sl.explore.coveringLink']";

            // Sur ouestfranceimmo
        } else if (chooseOuestFrance.isSelected()) {
            if (!pMin.isEmpty() && !pMax.isEmpty()) {
                pParam = "?prix=" + pMin + "_" + pMax;
            } else if (!pMin.isEmpty()) {
                pParam = "?prix=" + pMin + "_0";
            } else if (!pMax.isEmpty()) {
                pParam = "?prix=0_" + pMax;
            }

            if (!surface.isEmpty()) {
                surfaceParam = "&surface=0_ " + surface;
            }
            // URL Ouest France Immobilier
            url = "https://www.ouestfrance-immo.com/acheter/" + idType + "/" + codeVille + "/" + pParam + surfaceParam;
            priceXPath = ".//span[@data-v-09720c1a]";
            titleXPath = ".//p[contains(@data-v-ce3ef9f4, '')]";
            surfaceXPath = ".//div[@class='detail-highlightsitem badge badge--square' and contains(text(), 'm²')]";
            descriptionXPath = ".//div[@class=\"card-annonce__content__description line-clamp\"]/descendant::p";
            addressXPath = ".//p[@data-v-ce3ef9f4][2]";
            linkXPath = ".//a[@data-v-ce3ef9f4]";
        } else {
            url = "";
        }

        // Création d'une instance de la classe Scraping
        Scraping scraper = new Scraping();
        // Créer une instance de la classe Task
        String finalUrl = url;
        String finalPriceXPath = priceXPath;
        String finalTitleXPath = titleXPath;
        String finalSurfaceXPath = surfaceXPath;
        String finalDescriptionXPath = descriptionXPath;
        String finalLinkXPath = linkXPath;
        String finalAddressXPath = addressXPath;
        Task<List<Annonce>> scrapingTask = new Task<List<Annonce>>() {
            @Override
            protected List<Annonce> call() {
                try {
                    return scraper.scrapeAndPrint(finalUrl, finalPriceXPath, finalTitleXPath, finalSurfaceXPath, finalDescriptionXPath, finalAddressXPath, finalLinkXPath);
                } catch (IOException e) {
                    // Gérer l'exception ici (par exemple, afficher un message d'erreur)
                    e.printStackTrace();
                    return Collections.emptyList(); // Ou une autre action appropriée en cas d'erreur
                }
            }
        };

        // Liaison de la ProgressBar avec le travail de la tâche
        progressBar.progressProperty().bind(scrapingTask.progressProperty());
        progressBar.setVisible(true);
        // Définir les actions à effectuer une fois la tâche terminée
        scrapingTask.setOnSucceeded(event -> {
            List<Annonce> annonces = scrapingTask.getValue();

            // Effacer la liste des annonces existantes
            annonceList.clear();

            // Ajouter les nouvelles annonces à la liste
            annonceList.addAll(annonces);

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

            // Mettez à jour le texte de l'étiquette avec le nombre d'annonces
            annonceCountLabel.setText("Nombre d'annonces trouvées : " + annonces.size());

            // Log d'information indiquant que les données ont été mises à jour dans la TableView
            logger.info("Données mises à jour dans la TableView.");
            System.out.println(url);
            majLiens();

            // Désactiver la ProgressBar ici
            progressBar.setVisible(false);

            // Afficher une alerte si la liste d'annonces est vide
            if (annonceList.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Aucune annonce trouvée");
                alert.setHeaderText(null);
                alert.setContentText("Aucune annonce correspondante n'a été trouvée.");
                alert.showAndWait();
            }
        });

        scrapingTask.setOnFailed(event -> {
            // Gérer les erreurs ici, par exemple, afficher une boîte de dialogue d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Une erreur s'est produite pendant la recherche.");
            alert.setContentText(scrapingTask.getException().getMessage());
            alert.showAndWait();
        });

        // Créer un thread pour exécuter la tâche en arrière-plan
        Thread scrapingThread = new Thread(scrapingTask);
        scrapingThread.start();
    }

    /**
     * Methode pour modifier les liens d'annonces incomplets
     */
    private void majLiens() {
        // Récupération des annonces depuis la TableView
        List<Annonce> annonces = searchResult.getItems();

        // Parcours de la liste des annonces
        for (Annonce annonce : annonces) {
            // On vérifie si le lien commence par "/"
            if (annonce.getLien().startsWith("/")) {
                // Ajoutez une variable indiquant le site sélectionné
                String selectedSite = chooseSeLoger.isSelected() ? "seloger" : (chooseOuestFrance.isSelected() ? "ouestfrance" : "");

                // Modifier le lien en fonction du site sélectionné
                switch (selectedSite) {
                    case "seloger":
                        annonce.setLien("https://www.seloger.com" + annonce.getLien());
                        break;
                    case "ouestfrance":
                        annonce.setLien("https://www.ouestfrance-immo.com" + annonce.getLien());
                        break;
                }
            }
        }
    }

    /**
     * Methode pour sauvegarder les annonces au clique
     */
    @FXML
    public void onSaveFileClick() {
        // Création d'une instance de FileChooser
        FileChooser fileChooser = new FileChooser();

        // Définissez le répertoire initial sur le répertoire "Documents" de l'utilisateur
        String userHome = System.getProperty("user.home");
        String documentsPath = userHome + "/Documents";
        fileChooser.setInitialDirectory(new File(documentsPath));

        // Définissez un filtre d'extension par défaut pour les fichiers texte
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers texte (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Affichez la boîte de dialogue pour choisir un fichier à sauvegarder
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            // Assurez-vous que le fichier a l'extension .txt
            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }

            // Faites quelque chose avec le fichier sélectionné
            System.out.println("Fichier sélectionné : " + selectedFile.getAbsolutePath());

            // Appel à la méthode pour sauvegarder les annonces dans le fichier sélectionné
            saveToFile(selectedFile);

            // Affichez la boîte de dialogue de confirmation avec le contexte "fichier texte"
            showConfirmationDialog("fichier texte");
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

    private void showConfirmationDialog(String context) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);

        // Ajustez le message en fonction du contexte
        switch (context.toLowerCase()) {
            case "base de données":
                alert.setContentText("Annonces enregistrées dans la base de données : " + DaoFactory.DEFAULT_DB_NAME);
                break;
            case "fichier texte":
                alert.setContentText("Annonces enregistrées dans un fichier texte.");
                break;
            default:
                alert.setContentText("Opération réussie.");
        }

        alert.showAndWait();
    }

    /**
     * Methode pour sauvegader les annonces dans la base de donnees
     */
    @FXML
    private void onSaveDBClick() {
        try {
            // Obtention d'une instance de la factory DAO
            DaoFactory daoFactory = new DaoFactory();
            // Obtention d'une instance du DAO d'Annonce
            AnnonceDAO annonceDAO = daoFactory.getAnnonceDAO();

            // Récupération des annonces depuis la TableView
            List<Annonce> annonces = searchResult.getItems();

            // Boucle pour insérer chaque annonce dans la base de données
            for (Annonce annonce : annonces) {
                annonceDAO.create(annonce);
            }

            // Log d'information indiquant que les annonces ont été enregistrées dans la base de données
            logger.info("Annonces enregistrées dans la base de données : " + DaoFactory.DEFAULT_DB_NAME);

            // Affichez la boîte de dialogue de confirmation avec le contexte "base de données"
            showConfirmationDialog("base de données");
        } catch (SQLException e) {
            // Gestion des exceptions SQL : Affichage de la trace de la pile en cas d'erreur
            e.printStackTrace();
        }
    }

    /**
     * Methode pour selectionner une ville ce qui modifira l'url en fonction du return
     *
     * @param choixVille
     * @return
     */
    private String choisirVille(String choixVille) {
        if (choixVille != null) {
            if (chooseSeLoger.isSelected()) {
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
            } else if (chooseOuestFrance.isSelected()) {
                switch (choixVille) {
                    case "vannes":
                        return "vannes-56-56000";
                    case "lorient":
                        return "lorient-56-56100";
                    case "brest":
                        return "brest-29-29200";
                    case "quimper":
                        return "quimper-29-29000";
                    case "guingamp":
                        return "guingamp-22-22200";
                    case "saint-brieuc":
                        return "saint-brieuc-22-22000";
                }
            }
        }
        return null;
    }

    @FXML
    private void onVilleComboBoxSelected() {
        String selectedVille = villeComboBox.getSelectionModel().getSelectedItem();
        String codeVille = choisirVille(selectedVille);
        // Utilisez le code de la ville comme nécessaire
        System.out.println("Code de la ville sélectionnée : " + codeVille);
    }

    /**
     * Methode pour selectionner un type de bien ce qui modifira l'url en fonction du return
     *
     * @param choixType
     * @return
     */
    private String choisirType(String choixType) {
        if (choixType != null) {
            if (chooseSeLoger.isSelected()) {
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
            } else if (chooseOuestFrance.isSelected()) {
                switch (choixType) {
                    case "appartement":
                        return "appartement";
                    case "maison":
                        return "maison";
                    case "parking/box":
                        return "garage";
                    case "terrain":
                        return "terrain";
                }
            }
        }
        return null;
    }

    @FXML
    private void onTypeComboBoxSelected() {
        String selectedType = typeComboBox.getSelectionModel().getSelectedItem();
        String idType = choisirType(selectedType);
        // Utilisez le code de la ville comme nécessaire
        System.out.println("Type sélectionné : " + idType);
    }
}