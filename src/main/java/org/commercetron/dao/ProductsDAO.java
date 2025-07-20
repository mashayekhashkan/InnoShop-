package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Kategorie;
import org.commercetron.beans.Products;

import java.util.List;
import java.util.UUID;

public class ProductsDAO extends BaseDAO<Products, UUID> {
    protected ProductsDAO() {
        super(Products.class);
    }

    public List getRandomProducts(int count) {
        EntityManager em = getEntityManager();
        try {
            String sql = "SELECT p FROM Products p ORDER BY function('RANDOM')";
            return em.createQuery(sql, Products.class)
                    .setMaxResults(count)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Products> findByKategorie(Kategorie kategorie) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Products p WHERE p.kategorie = :kategorie", Products.class)
                    .setParameter("kategorie", kategorie)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

