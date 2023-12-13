package fr.cda.immobilier.controller;

import fr.cda.immobilier.model.DAO.DaoFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModalBDController {
    @FXML
    private Button close;
    @FXML
    private Button validate;

    @FXML
    private TextField serverName;
    @FXML
    private TextField dbName;
    @FXML
    private TextField port;
    @FXML
    private TextField login;
    @FXML
    private TextField password;

    /**
     * Méthode pour fermer la modale en cliquant sur un bouton
     */
    public void onCloseClick() {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    public void onValidateClick() {
        DaoFactory.clearConn();

        DaoFactory.DEFAULT_SERVER = serverName.getText();
        DaoFactory.DEFAULT_PORT = port.getText();
        DaoFactory.DEFAULT_DB_NAME = dbName.getText();
        DaoFactory.DEFAULT_USERNAME = login.getText();
        DaoFactory.DEFAULT_PASSWORD = password.getText();

        // Afficher une alerte pour confirmer la modification des paramètres
        showAlert(Alert.AlertType.INFORMATION, "Confirmation", "Paramètres modifiés avec succès.");

        // Fermez la fenêtre modale
        Stage stage = (Stage) validate.getScene().getWindow();
        stage.close();
    }

    /**
     * Affiche une alerte avec le type, le titre et le message spécifiés.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
