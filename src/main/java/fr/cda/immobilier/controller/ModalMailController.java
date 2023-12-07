package fr.cda.immobilier.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ModalMailController {

    @FXML
    private TextField enterEmail;
    @FXML
    private Button sendMail;
    @FXML
    private Button cancel;

    /**
     * Methode pour fermer la modale en cliquant sur un bouton
     */
    public void onCloseClick() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    public void sendAMail() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        // Configure API key authorization: api-key
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey("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");
        try {
            // Création d'une instance de TransactionalEmailsApi
            TransactionalEmailsApi api = new TransactionalEmailsApi();
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail("Killian.POSSEME@greta-bretagne-sud.fr");
            sender.setName("K. Possémé");

            List<SendSmtpEmailTo> toList = new ArrayList<SendSmtpEmailTo>();
            SendSmtpEmailTo to = new SendSmtpEmailTo();

            // Récupération de l'adresse e-mail à partir du TextField
            String email = enterEmail.getText();

            // Vérification si l'adresse e-mail est non vide avant de l'ajouter
            if (!email.isEmpty()) {
                // Définition de l'adresse e-mail du destinataire
                to.setEmail(email);
                to.setName("John Doe");
                toList.add(to);
            } else {
                // Affichage d'un message d'erreur ou gestion appropriée si l'adresse e-mail est vide
                System.out.println("Veuillez saisir une adresse e-mail.");
                return; // Arrêter le traitement si l'adresse e-mail est vide
            }

            // Création de propriétés pour stocker des en-têtes personnalisés
            Properties headers = new Properties();
            headers.setProperty("Some-Custom-Name", "unique-id-1234");

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();

            sendSmtpEmail.setSender(sender);
            sendSmtpEmail.setTo(toList);
            sendSmtpEmail.setHtmlContent("<html><body><h1>Voici un fichier contenant les résultats du scraping</h1></body></html>");
            sendSmtpEmail.setSubject("Scraping immobilier");
            sendSmtpEmail.setHeaders(headers);
            CreateSmtpEmail response = api.sendTransacEmail(sendSmtpEmail);
            // Affichage de la réponse
            System.out.println(response.toString());
        } catch (Exception e) {
            // Gestion des éventuelles exceptions pouvant survenir pendant l'exécution
            System.out.println("Une exception s'est produite : " + e.getMessage());
        }

    }
}