package fr.cda.immobilier.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ModalSaveController {
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
    @FXML
    private Button validate;
    @FXML
    private Button close;

    public void onCloseClick() {
        Platform.exit();
    }
}