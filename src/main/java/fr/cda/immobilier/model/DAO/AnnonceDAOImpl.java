package fr.cda.immobilier.model.DAO;

import fr.cda.immobilier.model.metier.Annonce;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Classe d'implementation des méthodes du dao
 */
public class AnnonceDAOImpl implements AnnonceDAO {
    protected Connection conn;
    private static final String TABLE = "annonce";
    private static final String ID_ANNONCE = "id";
    private static final String TITRE = "titre";
    private static final String PRIX = "prix";
    private static final String SURFACE = "surface";
    private static final String DESCRIPTION = "description";
    private static final String LIEN = "lien";
    private static final String TYPE_BIEN = "typeBien";
    private static final String ADRESSE = "adresse";

    /**
     * Appel de connexion a la base de donnees
     * @param daoFactory
     * @throws SQLException
     */
    public AnnonceDAOImpl(DaoFactory daoFactory) throws SQLException {
        try {
            this.conn = daoFactory.getConnection();
        } catch (SQLException e) {
            throw new SQLException("Connexion à la bdd impossible.");
        }
    }

    /**
     * Methode de creation d'annonces dans la base de donnees
     * @param annonce
     */
    @Override
    public void create(Annonce annonce) {
        try {
            String requete = "INSERT INTO " + TABLE + " (" + TITRE + ", " + PRIX + ", " + SURFACE + ", " + DESCRIPTION + ", " + LIEN + ", " + ADRESSE + ") VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(requete);
            pst.setString(1, annonce.getTitre());
            pst.setString(2, annonce.getPrix());
            pst.setString(3, annonce.getSurface());
            pst.setString(4, annonce.getDescription());
            pst.setString(5, annonce.getLien());
            pst.setString(6, annonce.getAdresse());
            pst.executeUpdate();
            pst.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}