package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Kategorie;
import org.commercetron.beans.Products;

import java.util.List;
import java.util.UUID;

public class ProductsDAO extends BaseDAO<Products, UUID> {
    public ProductsDAO() {
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
            String jpql = "SELECT p FROM Products p WHERE p.kategorie.kategorieId = :id";
            return em.createQuery(jpql, Products.class)
                    .setParameter("id", kategorie.getKategorieId())
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

