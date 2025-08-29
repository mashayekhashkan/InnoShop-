package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Kategorie;

import java.util.List;
import java.util.UUID;

public class KategorieDAO extends BaseDAO<Kategorie, UUID> {

    public KategorieDAO() {
        super(Kategorie.class);
    }

    public List<Kategorie> findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT k FROM Kategorie k WHERE LOWER(k.name) LIKE LOWER(:name)";
            return em.createQuery(jpql, Kategorie.class)
                    .setParameter("name", "%" + name + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByName(String name) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(k) FROM Kategorie k WHERE LOWER(k.name) = LOWER(:name)",
                            Long.class
                    )
                    .setParameter("name", name.trim())
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
