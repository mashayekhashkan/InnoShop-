package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.commercetron.beans.Bestellung;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;
import org.commercetron.beans.Zahlung;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object (DAO) for {@link Zahlung} entities.
 * <p>
 * Diese Klasse bietet Methoden zum Speichern und Abrufen von Zahlungen basierend auf
 * Rechnungsnummern, Beträgen, Datumsbereichen und Benutzern.
 * </p>
 */
public class ZahlungDAO extends BaseDAO<Zahlung, UUID> {

    /**
     * Erstellt ein neues {@code ZahlungDAO}-Objekt für {@link Zahlung}.
     */
    public ZahlungDAO() {
        super(Zahlung.class);
    }

    /**
     * Sucht Zahlungen anhand einer bestimmten Rechnungsnummer.
     *
     * @param rechnungsnummer die zu suchende Rechnungsnummer
     * @return Liste von Zahlungen mit dieser Rechnungsnummer, sortiert nach Eingangsdatum (neueste zuerst)
     */
    public List<Zahlung> findByRechnungsnummer(String rechnungsnummer) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT z FROM Zahlung z WHERE z.rechnungsnummer = :rechnungsnummer ORDER BY z.eingangsdatum DESC";
            return em.createQuery(jpql, Zahlung.class)
                    .setParameter("rechnungsnummer", rechnungsnummer)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Sucht Zahlungen mit einem Betrag kleiner oder gleich dem angegebenen Wert.
     *
     * @param betrag Maximalbetrag
     * @return Liste der passenden Zahlungen, sortiert aufsteigend nach Betrag und dann absteigend nach Eingangsdatum
     */
    public List<Zahlung> findByMaxBetrag(double betrag) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT z FROM Zahlung z WHERE z.betrag <= :betrag ORDER BY z.betrag ASC, z.eingangsdatum DESC";
            return em.createQuery(jpql, Zahlung.class)
                    .setParameter("betrag", betrag)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Sucht Zahlungen mit einem Betrag größer oder gleich dem angegebenen Wert.
     *
     * @param betrag Minimalbetrag
     * @return Liste der passenden Zahlungen, sortiert aufsteigend nach Betrag und dann absteigend nach Eingangsdatum
     */
    public List<Zahlung> findByMinBetrag(double betrag) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT z FROM Zahlung z WHERE z.betrag >= :betrag ORDER BY z.betrag ASC, z.eingangsdatum DESC";
            return em.createQuery(jpql, Zahlung.class)
                    .setParameter("betrag", betrag)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Sucht Zahlungen, die ab einem bestimmten Datum eingegangen sind.
     *
     * @param date Startdatum (inklusive)
     * @return Liste der Zahlungen ab diesem Datum, sortiert absteigend nach Eingangsdatum
     */
    public List<Zahlung> findFromDate(LocalDate date) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT z FROM Zahlung z WHERE z.eingangsdatum >= :date ORDER BY z.eingangsdatum DESC";
            return em.createQuery(jpql, Zahlung.class)
                    .setParameter("date", date)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Sucht Zahlungen in einem bestimmten Datumsbereich.
     *
     * @param startDate Startdatum (inklusive)
     * @param endDate   Enddatum (inklusive)
     * @return Liste der Zahlungen zwischen den beiden Daten, sortiert absteigend nach Eingangsdatum
     */
    public List<Zahlung> findBetween2Dates(LocalDate startDate, LocalDate endDate) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT z FROM Zahlung z WHERE z.eingangsdatum BETWEEN :startDate AND :endDate ORDER BY z.eingangsdatum DESC";
            return em.createQuery(jpql, Zahlung.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Sucht alle Zahlungen, die einem bestimmten Benutzer zugeordnet sind.
     *
     * @param user der Benutzer, dessen Zahlungen abgerufen werden sollen
     * @return Liste der Zahlungen, sortiert absteigend nach Zahlungsdatum
     */
    public List<Zahlung> findeZahlungenVonUser(User user) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Zahlung> query = em.createQuery(
                    "SELECT z FROM Zahlung z WHERE z.bestellung.user = :user ORDER BY z.zahlungDatum DESC",
                    Zahlung.class);
            query.setParameter("user", user);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Speichert eine neue Zahlung zu einer Bestellung mit gegebenem Betrag.
     * <p>
     * Generiert automatisch eine Rechnungsnummer und setzt das aktuelle Datum sowie den Status auf "bezahlt".
     * </p>
     *
     * @param betrag     der gezahlte Betrag
     * @param bestellung die zugehörige Bestellung
     */
    public void speichereZahlung(double betrag, Bestellung bestellung) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Zahlung zahlung = new Zahlung();
            zahlung.setRechnungsnummer("RE-" + UUID.randomUUID().toString().substring(0, 8));
            zahlung.setZahlungDatum(LocalDate.now());
            zahlung.setBetrag(betrag);
            zahlung.setBestellung(bestellung);
            zahlung.setStatus("bezahlt"); // ← Schreibfehler in "bezahlt" korrigiert

            em.persist(zahlung);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(); // Für produktiven Einsatz mit Logger ersetzen
        } finally {
            em.close();
        }
    }
}