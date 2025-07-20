package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Warenkorb;
import org.commercetron.beans.Wunschliste;

import java.util.List;
import java.util.UUID;

public class WunschlisteDAO extends BaseDAO<Wunschliste, UUID> {
    protected WunschlisteDAO() {
        super(Wunschliste.class);
    }

    public List<Wunschliste> findByUserId(UUID userId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.UserId = :UserId";
            return em.createQuery(jpql, Wunschliste.class)
                    .setParameter("userId", userId).getResultList();
        } finally {
            em.close();
        }
    }
}
