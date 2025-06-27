package org.commercetron.controller;

import org.commercetron.beans.Warenkorb;
import org.commercetron.beans.Wunschliste;
import org.commercetron.dao.WarenkorbDAO;
import org.commercetron.dao.WunschlisteDAO;
import org.commercetron.interfase.DaoInterface;

import java.util.List;
import java.util.UUID;

public class WunchlisteCntroller extends BaseController {
    private WunschlisteDAO dao;

    public WunchlisteCntroller(DaoInterface dao) {
        super(dao);
        if (dao instanceof WunschlisteDAO) {
            this.dao = (WunschlisteDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    public List<Wunschliste> getWarenkorbByUserId(UUID id) {
        try {
            return dao.findByUserId(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
