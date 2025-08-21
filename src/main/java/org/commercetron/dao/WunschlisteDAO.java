package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.commercetron.beans.Products;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;
import org.commercetron.beans.Wunschliste;

import java.util.List;
import java.util.UUID;

/**
 * DAO-Klasse zur Verwaltung von Wunschliste-Entitäten.
 * <p>
 * Bietet Datenzugriffsfunktionen für Wunschlisten, z. B. Hinzufügen oder Entfernen von Produkten,
 * sowie Abrufen der Wunschliste eines Benutzers.
 * </p>
 */
public class WunschlisteDAO extends BaseDAO<Wunschliste, UUID> {

    /**
     * Konstruktor zur Initialisierung von WunschlisteDAO mit der Wunschliste-Entitätsklasse.
     */
    public WunschlisteDAO() {
        super(Wunschliste.class);
    }

    /**
     * Findet alle Wunschlisten, die einem bestimmten Benutzer zugeordnet sind.
     *
     * @param userId die Benutzer-ID, deren Wunschliste(n) gesucht werden
     * @return Liste der Wunschlisten des Benutzers; kann leer sein
     */
    public List<Wunschliste> findByUserId(UUID userId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT w FROM Wunschliste w WHERE w.user.userId = :userId";
            return em.createQuery(jpql, Wunschliste.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Findet die Wunschliste eines bestimmten Benutzers.
     *
     * @param user der Benutzer, dessen Wunschliste gesucht wird
     * @return die Wunschliste des Benutzers oder {@code null}, falls keine existiert
     */
    public Wunschliste findeWunschlisteVonUser(User user) {
        EntityManager em = getEntityManager();
        try {
            List<Wunschliste> result = em.createQuery(
                            "SELECT w FROM Wunschliste w WHERE w.user = :user", Wunschliste.class)
                    .setParameter("user", user)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Fügt ein Produkt zur Wunschliste eines Benutzers hinzu.
     * <p>
     * Existiert die Wunschliste noch nicht, sollte sie vorher erstellt werden.
     * </p>
     *
     * @param user     der Benutzer, dem die Wunschliste gehört
     * @param product  das hinzuzufügende Produkt
     */
    public void fuegeProduktHinzu(User user, Products product) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Wunschliste wunschliste = findeWunschlisteVonUser(user);
            if (wunschliste != null) {
                wunschliste.getProducts().add(product);
                em.merge(wunschliste);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Entfernt ein Produkt aus der Wunschliste eines Benutzers.
     *
     * @param user    der Benutzer, dessen Produkt entfernt werden soll
     * @param product das zu entfernende Produkt
     */
    public void productsEntfernen(User user, Products product) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Wunschliste wunschliste = em.createQuery(
                            "SELECT w FROM Wunschliste w JOIN FETCH w.products WHERE w.user = :user", Wunschliste.class)
                    .setParameter("user", user)
                    .getSingleResult();

            boolean entfernt = wunschliste.getProducts()
                    .removeIf(p -> p.getProductsId().equals(product.getProductsId()));
            System.out.println("Produkt entfernt? " + entfernt);

            em.merge(wunschliste);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}