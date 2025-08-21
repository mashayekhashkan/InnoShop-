package org.commercetron.controller;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.User;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.UserDAO;
import org.commercetron.interfase.DaoInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserController extends BaseController{
    private UserDAO dao;
    public UserController(DaoInterface dao) {
        super(dao);
        if (dao instanceof UserDAO){
            this.dao = (UserDAO) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

    public User findByEmail(String email){
        List<User> users = dao.findByEmail(email);
        return users.isEmpty() ? null : users.get(0);

    }
    /**
     * Sucht einen Teilnehmer anhand seines Namens.
     *
     * @param name Ein Teilnehmer-Objekt, dessen Name als Suchkriterium genutzt wird.
     * @return Der gefundene Teilnehmer oder null, wenn kein passender gefunden wurde oder ein Fehler auftritt.
     */
    public User getByName (User name){
        if (dao != null){

            return (User) dao.findByName(String.valueOf(name));
        }
        return (User) dao.findByName(String.valueOf(name));
    }

    public boolean getExistsemail(String email){
        if (dao != null){
            return dao.emailExists(String.valueOf(email));
        }
        return true;
    }


    public void create(User user) {
        try {
            if (dao != null) {
                dao.create(user);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getByName(String name) {
      if (dao != null){
          return dao.findByUserId(UUID.fromString(name));
      }
      return new ArrayList<>();
    }

    public List<User> findByUserId(UUID userId) {
        if (dao != null) {
            return dao.findByUserId(userId);
        }
        return new ArrayList<>();
    }
}
