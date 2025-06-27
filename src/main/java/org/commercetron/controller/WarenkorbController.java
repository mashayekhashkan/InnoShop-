package org.commercetron.controller;

import org.commercetron.beans.Warenkorb;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.WarenkorbDAO;
import org.commercetron.interfase.DaoInterface;

import java.util.List;
import java.util.UUID;

public class WarenkorbController extends BaseController {
    private WarenkorbDAO dao;
    public WarenkorbController(DaoInterface dao) {
        super(dao);
        if (dao instanceof WarenkorbDAO){
            this.dao = (WarenkorbDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    public List<Warenkorb> getWarenkorbByUserId(UUID id) {
        try {
            return dao.findByUserId(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
