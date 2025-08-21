package org.commercetron.controller;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Bestellung;
import org.commercetron.beans.Products;
import org.commercetron.beans.User;
import org.commercetron.dao.BestellungDAO;
import org.commercetron.interfase.DaoInterface;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BestellungController extends BestellungDAO {
    private BestellungDAO dao;
    public BestellungController(DaoInterface dao) {
        if (dao instanceof BestellungDAO) {
            this.dao = (BestellungDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    public List<Bestellung> getByDate(LocalDate bestellungsDate) {
        EntityManager em = getEntityManager();
        if (dao != null) {
            return dao.findByDate(bestellungsDate);
        }
        return new ArrayList<>();
    }

    public List<Bestellung> getByUser(User user) {
        if (dao != null) {
            return dao.findByUser(user);
        }
        return new ArrayList<>();
    }

    public Bestellung getBestellung(User user, Map<Products, Integer> produkteMitMenge, boolean versand) {
        if (dao != null) {
            return dao.erstelleBestellung(user, produkteMitMenge, versand);
        }
        throw new IllegalStateException("DAO ist nicht initialisiert");
    }

    public List<Bestellung> getBestellungenVonUser(User user) {
        if (dao != null) {
            return dao.findeBestellungenVonUser(user);
        }
        return new ArrayList<>();
    }
}
