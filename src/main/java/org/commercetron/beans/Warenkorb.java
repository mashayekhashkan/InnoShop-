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
    @JoinColumn(name = "customer_id", nullable = false)
    private User user;
    @ElementCollection
    @CollectionTable(name = "warenkorb_produkte", joinColumns = @JoinColumn(name = "warenkorb_id"))
    @MapKeyJoinColumn(name = "product_id") // Produkt-ID (Fremdschlüssel)
    @Column(name = "menge") // Menge je Produkt
    private Map<Products, Integer> produkteMitMenge = new HashMap<>();
    @Column(name = "versand_preis", nullable = false)
    private double versandPreis;
    @Column(name = "gesamt_preis", nullable = false)
    private double gesamtPreis;
}
