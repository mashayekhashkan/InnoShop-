package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "warenkorb")
public class Warenkorb {
 @OneToOne
 @JoinColumn(name = "customer_id", nullable = false)
private UUID customerId;
@ManyToMany
@JoinColumn(name = "products_id")
private UUID productsId;
@Column(name = "menge", nullable = false)
private int menge;
@Column(name = "versand_preis", nullable = false)
private double versandPreis;
@Column(name = "gesamt_preis", nullable = false)
private double gesamtPreis;
}
