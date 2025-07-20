package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.commercetron.beans.Products;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarenkorbDAO extends BaseDAO<Warenkorb, UUID> {


    public WarenkorbDAO(Class<Warenkorb> warenkorbClass) {
        super(Warenkorb.class);
    }

    public List<Warenkorb> findByUserId(UUID userId) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT w FROM Warenkorb w WHERE w.user.userId = :userId";
            return em.createQuery(jpql, Warenkorb.class)
                    .setParameter("user", userId).getResultList();
        } finally {
            em.close();
        }
    }

    public void fuegeProduktHinzu(User user, Products product, int menge) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            Warenkorb warenkorb = findeWarenkorbVonUser(user);
            if (warenkorb == null) {
                warenkorb = new Warenkorb();
                warenkorb.setUser(user);
                warenkorb.setProdukteMitMenge(new HashMap<>());
            }

            Map<Products, Integer> produkte = warenkorb.getProdukteMitMenge();
            produkte.put(product, produkte.getOrDefault(product, 0) + menge);

            warenkorb.setGesamtPreis(berechneGesamtpreis(produkte));
            em.merge(warenkorb); // persist nur bei neuem Objekt nötig

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private double berechneGesamtpreis(Map<Products, Integer> produkteMitMenge) {
        return produkteMitMenge.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPreis() * e.getValue())
                .sum();
    }

    public Warenkorb findeWarenkorbVonUser(User user) {
        EntityManager em = getEntityManager(); // oder über deine Factory
        try {
            return em.createQuery("SELECT w FROM Warenkorb w WHERE w.user = :user", Warenkorb.class)
                    .setParameter("user", user)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void leereWarenkorb(User user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Warenkorb warenkorb = em.createQuery("SELECT w FROM Warenkorb w WHERE w.user = :user", Warenkorb.class)
                    .setParameter("user", user)
                    .getSingleResult();
            warenkorb.getProdukteMitMenge().clear();
            warenkorb.setGesamtPreis(0);
            em.merge(warenkorb);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
