package org.commercetron.controller;

import org.commercetron.interfase.ControllerInterface;
import org.commercetron.interfase.DaoInterface;

import java.util.List;
import java.util.UUID;

public class BaseController<T> implements ControllerInterface<T> {

    private DaoInterface<T, UUID> dao;

    public BaseController(DaoInterface<T, UUID> dao) {
        this.dao = dao;
    }

    @Override
    public T create(T entity){
        try {
            return dao.save(entity);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public T getById(UUID id) {
        try {
            return dao.findById(id);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<T> getAll() {
        try {
            return dao.findAll();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public T update(T entity) {
        try {
            return dao.update(entity);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

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

    @Override
    public boolean deleteById(UUID id) {
        T entity = getById(id);
        if (entity!= null) {
            return delete(entity);
        }
        return false;
    }
}
