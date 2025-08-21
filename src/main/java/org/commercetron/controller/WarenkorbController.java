package org.commercetron.controller;

import com.vaadin.pro.licensechecker.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.commercetron.beans.Products;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.ProductsDAO;
import org.commercetron.dao.WarenkorbDAO;
import org.commercetron.interfase.DaoInterface;

import java.util.*;

/**
 * Der {@code WarenkorbController} ist ein spezialisierter Controller zur Verwaltung
 * von Warenkörben. Er erweitert den {@link BaseController} und arbeitet ausschließlich
 * mit einem {@link WarenkorbDAO}.
 *
 * <p>Die Klasse stellt Methoden bereit, um Produkte in den Warenkorb hinzuzufügen,
 * Warenkörbe eines Benutzers abzufragen, den Gesamtpreis zu berechnen und Warenkörbe
 * zu leeren.</p>
 */
public class WarenkorbController extends BaseController {

    /**
     * Referenz auf das konkrete {@link WarenkorbDAO},
     * das für den Datenbankzugriff auf {@link Warenkorb}-Objekte verwendet wird.
     */
    private WarenkorbDAO dao;

    /**
     * Konstruktor für den {@code WarenkorbController}.
     *
     * @param dao Ein {@link DaoInterface}, das zwingend ein {@link WarenkorbDAO} sein muss.
     * @throws IllegalArgumentException falls ein inkompatibles DAO übergeben wird.
     */
    public WarenkorbController(DaoInterface dao) {
        super(dao);
        if (dao instanceof WarenkorbDAO) {
            this.dao = (WarenkorbDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    /**
     * Fügt ein Produkt in einer bestimmten Menge in den Warenkorb eines Benutzers ein.
     *
     * @param user    der Benutzer, dem das Produkt hinzugefügt werden soll.
     * @param product das Produkt, das hinzugefügt werden soll.
     * @param menge   die Menge des Produkts.
     * @throws RuntimeException falls beim Hinzufügen ein Fehler auftritt.
     */
    public void getFuegeProduktHinzu(User user, Products product, int menge) {
        try {
            if (dao != null) {
                dao.fuegeProduktHinzu(user, product, menge);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Liefert eine Liste aller Warenkörbe eines bestimmten Benutzers anhand seiner ID.
     *
     * @param userId die eindeutige ID des Benutzers.
     * @return eine {@link List} von {@link Warenkorb}, ggf. leer wenn kein DAO verfügbar ist.
     */
    public List<Warenkorb> getByUserId(UUID userId) {
        if (dao != null) {
            return dao.findByUserId(userId);
        }
        return Collections.emptyList();
    }

    /**
     * Berechnet den Gesamtpreis eines Warenkorbs anhand der enthaltenen Produkte und deren Mengen.
     *
     * @param produkteMitMenge eine {@link Map}, die Produkte den jeweiligen Mengen zuordnet.
     * @return der berechnete Gesamtpreis.
     */
    public double berechneGesamtpreis(Map<Products, Integer> produkteMitMenge) {
        return produkteMitMenge.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPreis() * e.getValue())
                .sum();
    }

    /**
     * Liefert den aktuellen Warenkorb eines Benutzers.
     *
     * @param user der Benutzer, dessen Warenkorb abgerufen werden soll.
     * @return der {@link Warenkorb} des Benutzers oder {@code null}, falls keiner existiert.
     * @throws RuntimeException falls beim Abrufen ein Fehler auftritt.
     */
    public Warenkorb getWarenkorbVonUser(User user) {
        try {
            if (dao != null) {
                return dao.findeWarenkorbVonUser(user);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Leert den Warenkorb eines Benutzers.
     *
     * @param user der Benutzer, dessen Warenkorb geleert werden soll.
     * @throws RuntimeException falls beim Leeren ein Fehler auftritt.
     */
    public void getleereWarenkorb(User user) {
        try {
            if (dao != null) {
                dao.leereWarenkorb(user);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProduktMenge(User user, Products produkt, int neueMenge) {
        Warenkorb warenkorb = getWarenkorbVonUser(user);
        if (warenkorb != null && warenkorb.getProdukteMitMenge().containsKey(produkt)) {
            if (neueMenge <= 0) {
                warenkorb.getProdukteMitMenge().remove(produkt); // Entfernen, wenn Menge 0
            } else {
                warenkorb.getProdukteMitMenge().put(produkt, neueMenge);
            }

            // Gesamtpreis ggf. neu berechnen
            double gesamt = warenkorb.getProdukteMitMenge().entrySet().stream()
                    .mapToDouble(entry -> entry.getKey().getPreis() * entry.getValue())
                    .sum();

            warenkorb.setGesamtPreis(gesamt);

            update(warenkorb); // Speichern
        }
    }
}