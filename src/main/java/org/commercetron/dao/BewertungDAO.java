package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.commercetron.beans.Bewertung;
import org.commercetron.beans.Products;

import java.util.List;
import java.util.UUID;

/**
 * DAO-Klasse zur Verwaltung von Bewertung-Entitäten.
 * Erweitert BaseDAO, um spezifische Datenzugriffsmethoden für Bewertungen bereitzustellen.
 */
public class BewertungDAO extends BaseDAO<Bewertung, UUID> {

    /**
     * Konstruktor zur Initialisierung von BewertungDAO mit der Bewertung-Entitätsklasse.
     */
    public BewertungDAO() {
        super(Bewertung.class);
    }

    /**
     * Findet alle Bewertungen für ein bestimmtes Produkt.
     *
     * @param products Das Produkt, für das die Bewertungen abgerufen werden sollen
     * @return Liste der Bewertungen für das angegebene Produkt
     */
    public List<Bewertung> findByProduct(Products products) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT b FROM Bewertung b WHERE b.products = :product", Bewertung.class)
                    .setParameter("product", products) // Parametername muss mit der Abfrage übereinstimmen
                    .getResultList();
        } finally {
            em.close(); // EntityManager schließen, um Ressourcen freizugeben
        }
    }

    /**
     * Findet alle Bewertungen für ein bestimmtes Produkt.
     * Diese Methode ist funktional ähnlich zu findByProduct, nutzt jedoch TypedQuery für mehr Klarheit.
     *
     * @param products Das Produkt, für das die Bewertungen abgerufen werden sollen
     * @return Liste der Bewertungen für das angegebene Produkt
     */
    public List<Bewertung> findeBewertungenZuProdukt(Products products) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Bewertung> query = em.createQuery(
                    "SELECT b FROM Bewertung b WHERE b.products = :products", Bewertung.class);
            query.setParameter("products", products);
            return query.getResultList();
        } finally {
            em.close(); // EntityManager schließen, um Ressourcen freizugeben
        }
    }
}
