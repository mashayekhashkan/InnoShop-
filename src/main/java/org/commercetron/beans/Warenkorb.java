package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

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
@ManyToOne
@JoinColumn(name = "products_id")
private Products products;
@Column(name = "menge", nullable = false)
private int menge;
@Column(name = "versand_preis", nullable = false)
private double versandPreis;
@Column(name = "gesamt_preis", nullable = false)
private double gesamtPreis;
}
