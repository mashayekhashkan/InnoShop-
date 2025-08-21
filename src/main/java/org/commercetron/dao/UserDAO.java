package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;

import java.util.List;
import java.util.UUID;

/**
 * DAO-Klasse zur Verwaltung von User-Entitäten.
 * Erweitert BaseDAO, um spezifische Datenzugriffsmethoden für User bereitzustellen.
 */
public class UserDAO extends BaseDAO<User, UUID> {

    /**
     * Konstruktor zur Initialisierung von UserDAO mit der User-Entitätsklasse.
     */
    public UserDAO() {
        super(User.class);
    }

    /**
     * Findet alle User mit der angegebenen E-Mail-Adresse.
     *
     * @param email die E-Mail-Adresse des Users, nach der gesucht werden soll
     * @return Liste der User, die der angegebenen E-Mail-Adresse entsprechen
     */
    public List<User> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.email = :email";
            return em.createQuery(jpql, User.class)
                    .setParameter("email", email)
                    .getResultList();
        } finally {
            em.close(); // EntityManager schließen, um Ressourcen freizugeben
        }
    }

    /**
     * Erstellt einen neuen User in der Datenbank.
     *
     * @param user die zu erstellende User-Entität
     */
    public void create(User user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Findet alle User, deren Name dem angegebenen Muster entspricht.
     *
     * @param name das Namensmuster für die Suche
     * @return Liste der User, deren Name dem Muster entspricht
     */
    public List<User> findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.name LIKE :name";
            return em.createQuery(jpql, User.class)
                    .setParameter("name", "%" + name + "%") // LIKE für Teilübereinstimmungen
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Prüft, ob ein User mit der angegebenen E-Mail-Adresse bereits existiert.
     *
     * @param email die zu prüfende E-Mail-Adresse
     * @return true, wenn die E-Mail-Adresse bereits existiert, sonst false
     */
    public boolean emailExists(String email) {
        EntityManager em = getEntityManager();
        try {
            long count = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE LOWER(u.email) = LOWER(:email)", long.class)
                    .setParameter("email", email.trim()) // Leerzeichen entfernen für genaue Prüfung
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Findet alle User mit der angegebenen Benutzer-ID.
     *
     * @param costumerId die UUID des Users, nach der gesucht werden soll
     * @return Liste der User, die der angegebenen Benutzer-ID entsprechen
     */
    public List<User> findByUserId(UUID costumerId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.userId = :userId";
            return em.createQuery(jpql, User.class)
                    .setParameter("userId", costumerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}