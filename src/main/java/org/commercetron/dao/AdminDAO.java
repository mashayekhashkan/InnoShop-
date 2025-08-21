package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.commercetron.beans.Admin;

import java.util.List;
import java.util.UUID;
/**
 * DAO-Klasse für die Entität Admin.
 * Erweitert das generische BaseDAO mit UUID als Primärschlüssel.
 */
public class AdminDAO extends BaseDAO<Admin, UUID> {

    /**
     * Konstruktor für AdminDAO.
     * Ruft den Konstruktor der Basisklasse auf und übergibt die Admin-Klasse.
     */
    public AdminDAO() {
        super(Admin.class);
    }

    /**
     * Sucht einen Admin anhand des exakten Namens.
     *
     * @param name Der Name des Admins
     * @return Admin-Objekt, wenn gefunden, sonst null
     */
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
