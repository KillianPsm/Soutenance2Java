package fr.cda.immobilier.model.DAO;

import fr.cda.immobilier.model.metier.Annonce;

public interface AnnonceDAO {
    /**
     * @param annonce
     */
    void create(Annonce annonce);
}
