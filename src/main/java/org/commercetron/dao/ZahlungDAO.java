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

public class ZahlungDAO extends BaseDAO<Zahlung, UUID> {
    public ZahlungDAO() {
        super(Zahlung.class);
    }

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



    public List<Zahlung> findeZahlungenVonUser(User user) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Zahlung> query = em.createQuery(
                "SELECT z FROM Zahlung z WHERE z.bestellung.user = :user ORDER BY z.zahlungDatum DESC",
                Zahlung.class
        );
        query.setParameter("user", user);
        return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void speichereZahlung(double betrag, Bestellung bestellung) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Zahlung zahlung = new Zahlung();
            zahlung.setRechnungsnummer("RE-" + UUID.randomUUID().toString().substring(0, 8));
            zahlung.setZahlungDatum(LocalDate.now());
            zahlung.setBetrag(betrag);
            zahlung.setBestellung(bestellung);
            zahlung.setStatus("bezhalt");

            em.persist(zahlung);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
