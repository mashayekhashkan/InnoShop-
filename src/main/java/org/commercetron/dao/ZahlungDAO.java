package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Zahlung;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ZahlungDAO extends BaseDAO<Zahlung, UUID> {
    protected ZahlungDAO() {
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
}
