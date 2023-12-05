package fr.cda.immobilier.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ModalMailController {
    @FXML
    private Button sendMail;
    @FXML
    private Button cancel;

    public void onCloseClick() {
        Platform.exit();
    }

}