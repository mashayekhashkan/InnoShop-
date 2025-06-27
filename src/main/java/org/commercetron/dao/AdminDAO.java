package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Admin;

import java.util.List;
import java.util.UUID;

public class AdminDAO extends BaseDAO<Admin, UUID> {

    public AdminDAO() {
        super(Admin.class);
    }

    public List<Admin> findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT a From Admin a WHERE a.name LIKE: name";
            return em.createQuery(jpql, Admin.class)
                    .setParameter("name", "%" + name + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
