package org.commercetron.controller;

import org.commercetron.beans.Bestellung;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.BaseDAO;
import org.commercetron.dao.BestellungDAO;
import org.commercetron.interfase.DaoInterface;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BstellungController extends BestellungDAO {
    private BestellungDAO dao;

    public BstellungController() {
        this.dao = new BestellungDAO();
    }

    public List<Bestellung> getBestellungViaDate(LocalDate date) {
        if (dao != null){
            return dao.findByDate(date);
        }
        return null;
    }
}
