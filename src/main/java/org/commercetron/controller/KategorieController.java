package org.commercetron.controller;

import org.commercetron.beans.Kategorie;
import org.commercetron.dao.KategorieDAO;
import org.commercetron.interfase.DaoInterface;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class KategorieController extends BaseController {
    private KategorieDAO dao;
    public KategorieController(DaoInterface dao) {
        super(dao);
        if (dao instanceof KategorieDAO){
            this.dao = (KategorieDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    public List<Kategorie> getByName(String name) {
        try {
            return dao.findByName(name);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
        return Collections.emptyList();
    }
}
