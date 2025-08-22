package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Kategorie;
import org.commercetron.beans.Products;

import java.util.List;
import java.util.UUID;

/**
 * DAO-Klasse zur Verwaltung von Products-Entitäten.
 * Erweitert BaseDAO, um spezifische Datenzugriffsmethoden für Produkte bereitzustellen.
 */
public class ProductsDAO extends BaseDAO<Products, UUID> {

    /**
     * Konstruktor zur Initialisierung von ProductsDAO mit der Products-Entitätsklasse.
     */
    public ProductsDAO() {
        super(Products.class);
    }

    /**
     * Ruft eine bestimmte Anzahl zufälliger Produkte aus der Datenbank ab.
     *
     * @param count Anzahl der zufälligen Produkte, die abgerufen werden sollen
     * @return Liste von zufälligen Produkten
     */
    public List<Products> getRandomProducts(int count) {
        EntityManager em = getEntityManager();
        try {

            String sql = "SELECT p FROM Products p WHERE p.aktiv = true ORDER BY function('RANDOM')";
            return em.createQuery(sql, Products.class)
                    .setMaxResults(count)
                    .getResultList();
        } finally {
            em.close(); // EntityManager schließen, um Ressourcen freizugeben
        }
    }

    /**
     * Findet alle aktiven Produkte einer bestimmten Kategorie.
     * <p>
     * Es werden nur Produkte zurückgegeben, deren `aktiv`-Flag auf `true` gesetzt ist.
     * </p>
     *
     * @param kategorie die Kategorie, für die aktive Produkte abgerufen werden sollen
     * @return Liste der aktiven Produkte in der angegebenen Kategorie
     */
    public List<Products> findByKategorie(Kategorie kategorie) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT p FROM Products p WHERE p.kategorie.kategorieId = :id AND p.aktiv = true";
            return em.createQuery(jpql, Products.class)
                    .setParameter("id", kategorie.getKategorieId())
                    .getResultList();
        } finally {
            em.close();
        }
    }
    /**
     * Findet alle aktiven Produkte.
     *
     * @return Liste aller aktiven Produkte
     */
    public List<Products> findeAlleAktiven() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT p FROM Products p WHERE p.aktiv = true";
            return em.createQuery(jpql, Products.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Findet alle inaktiven Produkte.
     *
     * @return Liste aller inaktiven Produkte
     */
    public List<Products> findeAlleDeaktiven() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT p FROM Products p WHERE p.aktiv = false";
            return em.createQuery(jpql, Products.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Findet alle aktiven Produkte innerhalb einer bestimmten Kategorie.
     *
     * @param kategorie die Kategorie, für die aktive Produkte abgerufen werden sollen
     * @return Liste der aktiven Produkte in der angegebenen Kategorie
     */
    public List<Products> findeAlleAktivenInKategorie(Kategorie kategorie) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT p FROM Products p WHERE p.aktiv = true AND p.kategorie = :kategorie";
            return em.createQuery(jpql, Products.class)
                    .setParameter("kategorie", kategorie)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Findet alle inaktiven Produkte innerhalb einer bestimmten Kategorie.
     *
     * @param kategorie die Kategorie, für die inaktive Produkte abgerufen werden sollen
     * @return Liste der inaktiven Produkte in der angegebenen Kategorie
     */
    public List<Products> findeAlleDeaktivenInKategorie(Kategorie kategorie) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT p FROM Products p WHERE p.aktiv = false AND p.kategorie = :kategorie";
            return em.createQuery(jpql, Products.class)
                    .setParameter("kategorie", kategorie)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
