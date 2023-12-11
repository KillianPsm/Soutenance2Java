package fr.cda.immobilier.model.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DaoFactory {
    private String url;
    private String server;
    private String dbname;
    private String port;
    private String username;
    private String password;

    public DaoFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static DaoFactory getInstance() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {

        }

        DaoFactory instance = new DaoFactory(
                "jdbc:mysql://localhost:3306/soutenance2", "root", "");
//                "jdbc:mysql://" + this.server + ":" + this.port + "/" + this.dbname, this.username, this.password);
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // Récupération du Dao
    public AnnonceDAOImpl getAnnonceDAO() throws SQLException {
        return new AnnonceDAOImpl(this);
    }
}