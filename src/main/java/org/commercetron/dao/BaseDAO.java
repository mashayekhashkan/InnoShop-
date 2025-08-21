package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.commercetron.interfase.DaoInterface;

import java.util.List;
import java.util.UUID;

/**
 * Abstrakte Basisklasse für Data Access Objects (DAOs), die
 * allgemeine CRUD-Operationen (Create, Read, Update, Delete)
 * für Entitäten des Typs T bereitstellt.
 *
 * @param <T>  Typ der Entität
 * @param <ID> Typ des Primärschlüssels der Entität
 */
public abstract class BaseDAO<T, ID> implements DaoInterface<T, ID> {

    /**
     * Statische EntityManagerFactory zur Erzeugung von EntityManager-Instanzen.
     * Diese Factory wird für die gesamte Lebensdauer der Anwendung wiederverwendet,
     * um Ressourcen effizient zu nutzen.
     */
    protected static final EntityManagerFactory EMF
            = Persistence.createEntityManagerFactory("persistence-unit");

    /**
     * Klasse der verwalteten Entität.
     * Wird benötigt, um generische JPQL-Abfragen und find()-Operationen durchzuführen.
     */
    protected final Class<T> entityClass;

    /**
     * Konstruktor zur Initialisierung der Entitätsklasse.
     *
     * @param entityClass Klasse der Entität
     */
    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Liefert eine neue EntityManager-Instanz für Datenbankoperationen.
     *
     * @return EntityManager
     */
    protected EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Speichert eine Entität in der Datenbank.
     *
     * @param entity Die zu speichernde Entität
     * @return Die gespeicherte Entität
     * @throws RuntimeException bei Fehlern während der Transaktion
     */
    @Override
    public T save(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity); // Persistiert die Entität
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            // Rollback bei Fehlern
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Fehler beim Speichern: " + e.getMessage());
            throw new RuntimeException("Fehler beim Speichern", e);
        } finally {
            em.close(); // EntityManager schließen, um Ressourcen freizugeben
        }
    }

    /**
     * Aktualisiert eine bestehende Entität in der Datenbank.
     *
     * @param entity Die zu aktualisierende Entität
     * @return Die aktualisierte Entität
     * @throws RuntimeException bei Fehlern während der Transaktion
     */
    @Override
    public T update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T updateEntity = em.merge(entity); // Merge führt ein Update durch oder speichert neu
            em.getTransaction().commit();
            return updateEntity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Fehler beim Aktualisieren: " + e.getMessage());
            throw new RuntimeException("Fehler beim Aktualisieren", e);
        } finally {
            em.close();
        }
    }

    /**
     * Löscht eine Entität aus der Datenbank.
     *
     * @param entity Die zu löschende Entität
     * @return null, da die Entität nach dem Löschen nicht mehr existiert
     * @throws RuntimeException bei Fehlern während der Transaktion
     */
    @Override
    public T delete(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(entity)) {
                entity = em.merge(entity); // Falls die Entität nicht verwaltet wird
            }
            em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Fehler beim Löschen: " + e.getMessage());
            throw new RuntimeException("Fehler beim Löschen", e);
        } finally {
            em.close();
        }
        return null;
    }

    /**
     * Sucht eine Entität anhand ihrer ID.
     *
     * @param id Primärschlüssel der Entität
     * @return Gefundene Entität oder null, falls nicht vorhanden
     */
    @Override
    public T findById(UUID id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    /**
     * Liefert alle Entitäten dieser Klasse aus der Datenbank.
     *
     * @return Liste aller Entitäten
     */
    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Schließt Ressourcen, falls notwendig.
     * In dieser Basisklasse derzeit leer implementiert.
     */
    @Override
    public void close() {
        // Optional: Implementierung in abgeleiteten Klassen
    }

    /**
     * Schließt die EntityManagerFactory und gibt alle Ressourcen frei.
     * Sollte beim Herunterfahren der Anwendung aufgerufen werden.
     */
    public static void shutdown() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }
}
