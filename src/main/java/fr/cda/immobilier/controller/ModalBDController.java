package fr.cda.immobilier.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    /**
     * Méthode pour valider les paramètres de connexion à la base de données
     */
    @FXML
    private void validateDatabaseConnection() {
        String host = hostName.getText();
        String database = dbName.getText();
        String portNumber = port.getText();
        String user = login.getText();
        String pass = password.getText();

        // Construisez l'URL en fonction des informations fournies
        String url = "jdbc:mysql://" + host + ":" + portNumber + "/" + database;

        try {
            // Établissez la connexion à la base de données
            Connection connection = DriverManager.getConnection(url, user, pass);

            // Utilisez la connexion au besoin

            // Fermez la connexion
            connection.close();
        } catch (SQLException e) {
            // Gestion de l'échec de la connexion
            e.printStackTrace();
            // Affichez un message d'erreur à l'utilisateur si nécessaire
        }
    }
}
