package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.commercetron.beans.Bewertung;
import org.commercetron.beans.Products;

import java.util.List;
import java.util.UUID;

public class BewertungDAO extends BaseDAO<Bewertung, UUID> {
    public BewertungDAO() {
        super(Bewertung.class);
    }

    public List<Bewertung> findByProduct(Products products){
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT b FROM Bewertung b WHERE b.products = :product", Bewertung.class)
                    .setParameter("products", products)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Bewertung> findeBewertungenZuProdukt(Products products){
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Bewertung> query = em.createQuery(
                    "SELECT b FROM Bewertung b WHERE b.products = :products", Bewertung.class);
            query.setParameter("products", products);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
