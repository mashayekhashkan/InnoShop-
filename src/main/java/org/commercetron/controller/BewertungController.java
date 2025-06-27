package org.commercetron.controller;

import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.BewertungDAO;
import org.commercetron.interfase.DaoInterface;

public class BewertungController extends BaseController {
    private BewertungDAO dao;

    public BewertungController(DaoInterface dao) {
        super(dao);
        if (dao instanceof BewertungDAO){
            this.dao = (BewertungDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }
}
