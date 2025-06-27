package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Bestellung;
import org.commercetron.interfase.DaoInterface;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BestellungDAO extends BaseDAO<Bestellung, UUID> {
    protected BestellungDAO(DaoInterface dao) {
        super(Bestellung.class);
    }

public List<Bestellung> findByDate(LocalDate bestellungsDate){
    EntityManager em = getEntityManager();
    try {
        String jpql = "SELECT b FROM Bestellung b WHERE b.bestelldatum = :datum ";

        return em.createQuery(jpql, Bestellung.class)
                .setParameter("datum", bestellungsDate)
                .getResultList();
    } finally {
        em.close();
    }
}
}
