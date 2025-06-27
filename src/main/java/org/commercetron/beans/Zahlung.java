package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "zahlung")
public class Zahlung {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "zahlung_id", nullable = false, updatable = true)
    private UUID zahlungId;
    @Temporal(TemporalType.DATE)
    @Column(name = "zahlung_datum", nullable = false)
    private LocalDate zahlungDatum;
    @Column(name = "status", nullable = false)
    private String staus;
    @Column(name = "rechnungsnummer", nullable = false)
    private String rechnungsnummer;
    @Column(name = "betrag", nullable = false)
    private double betrag;
    @OneToOne
    @JoinColumn(name = "warenkorb_id", nullable = false)
    private Warenkorb warenkorb;


}
