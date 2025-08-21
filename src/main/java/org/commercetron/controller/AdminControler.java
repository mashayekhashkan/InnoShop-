package org.commercetron.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.commercetron.beans.Admin;
import org.commercetron.dao.AdminDAO;
import org.commercetron.interfase.DaoInterface;

/**
 * Der {@code AdminControler} ist ein spezialisierter Controller für die Verwaltung von
 * {@link Admin}-Entitäten. Er erweitert den {@link BaseController} und arbeitet
 * ausschließlich mit einem {@link AdminDAO}.
 *
 * <p>Die Klasse kapselt die Logik zum Zugriff auf Admin-Daten und stellt
 * Controller-Funktionalitäten bereit, die speziell für Administratoren relevant sind.</p>
 */
public class AdminControler extends BaseController {

    /**
     * Referenz auf das konkrete {@link AdminDAO},
     * das für den Datenbankzugriff auf {@link Admin}-Objekte verwendet wird.
     */
    private AdminDAO dao;

    /**
     * Konstruktor für den {@code AdminControler}.
     *
     * @param dao Ein {@link DaoInterface}, das zwingend ein {@link AdminDAO} sein muss.
     * @throws IllegalArgumentException wenn ein inkompatibles DAO übergeben wird.
     */
    public AdminControler(DaoInterface dao) {
        super(dao);
        if (dao instanceof AdminDAO) {
            this.dao = (AdminDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    /**
     * Sucht einen {@link Admin} anhand des exakten Namens.
     *
     * ⚠️ Hinweis: Der Parameter ist aktuell ein {@link Admin}-Objekt,
     * jedoch wird nur {@code String.valueOf(name)} genutzt.
     * Sinnvoller wäre ein String-Parameter.
     *
     * @param name ein {@link Admin}-Objekt, dessen Name als Suchkriterium genutzt wird.
     * @return der gefundene {@link Admin} oder {@code null}, wenn kein Treffer vorliegt.
     */
    public Admin findByname(Admin name) {
        if (dao != null) {
            // Ruft das DAO auf und sucht nach einem Admin mit exakt übereinstimmendem Namen
            return dao.findByExactName(String.valueOf(name));
        }
        return null;
    }

    /**
     * Sucht einen {@link Admin} anhand eines exakten Namens (als String).
     *
     * @param name der exakte Name des Administrators.
     * @return der gefundene {@link Admin} oder {@code null}, wenn kein Treffer vorliegt.
     */
    public Admin getByExactName(String name) {
        if (dao != null) {
            return dao.findByExactName(name);
        }
        return null;
    }
}