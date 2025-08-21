package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Bestellung;
import org.commercetron.beans.Products;
import org.commercetron.beans.User;
import org.commercetron.interfase.DaoInterface;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DAO-Klasse zur Verwaltung von Bestellung-Entitäten.
 * Erweitert BaseDAO, um spezifische Datenzugriffsmethoden für Bestellungen bereitzustellen.
 */
public class BestellungDAO extends BaseDAO<Bestellung, UUID> {

    /**
     * Konstruktor zur Initialisierung von BestellungDAO mit der Bestellung-Entitätsklasse.
     */
    public BestellungDAO() {
        super(Bestellung.class);
    }

    /**
     * Findet alle Bestellungen nach einem bestimmten Bestelldatum.
     *
     * @param bestellungsDate Datum der Bestellung
     * @return Liste der Bestellungen, die dem angegebenen Datum entsprechen
     */
    public List<Bestellung> findByDate(LocalDate bestellungsDate) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT b FROM Bestellung b WHERE b.bestelldatum = :datum";
            return em.createQuery(jpql, Bestellung.class)
                    .setParameter("datum", bestellungsDate)
                    .getResultList();
        } finally {
            em.close(); // EntityManager schließen, um Ressourcen freizugeben
        }
    }

    /**
     * Findet alle Bestellungen, die einem bestimmten User zugeordnet sind.
     *
     * @param user User, dessen Bestellungen abgerufen werden sollen
     * @return Liste der Bestellungen des Users, sortiert nach Bestelldatum absteigend
     */
    public List<Bestellung> findByUser(User user) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT b FROM Bestellung b WHERE b.user = :user ORDER BY b.bestelldatum DESC";
            return em.createQuery(jpql, Bestellung.class)
                    .setParameter("user", user)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Erstellt eine neue Bestellung für einen User mit angegebenen Produkten und Versandoption.
     * Berechnet automatisch den Gesamtpreis der Bestellung.
     *
     * @param user User, der die Bestellung aufgibt
     * @param produkteMitMenge Map mit Produkten und deren Mengen
     * @param versand Gibt an, ob Versand erforderlich ist
     * @return Die erstellte Bestellung
     */
    public Bestellung erstelleBestellung(User user, Map<Products, Integer> produkteMitMenge, boolean versand) {
        Bestellung bestellung = new Bestellung();
        bestellung.setUser(user);
        bestellung.setProdukteMitMenge(produkteMitMenge);
        bestellung.setBestelldatum(LocalDate.now());
        bestellung.setVersand(versand);

        // Berechnung des Gesamtpreises anhand der Produkte und deren Mengen
        double gesamtpreis = produkteMitMenge.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPreis() * e.getValue())
                .sum();
        bestellung.setPreis(gesamtpreis);

        return save(bestellung); // Neue Bestellung speichern
    }

    /**
     * Findet alle Bestellungen eines bestimmten Users.
     * Doppelte Methode zu findByUser, mit identischer Funktionalität.
     *
     * @param user User, dessen Bestellungen abgerufen werden sollen
     * @return Liste der Bestellungen des Users, sortiert nach Bestelldatum absteigend
     */
    public List<Bestellung> findeBestellungenVonUser(User user) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT b FROM Bestellung b WHERE b.user = :user ORDER BY b.bestelldatum DESC",
                            Bestellung.class)
                    .setParameter("user", user)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}