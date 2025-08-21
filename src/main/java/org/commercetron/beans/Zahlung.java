package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Repräsentiert eine Zahlung im System.
 * Eine Zahlung ist einer Bestellung zugeordnet und enthält Informationen
 * wie Betrag, Datum, Status und Rechnungsnummer.
 */
@Entity
@Data
@Table(name = "zahlung")
public class Zahlung {

    /**
     * Eindeutige ID der Zahlung (UUID, Primärschlüssel).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "zahlung_id", nullable = false, updatable = true)
    private UUID zahlungId;

    /**
     * Datum, an dem die Zahlung erfolgt ist.
     */
    @Column(name = "zahlung_datum", nullable = false)
    private LocalDate zahlungDatum;

    /**
     * Aktueller Status der Zahlung (z. B. "offen", "bezahlt", "storniert").
     */
    @Column(name = "status", nullable = false)
    private String status;

    /**
     * Rechnungsnummer, die zur Zahlung gehört.
     */
    @Column(name = "rechnungsnummer", nullable = false)
    private String rechnungsnummer;

    /**
     * Betrag, der mit dieser Zahlung beglichen wurde.
     */
    @Column(name = "betrag", nullable = false)
    private double betrag;

    /**
     * Bestellung, zu der diese Zahlung gehört (N:1 Beziehung).
     */
    @ManyToOne
    @JoinColumn(name = "bestellung_id", nullable = false)
    private Bestellung bestellung;

    // --- Getter und Setter ---

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