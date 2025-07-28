package org.commercetron.dao;

import jakarta.persistence.EntityManager;
import org.commercetron.beans.Bestellung;
import org.commercetron.beans.Products;
import org.commercetron.beans.User;
import org.commercetron.interfase.DaoInterface;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BestellungDAO extends BaseDAO<Bestellung, UUID> {
    public BestellungDAO() {
        super(Bestellung.class);
    }

    public List<Bestellung> findByDate(LocalDate bestellungsDate) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT b FROM Bestellung b WHERE b.bestelldatum = :datum ";

            return em.createQuery(jpql, Bestellung.class)
                    .setParameter("datum", bestellungsDate)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Bestellung> findByUser(User user) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT b FROM Bestellung b WHERE b.user = :user ORDER BY b.bestelldatum DESC";
            return em.createQuery(jpql, Bestellung.class)
                    .setParameter("user", user)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Bestellung erstelleBestellung(User user, Map<Products, Integer> produkteMitMenge, boolean versand) {
        Bestellung bestellung = new Bestellung();
        bestellung.setUser(user);
        bestellung.setProdukteMitMenge(produkteMitMenge);
        bestellung.setBestelldatum(LocalDate.now());
        bestellung.setVersand(versand);

        double gesamtpreis = produkteMitMenge.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPreis() * e.getValue())
                .sum();
        bestellung.setPreis(gesamtpreis);

        return save(bestellung);
    }

    public List<Bestellung> findeBestellungenVonUser(User user) {
        EntityManager em = getEntityManager();
        return (List<Bestellung>) em.createQuery("SELECT b FROM Bestellung b WHERE b.user = :user ORDER BY b.bestelldatum DESC", Bestellung.class)
                .setParameter("user", user)
                .getResultList();
    }
}

