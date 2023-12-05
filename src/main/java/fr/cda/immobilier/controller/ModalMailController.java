package fr.cda.immobilier.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ModalMailController {
    @FXML
    private Button sendMail;
    @FXML
    private Button cancel;

    public void onCloseClick() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

}