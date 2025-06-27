package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "bestellung")
public class Bestellung {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bestellung_id", nullable = false, updatable = false)
    private UUID bestellungId;
    @OneToOne
    @JoinColumn(name = "costumerId")
    private User user;
    @OneToOne
    @JoinColumn(name = "products_id")
    private Products products;
    @Temporal(TemporalType.DATE)
    @Column(name = "bestelldatum", nullable = false)
    private LocalDate bestelldatum;
    @Column(name = "menge", nullable = false)
    private int menge;
    @Column(name = "preis", nullable = false)
    private double preis;
    @Column(name = "versand", nullable = false)
    private boolean versand;
}
