package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.commercetron.beans.Admin;

import java.util.List;
import java.util.UUID;

public class AdminDAO extends BaseDAO<Admin, UUID> {

    public AdminDAO() {
        super(Admin.class);
    }

    public Admin findByExactName(String name) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a FROM Admin a WHERE a.name = :name";
            return em.createQuery(jpql, Admin.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
