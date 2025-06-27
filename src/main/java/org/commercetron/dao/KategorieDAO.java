package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Kategorie;

import java.util.List;
import java.util.UUID;

public class KategorieDAO extends BaseDAO<Kategorie, UUID> {
    protected KategorieDAO() {
        super(Kategorie.class);
    }
    public List<Kategorie> findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT k FROM Kategorie k WHERE k.kategorie LIKE :kategorie";
            return em.createQuery(jpql, Kategorie.class)
                    .setParameter("name", "%" + name + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
