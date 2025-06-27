package org.commercetron.controller;

import org.commercetron.beans.Zahlung;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.ZahlungDAO;
import org.commercetron.interfase.DaoInterface;

import java.time.LocalDate;
import java.util.List;

public class ZahlungController extends BaseController{
    private ZahlungDAO dao;
    public ZahlungController(DaoInterface dao) {
        super(dao);
        if (dao instanceof AdminDAO){
            this.dao = (ZahlungDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    public List<Zahlung> getByRechnungsnummer(String rechnungsnummer) {
        try {
            return dao.findByRechnungsnummer(rechnungsnummer);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Findet Zahlungen, die kleiner-gleich des vorgegebenen Betrags betragen
     * Ergebnisse sind nach Zahlungsbetrag aufsteigend sortiert
     *
     * @param betrag Der maximale Betrag
     * @return Liste der gefundenen Zahlungen
     */
    public List<Zahlung> getByMaxBetrag(double betrag) {
        try {
            return dao.findByMaxBetrag(betrag);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Findet Zahlungen, die größer-gleich des vorgegebenen Betrags betragen
     * Ergebnisse sind nach Zahlungsbetrag aufsteigend sortiert
     *
     * @param betrag Der minimale Betrag
     * @return Liste der gefundenen Zahlungen
     */
    public List<Zahlung> getByMinBetrag(double betrag) {
        try {
            return dao.findByMinBetrag(betrag);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Findet Zahlungen, die ab einem Dataum eingegangen sind. Ergebnisse sind
     * nach Eingangsdatum absteigend sortiert.
     *
     * @param date
     * @return Liste der gefundenen Zahlungen
     */
    public List<Zahlung> getFromDate(LocalDate date) {
        try {
            return dao.findFromDate(date);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Findet Zahlungen, die in einem Zeitraum eingegangen sind. Ergebnisse sind
     * nach Eingangsdatum absteigend sortiert
     *
     * @param startDate
     * @param endDate
     * @return Liste der gefundenen Zahlungen
     */
    public List<Zahlung> getBetween2Dates(LocalDate startDate, LocalDate endDate) {
        try {
            return dao.findBetween2Dates(startDate, endDate);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
