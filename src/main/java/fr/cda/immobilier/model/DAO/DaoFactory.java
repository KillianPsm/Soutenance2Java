package fr.cda.immobilier.model.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

public class DaoFactory {
    private static Connection connect = null;
    public static String DEFAULT_SERVER = "localhost";
    public static String DEFAULT_PORT = "3306";
    public static String DEFAULT_DB_NAME = "soutenance2";
    public static String DEFAULT_USERNAME = "root";
    public static String DEFAULT_PASSWORD = "";

    public DaoFactory() {}

    public static Connection getInstance() throws SQLException {
        if (connect == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String jdbcURL = "jdbc:mysql://" + DEFAULT_SERVER + ":" + DEFAULT_PORT + "/" + DEFAULT_DB_NAME;
                String username = DEFAULT_USERNAME;
                String password = DEFAULT_PASSWORD;
                connect = DriverManager.getConnection(jdbcURL, username, password);
                if (connect != null) {
                    System.out.println("Connexion a la bd reussie");
                } else {
                    System.out.println("Probleme de connexion");
                }
            } catch (SQLException e) {
                System.out.println("Echec de la tentative de connexion : " + e.getMessage() + Arrays.toString(e.getStackTrace()));
                throw e;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return connect;
    }

    public Connection getConnection() throws SQLException {
        return DaoFactory.getInstance();
    }

    // Récupération du Dao
    public AnnonceDAOImpl getAnnonceDAO() throws SQLException {
        return new AnnonceDAOImpl(this);
    }

    public static void clearConn() {
        connect = null;
    }
}