package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.commercetron.interfase.DaoInterface;

import java.util.List;
import java.util.UUID;

public abstract class BaseDAO<T, ID> implements DaoInterface<T, ID> {

    protected static final EntityManagerFactory EMF
            = Persistence.createEntityManagerFactory("persistence.xml");

    protected final Class<T> entityClass;

    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    @Override
    public T save(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Fehler beim Speichern: " + e.getMessage());
            throw new RuntimeException("Fehler beim Speichern", e);
        } finally {
            em.close();
        }

    }

    @Override
    public T update(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T updateEntity = em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Fehler beim Aktualisieren: " + e.getMessage());
            throw new RuntimeException("Fehler beim Aktualisieren", e);
        } finally {
            em.close();
        }
        return null;
    }

    @Override
    public T delete(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (!em.contains(em)) {
                entity = em.merge(entity);
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

    @Override
    public T findById(UUID id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM" + entityClass.getSimpleName() + "e";
            return em.createQuery(jpql, entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void close() {

    }

public static void shutdown(){
        if (EMF != null && EMF.isOpen()){
            EMF.close();
        }
}
}

