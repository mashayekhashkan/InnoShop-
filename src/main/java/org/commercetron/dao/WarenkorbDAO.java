package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.commercetron.beans.Products;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Access Object (DAO) zur Verwaltung von {@link Warenkorb}-Entitäten.
 * <p>
 * Stellt Datenzugriffsoperationen für Warenkörbe bereit, z. B. Hinzufügen von Produkten,
 * Leeren des Warenkorbs oder Abfrage nach Benutzer-ID.
 * </p>
 */
public class WarenkorbDAO extends BaseDAO<Warenkorb, UUID> {

    /**
     * Erstellt ein neues {@code WarenkorbDAO} für die Entität {@link Warenkorb}.
     *
     * @param warenkorbClass der Klassentyp der Entität (in der Regel {@code Warenkorb.class})
     */
    public WarenkorbDAO(Class<Warenkorb> warenkorbClass) {
        super(Warenkorb.class);
    }

    /**
     * Sucht alle {@link Warenkorb}-Einträge eines bestimmten Benutzers anhand der Benutzer-ID.
     *
     * @param userId die ID des Benutzers, dessen Warenkorb(e) abgerufen werden sollen
     * @return eine Liste von {@code Warenkorb}-Instanzen des Benutzers; kann leer sein
     */
    public List<Warenkorb> findByUserId(UUID userId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT w FROM Warenkorb w WHERE w.user.userId = :userId";
            return em.createQuery(jpql, Warenkorb.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Fügt ein Produkt zum Warenkorb eines Benutzers hinzu.
     * <p>
     * Existiert noch kein Warenkorb für den Benutzer, wird ein neuer erstellt.
     * Ist das Produkt bereits im Warenkorb enthalten, wird die Menge entsprechend erhöht.
     * </p>
     *
     * @param user    der Benutzer, dem der Warenkorb gehört
     * @param product das hinzuzufügende Produkt
     * @param menge   die Menge des Produkts, die hinzugefügt werden soll
     */
    public void fuegeProduktHinzu(User user, Products product, int menge) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Warenkorb warenkorb = findeWarenkorbVonUser(user);
            if (warenkorb == null) {
                warenkorb = new Warenkorb();
                warenkorb.setUser(user);
                warenkorb.setProdukteMitMenge(new HashMap<>());
            }

            Map<Products, Integer> produkte = warenkorb.getProdukteMitMenge();
            produkte.put(product, produkte.getOrDefault(product, 0) + menge);

            warenkorb.setGesamtPreis(berechneGesamtpreis(produkte));
            em.merge(warenkorb); // merge statt persist, da der Warenkorb evtl. bereits existiert

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(); // Fehlerprotokollierung; kann durch einen Logger ersetzt werden
        } finally {
            em.close();
        }
    }

    /**
     * Berechnet den Gesamtpreis aller Produkte im Warenkorb.
     *
     * @param produkteMitMenge eine Map mit Produkten und deren Mengen
     * @return der Gesamtpreis
     */
    private double berechneGesamtpreis(Map<Products, Integer> produkteMitMenge) {
        return produkteMitMenge.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPreis() * e.getValue())
                .sum();
    }

    /**
     * Sucht den Warenkorb eines bestimmten Benutzers.
     *
     * @param user der Benutzer, dessen Warenkorb abgerufen werden soll
     * @return der {@link Warenkorb} des Benutzers oder {@code null}, falls keiner existiert
     */
    public Warenkorb findeWarenkorbVonUser(User user) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT w FROM Warenkorb w WHERE w.user = :user", Warenkorb.class)
                    .setParameter("user", user)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Leert den Warenkorb eines bestimmten Benutzers.
     * <p>
     * Alle Produkte werden entfernt und der Gesamtpreis auf null gesetzt.
     * </p>
     *
     * @param user der Benutzer, dessen Warenkorb geleert werden soll
     */
    public void leereWarenkorb(User user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Warenkorb warenkorb = em.createQuery("SELECT w FROM Warenkorb w WHERE w.user = :user", Warenkorb.class)
                    .setParameter("user", user)
                    .getSingleResult();

            warenkorb.getProdukteMitMenge().clear();
            warenkorb.setGesamtPreis(0);
            em.merge(warenkorb);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(); // Fehlerprotokollierung; sollte durch Logging ersetzt werden
        } finally {
            em.close();
        }
    }
}