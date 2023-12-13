package fr.cda.immobilier.controller;

import fr.cda.immobilier.model.DAO.DaoFactory;
import javafx.fxml.FXML;
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

        // Fermez la fenêtre modale
        Stage stage = (Stage) validate.getScene().getWindow();
        stage.close();
    }
}
