package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.User;

import java.util.List;
import java.util.UUID;

public class UserDAO extends BaseDAO<User, UUID> {
    public UserDAO() {
        super(User.class);
    }
    public List<User> findByEmail(User email) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.email = : u.email";
            return em.createQuery(jpql, User.class)
                    .setParameter("email", email)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<User> findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.name LIKE :name";
            return em.createQuery(jpql, User.class)
                    .setParameter("name", "%" + name + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean emailExists(String email){
        EntityManager em = getEntityManager();
        try {
            long count = em.createQuery("SELECT COUNT(u) FROM User u" +
                    "WHERE LOWER(u.email) = LOWER(:email)", long.class)
                    .setParameter("email", email.trim())
                    .getSingleResult();
            return count != 0 && count > 0;
        } finally {
            em.close();
        }
    }
}
