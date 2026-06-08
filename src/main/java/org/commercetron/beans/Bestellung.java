package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repräsentiert eine Kundenbestellung im System.
 *
 * <p>Die Klasse ist als JPA-Entity annotiert und wird in der Tabelle
 * {@code bestellung} gespeichert. Eine Bestellung ist immer einem Benutzer
 * zugeordnet und enthält eine Liste von Produkten mit den dazugehörigen Mengen.</p>
 *
 * <p>Felder:</p>
 * <ul>
 *   <li>{@code bestellungId} – Primärschlüssel der Bestellung (UUID)</li>
 *   <li>{@code user} – Verweis auf den Benutzer, der die Bestellung aufgegeben hat</li>
 *   <li>{@code produkteMitMenge} – Produkte und deren jeweilige Menge (als Map)</li>
 *   <li>{@code bestelldatum} – Datum, an dem die Bestellung erstellt wurde</li>
 *   <li>{@code preis} – Gesamtpreis der Bestellung</li>
 *   <li>{@code versand} – Status, ob die Bestellung bereits versendet wurde</li>
 * </ul>
 */
@Entity
@Data
@Table(name = "bestellung")

public class Bestellung {

    /**
     * Primärschlüssel der Bestellung.
     * <p>Wird automatisch als UUID generiert und ist unveränderlich.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bestellung_id", nullable = false, updatable = false)
    private UUID bestellungId;

    /**
     * Verknüpfter Benutzer, der die Bestellung aufgegeben hat.
     * <p>Viele Bestellungen können einem Benutzer zugeordnet sein.</p>
     */
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    /**
     * Enthält die bestellten Produkte und deren jeweilige Menge.
     * <p>Wird in einer separaten Join-Tabelle {@code bestellung_produkte}
     * gespeichert. Der Schlüssel ist ein Produkt, der Wert die Anzahl.</p>
     * <p>FetchType.EAGER wird hier verwendet, da die Produkte in den meisten
     * Fällen direkt mitgeladen werden sollen.</p>
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "bestellung_produkte",
            joinColumns = @JoinColumn(name = "bestellung_id")
    )
    @MapKeyJoinColumn(name = "product_id")
    @Column(name = "menge")
    private Map<Products, Integer> produkteMitMenge = new HashMap<>();

    /**
     * Datum der Bestellung.
     * <p>Wird automatisch beim Erstellen einer Bestellung gesetzt.</p>
     */
    @Column(name = "bestelldatum", nullable = false)
    private LocalDate bestelldatum;

    /**
     * Gesamtpreis der Bestellung in Euro.
     * <p>Berechnet sich aus allen bestellten Produkten und deren Mengen.</p>
     */
    @Column(name = "preis", nullable = false)
    private double preis;

    /**
     * Versandstatus der Bestellung.
     * <p>{@code false} = noch nicht versendet, {@code true} = bereits versendet.</p>
     */
    @Column(name = "versand", nullable = false)
    private boolean versand;

    // --- Getter und Setter ---
    // (explizit implementiert, obwohl durch @Data bereits generiert;
    // können entfernt werden, wenn Lombok genutzt wird)

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