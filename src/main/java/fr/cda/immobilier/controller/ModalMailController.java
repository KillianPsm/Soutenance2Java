package fr.cda.immobilier.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;

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