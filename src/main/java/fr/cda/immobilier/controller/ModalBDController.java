package fr.cda.immobilier.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModalBDController {
    @FXML
    private Button close;

    @FXML
    private TextField hostName;

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
}
