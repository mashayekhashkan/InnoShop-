package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;
import org.commercetron.beans.Products;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@Table(name = "warenkorb")

public class Warenkorb {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "warenkorb_id", nullable = false, updatable = false)
    private UUID warenkorbId;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "warenkorb_produkte", joinColumns = @JoinColumn(name = "warenkorb_id"))
    @MapKeyJoinColumn(name = "product_id") // Produkt-ID (Fremdschlüssel)
    @Column(name = "menge") // Menge je Produkt
    private Map<Products, Integer> produkteMitMenge = new HashMap<>();
    @Column(name = "versand_preis", nullable = false)
    private double versandPreis;
    @Column(name = "gesamt_preis", nullable = false)
    private double gesamtPreis;

    public UUID getWarenkorbId() {
        return warenkorbId;
    }

    public void setWarenkorbId(UUID warenkorbId) {
        this.warenkorbId = warenkorbId;
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

    public double getVersandPreis() {
        return versandPreis;
    }

    public void setVersandPreis(double versandPreis) {
        this.versandPreis = versandPreis;
    }

    public double getGesamtPreis() {
        return gesamtPreis;
    }

    public void setGesamtPreis(double gesamtPreis) {
        this.gesamtPreis = gesamtPreis;
    }
}
