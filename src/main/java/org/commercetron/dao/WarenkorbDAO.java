package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Warenkorb;

import java.util.List;
import java.util.UUID;

public class WarenkorbDAO extends BaseDAO<Warenkorb, UUID> {


    protected WarenkorbDAO(Class<Warenkorb> entityClass) {
        super(entityClass);
    }

    public List<Warenkorb> findByUserId(UUID userId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.UserId = :UserId";
            return em.createQuery(jpql, Warenkorb.class)
                    .setParameter("teilnehmer_id", userId).getResultList();
        } finally {
            em.close();
        }
    }
}
