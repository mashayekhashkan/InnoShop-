package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@Table(name = "bestellung")
public class Bestellung {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bestellung_id", nullable = false, updatable = false)
    private UUID bestellungId;
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bestellung_produkte", joinColumns = @JoinColumn(name = "bestellung_id"))
    @MapKeyJoinColumn(name = "product_id")
    @Column(name = "menge")
    private Map<Products, Integer> produkteMitMenge = new HashMap<>();
    @Column(name = "bestelldatum", nullable = false)
    private LocalDate bestelldatum;
    @Column(name = "preis", nullable = false)
    private double preis;
    @Column(name = "versand", nullable = false)
    private boolean versand;

    public UUID getBestellungId() {
        return bestellungId;
    }

    public void setBestellungId(UUID bestellungId) {
        this.bestellungId = bestellungId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<Products, Integer> getProdukteMitMenge() {
        return produkteMitMenge;
    }

    public void setProdukteMitMenge(Map<Products, Integer> produkteMitMenge) {
        this.produkteMitMenge = produkteMitMenge;
    }

    public LocalDate getBestelldatum() {
        return bestelldatum;
    }

    public void setBestelldatum(LocalDate bestelldatum) {
        this.bestelldatum = bestelldatum;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public boolean isVersand() {
        return versand;
    }

    public void setVersand(boolean versand) {
        this.versand = versand;
    }
}
