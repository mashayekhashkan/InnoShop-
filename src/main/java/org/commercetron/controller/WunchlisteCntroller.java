package org.commercetron.controller;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Products;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;
import org.commercetron.beans.Wunschliste;
import org.commercetron.dao.WarenkorbDAO;
import org.commercetron.dao.WunschlisteDAO;
import org.commercetron.interfase.DaoInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WunchlisteCntroller extends BaseController {
    private WunschlisteDAO dao;

    public WunchlisteCntroller(DaoInterface dao) {
        super(dao);
        if (dao instanceof WunschlisteDAO) {
            this.dao = (WunschlisteDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }



    public List<Wunschliste> getByUserId(UUID userId) {
        try {
            if (dao != null) {
                dao.findById(userId);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>();
    }

    public Wunschliste getWunschlisteVonUser(User user) {
        try {
            if (dao != null) {
                dao.findeWunschlisteVonUser(user);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return dao.findeWunschlisteVonUser(user);
    }

    public void getfuegeProduktHinzu(User user, Products product) {
        try {
            if (dao != null) {
                dao.fuegeProduktHinzu(user, product);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void getproductsEntfernen(User user, Products product) {
        try {
            if (dao != null) {
                dao.productsEntfernen(user, product);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
