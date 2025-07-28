package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.commercetron.beans.Products;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;
import org.commercetron.beans.Wunschliste;

import java.util.List;
import java.util.UUID;

public class WunschlisteDAO extends BaseDAO<Wunschliste, UUID> {
    public WunschlisteDAO() {
        super(Wunschliste.class);
    }

    public List<Wunschliste> findByUserId(UUID userId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.UserId = :UserId";
            return em.createQuery(jpql, Wunschliste.class)
                    .setParameter("userId", userId).getResultList();
        } finally {
            em.close();
        }
    }

    public Wunschliste findeWunschlisteVonUser(User user) {
        EntityManager em = getEntityManager();
        try {
            List<Wunschliste> result = em.createQuery("SELECT w FROM Wunschliste w WHERE w.user = :user", Wunschliste.class)
                    .setParameter("user", user)
                    .getResultList();

            if (result.isEmpty()) {
                return null;
            } else {
                return result.get(0);
            }

        } finally {
            em.close();
        }
    }

    public void fuegeProduktHinzu(User user, Products products) {

        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        Wunschliste wunschliste = findeWunschlisteVonUser(user);
        wunschliste.getProducts().add(products);

        em.merge(wunschliste);
        em.getTransaction().commit();

    }

    public void productsEntfernen(User user, Products products) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Wunschliste wunschliste = em.createQuery("SELECT w FROM Wunschliste w JOIN FETCH w.products WHERE w.user = :user", Wunschliste.class)
                    .setParameter("user", user)
                    .getSingleResult();
//           boolean removed =  wunschliste.getProducts().remove(products);
            boolean removed = wunschliste.getProducts().removeIf(p -> p.getProductsId().equals(products.getProductsId()));
            System.out.println("Produkt entfernt? " + removed);
            em.merge(wunschliste);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
