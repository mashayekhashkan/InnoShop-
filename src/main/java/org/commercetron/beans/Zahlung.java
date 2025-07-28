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
    @Column(name = "zahlung_datum", nullable = false)
    private LocalDate zahlungDatum;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "rechnungsnummer", nullable = false)
    private String rechnungsnummer;
    @Column(name = "betrag", nullable = false)
    private double betrag;
//    @OneToOne
//    @JoinColumn(name = "warenkorb_id", nullable = false)
    @ManyToOne
    @JoinColumn(name = "bestellung_id", nullable = false)
    private Bestellung bestellung;

    public UUID getZahlungId() {
        return zahlungId;
    }

    public void setZahlungId(UUID zahlungId) {
        this.zahlungId = zahlungId;
    }

    public LocalDate getZahlungDatum() {
        return zahlungDatum;
    }

    public void setZahlungDatum(LocalDate zahlungDatum) {
        this.zahlungDatum = zahlungDatum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRechnungsnummer() {
        return rechnungsnummer;
    }

    public void setRechnungsnummer(String rechnungsnummer) {
        this.rechnungsnummer = rechnungsnummer;
    }

    public double getBetrag() {
        return betrag;
    }

    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }

    public Bestellung getBestellung() {
        return bestellung;
    }

    public void setBestellung(Bestellung bestellung) {
        this.bestellung = bestellung;
    }
}
