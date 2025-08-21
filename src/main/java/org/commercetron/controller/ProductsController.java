package org.commercetron.controller;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Kategorie;
import org.commercetron.beans.Products;
import org.commercetron.dao.ProductsDAO;
import org.commercetron.dao.UserDAO;
import org.commercetron.interfase.DaoInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Der {@code ProductsController} ist ein spezialisierter Controller für die Verwaltung
 * von {@link Products}-Entitäten. Er erweitert den {@link BaseController} und
 * delegiert Datenbankzugriffe an ein {@link ProductsDAO}.
 *
 * <p>Die Klasse bietet Methoden zur Abfrage von Produkten, z. B. nach Kategorien,
 * Status (aktiv/deaktiviert) oder in zufälliger Auswahl.</p>
 */
public class ProductsController extends BaseController {

    /**
     * Referenz auf das konkrete {@link ProductsDAO},
     * das für den Datenbankzugriff von {@link Products}-Objekten genutzt wird.
     */
    private ProductsDAO dao;

    /**
     * Konstruktor für den {@code ProductsController}.
     *
     * @param dao ein {@link DaoInterface}, das zwingend ein {@link ProductsDAO} sein muss.
     * @throws IllegalArgumentException wenn ein inkompatibles DAO übergeben wird.
     */
    public ProductsController(DaoInterface dao) {
        super(dao);
        if (dao instanceof ProductsDAO) {
            this.dao = (ProductsDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    /**
     * Gibt eine zufällige Auswahl an Produkten zurück.
     *
     * @param count Anzahl der zufällig auszuwählenden Produkte.
     * @return Liste von {@link Products} oder eine leere Liste, wenn kein DAO vorhanden ist.
     */
    public List<Products> getRandom(int count) {
        if (dao != null) {
            return dao.getRandomProducts(count);
        }
        return Collections.emptyList();
    }

    /**
     * Findet alle Produkte, die einer bestimmten {@link Kategorie} zugeordnet sind.
     *
     * @param kategorie Kategorie, nach der gefiltert werden soll.
     * @return Liste von {@link Products} oder eine leere Liste, wenn kein DAO vorhanden ist.
     */
    public List<Products> getByKategorie(Kategorie kategorie) {
        if (dao != null) {
            return dao.findByKategorie(kategorie);
        }
        return new ArrayList<>();
    }

    /**
     * Liefert alle aktuell aktiven Produkte.
     *
     * @return Liste aktiver {@link Products} oder eine leere Liste, wenn kein DAO vorhanden ist.
     */
    public List<Products> getAlleAktiven() {
        if (dao != null) {
            return dao.findeAlleAktiven();
        }
        return new ArrayList<>();
    }

    /**
     * Liefert alle derzeit deaktivierten Produkte.
     *
     * @return Liste deaktivierter {@link Products} oder eine leere Liste, wenn kein DAO vorhanden ist.
     */
    public List<Products> getAlleDeaktiven() {
        if (dao != null) {
            return dao.findeAlleDeaktiven();
        }
        return new ArrayList<>();
    }

    /**
     * Liefert alle aktiven Produkte innerhalb einer bestimmten {@link Kategorie}.
     *
     * @param kategorie Kategorie, nach der gefiltert werden soll.
     * @return Liste aktiver {@link Products} in der Kategorie oder eine leere Liste, wenn kein DAO vorhanden ist.
     */
    public List<Products> getAlleAktivenInKategorie(Kategorie kategorie) {
        if (dao != null) {
            return dao.findeAlleAktivenInKategorie(kategorie);
        }
        return new ArrayList<>();
    }

    /**
     * Liefert alle deaktivierten Produkte innerhalb einer bestimmten {@link Kategorie}.
     *
     * @param kategorie Kategorie, nach der gefiltert werden soll.
     * @return Liste deaktivierter {@link Products} in der Kategorie oder eine leere Liste, wenn kein DAO vorhanden ist.
     */
    public List<Products> findeAlleDeaktivenInKategorie(Kategorie kategorie) {
        if (dao != null) {
            return dao.findeAlleDeaktivenInKategorie(kategorie);
        }
        return new ArrayList<>();
    }
}
