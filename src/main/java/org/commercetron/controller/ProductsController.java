package org.commercetron.controller;

import org.commercetron.beans.Kategorie;
import org.commercetron.beans.Products;
import org.commercetron.dao.ProductsDAO;
import org.commercetron.dao.UserDAO;
import org.commercetron.interfase.DaoInterface;

import java.util.ArrayList;
import java.util.List;

public class ProductsController extends BaseController{
    private ProductsDAO dao;
    public ProductsController(DaoInterface dao) {
        super(dao);
        if (dao instanceof UserDAO){
            this.dao = (ProductsDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    public List getRondom(int cont){
        if (dao != null){
            return dao.getRandomProducts(cont);
        }

        return new ArrayList<>();
    }

    public List getBayKategorie(Kategorie kategorie){
        if (dao != null){
            return dao.findByKategorie(kategorie);
        }
        return new ArrayList<>();
    }
}
