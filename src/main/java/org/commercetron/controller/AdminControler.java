package org.commercetron.controller;

import org.commercetron.beans.Admin;
import org.commercetron.dao.AdminDAO;
import org.commercetron.interfase.DaoInterface;

public class AdminControler extends BaseController{
    private AdminDAO dao;
    public AdminControler(DaoInterface dao) {
        super(dao);
        if (dao instanceof AdminDAO){
            this.dao = (AdminDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    public Admin findByname(Admin name) {
        if (dao != null) {
            return (Admin) dao.findByExactName(String.valueOf(name));
        }
        return null;
    }
}
