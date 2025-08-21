package org.commercetron.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.commercetron.beans.Bestellung;
import org.commercetron.beans.User;
import org.commercetron.beans.Zahlung;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.ZahlungDAO;
import org.commercetron.interfase.DaoInterface;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ZahlungController extends BaseController {
    private ZahlungDAO dao;

    public ZahlungController(DaoInterface dao) {
        super(dao);
        if (dao instanceof ZahlungDAO) {
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
        return new ArrayList<>();
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
        return new ArrayList<>();
    }


    /**
     * Liefert alle Zahlungen eines bestimmten Benutzers.
     *
     * @param user der Benutzer, dessen Zahlungen abgerufen werden sollen.
     * @return eine {@link List} von {@link Zahlung}, oder eine leere Liste, falls keine Zahlungen gefunden wurden
     * oder ein Fehler auftritt.
     */
    public List<Zahlung> getZahlungenVonUser(User user) {
        try {
            return dao.findeZahlungenVonUser(user);
        } catch (RuntimeException e) {
            // Fehlerausgabe für Debugging – in Produktivcode besser Logging verwenden
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Speichert eine neue Zahlung für eine bestimmte Bestellung.
     *
     * @param betrag     der zu zahlende Betrag.
     * @param bestellung die zugehörige Bestellung, für die die Zahlung gespeichert wird.
     * @throws RuntimeException falls beim Speichern der Zahlung ein Fehler auftritt.
     */
    public void getSpeichereZahlung(double betrag, Bestellung bestellung) {
        try {
            if (dao != null) {
                dao.speichereZahlung(betrag, bestellung);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
