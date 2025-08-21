package org.commercetron.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.commercetron.beans.Bewertung;
import org.commercetron.beans.Products;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.BewertungDAO;
import org.commercetron.interfase.DaoInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Der {@code BewertungController} ist ein spezialisierter Controller zur Verwaltung von
 * {@link Bewertung}-Entitäten. Er erweitert den {@link BaseController} und arbeitet ausschließlich
 * mit einem {@link BewertungDAO}.
 *
 * <p>Die Klasse stellt Methoden bereit, um Bewertungen für bestimmte Produkte abzufragen.</p>
 */
public class BewertungController extends BaseController<Bewertung>{

    /**
     * Referenz auf das konkrete {@link BewertungDAO},
     * das für den Datenbankzugriff auf {@link Bewertung}-Objekte verwendet wird.
     */
    private BewertungDAO dao;

    /**
     * Konstruktor für den {@code BewertungController}.
     *
     * @param dao Ein {@link DaoInterface}, das zwingend ein {@link BewertungDAO} sein muss.
     * @throws IllegalArgumentException falls ein inkompatibles DAO übergeben wird.
     */
    public BewertungController(DaoInterface dao) {
        super(dao);
        if (dao instanceof BewertungDAO) {
            this.dao= (BewertungDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    /**
     * Liefert alle Bewertungen zu einem bestimmten Produkt.
     *
     * @param product das {@link Products}-Objekt, für das Bewertungen gesucht werden sollen.
     * @return eine {@link List} von {@link Bewertung}, oder eine leere Liste, falls keine gefunden wurden.
     */
    public List<Bewertung> getByProduct(Products product) {
        if (dao != null) {
            return dao.findByProduct(product);
        }
        return new ArrayList<>();
    }

    /**
     * Liefert alle Bewertungen zu einem bestimmten Produkt.
     *
     * <p>Diese Methode ist funktional identisch zu {@link #getByProduct(Products)} und stellt
     * lediglich eine alternative Bezeichnung dar.</p>
     *
     * @param product das {@link Products}-Objekt, für das Bewertungen gesucht werden sollen.
     * @return eine {@link List} von {@link Bewertung}, oder eine leere Liste, falls keine gefunden wurden.
     */
    public List<Bewertung> getBewertungenZuProdukt(Products product) {
        if (dao != null) {
            return dao.findByProduct(product);
        }
        return new ArrayList<>();
    }
}
