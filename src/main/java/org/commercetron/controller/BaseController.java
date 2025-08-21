package org.commercetron.controller;

import org.commercetron.interfase.ControllerInterface;
import org.commercetron.interfase.DaoInterface;

import java.util.List;
import java.util.UUID;

/**
 * Generischer Basiskontroller für CRUD-Operationen.
 *
 * @param <T> Der Entitätstyp, den der Controller verwaltet.
 */
public class BaseController<T> implements ControllerInterface<T> {

    private DaoInterface<T, UUID> dao;

    /**
     * Konstruktor für den Basiskontroller.
     *
     * @param dao Das DAO-Interface zur Verwaltung der Entitäten.
     */
    public BaseController(DaoInterface<T, UUID> dao) {
        this.dao = dao;
    }

    /**
     * Erstellt eine neue Entität in der Datenbank.
     *
     * @param entity Die zu speichernde Entität
     * @return Die gespeicherte Entität oder null bei Fehler
     */
    @Override
    public T create(T entity) {
        try {
            return dao.save(entity);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Sucht eine Entität anhand der ID.
     *
     * @param id Die UUID der Entität
     * @return Die gefundene Entität oder null, wenn nicht gefunden
     */
    @Override
    public T getById(UUID id) {
        try {
            return dao.findById(id);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Liefert alle Entitäten zurück.
     *
     * @return Liste aller Entitäten oder null bei Fehler
     */
    @Override
    public List<T> getAll() {
        try {
            return dao.findAll();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Aktualisiert eine vorhandene Entität.
     *
     * @param entity Die zu aktualisierende Entität
     * @return Die aktualisierte Entität oder null bei Fehler
     */
    @Override
    public T update(T entity) {
        try {
            return dao.update(entity);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Löscht eine Entität.
     *
     * @param entity Die zu löschende Entität
     * @return true, wenn erfolgreich, sonst false
     */
    @Override
    public boolean delete(T entity) {
        try {
            dao.delete(entity);
            return true;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Löscht eine Entität anhand ihrer ID.
     *
     * @param id Die UUID der zu löschenden Entität
     * @return true, wenn erfolgreich, sonst false
     */
    @Override
    public boolean deleteById(UUID id) {
        T entity = getById(id);
        if (entity != null) {
            return delete(entity);
        }
        return false;
    }
}
