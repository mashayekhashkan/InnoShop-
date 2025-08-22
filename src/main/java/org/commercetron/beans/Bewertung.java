package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;
import java.util.UUID;

/**
 * Repräsentiert eine Produktbewertung durch einen Benutzer.
 *
 * <p>Die Klasse ist als JPA-Entity annotiert und wird in der Tabelle
 * {@code bewertung} gespeichert. Jede Bewertung ist einem Benutzer zugeordnet
 * und bezieht sich optional auf ein Produkt.</p>
 *
 * <p>Felder:</p>
 * <ul>
 *   <li>{@code bewertungId} – Primärschlüssel (UUID)</li>
 *   <li>{@code user} – Benutzer, der die Bewertung abgegeben hat</li>
 *   <li>{@code products} – Das bewertete Produkt</li>
 *   <li>{@code rating} – Bewertungswert (z. B. Sterne oder Schulnotensystem)</li>
 *   <li>{@code comment} – Freitext-Kommentar des Benutzers</li>
 * </ul>
 *
 * <p>Hinweis: Es wäre empfehlenswert, das Feld {@code rating} als numerischen Wert
 * (z. B. {@code int}) zu modellieren, um Vergleiche und Berechnungen (z. B. Durchschnitt)
 * effizienter zu gestalten.</p>
 */
@Entity
@Data
@Table(name = "bewertung")
public class Bewertung {

    /**
     * Primärschlüssel der Bewertung.
     * <p>Wird automatisch als UUID generiert und ist unveränderlich.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bewertung_id", nullable = false, updatable = false)
    private UUID bewertungId;

    /**
     * Verweis auf den Benutzer, der die Bewertung erstellt hat.
     * <p>Jede Bewertung muss einem Benutzer zugeordnet sein.</p>
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Referenziertes Produkt, auf das sich die Bewertung bezieht.
     * <p>Darf null sein, wenn die Bewertung sich nicht auf ein spezifisches Produkt bezieht
     * (z. B. allgemeines Feedback).</p>
     */
    @ManyToOne
    @JoinColumn(name = "products_id")
    private Products products;

    /**
     * Bewertungswert (z. B. Anzahl Sterne, Schulnote).
     * <p>Aktuell als String modelliert; für numerische Berechnungen wäre {@code int} sinnvoller.</p>
     */
    @Column(name = "rating", nullable = false)
    private String rating;

    /**
     * Kommentartext der Bewertung.
     * <p>Darf nicht null sein. Kann vom Benutzer genutzt werden, um seine Bewertung zu begründen.</p>
     */
    @Column(name = "comment", nullable = false)
    private String comment;

    // --- Getter und Setter ---
    // (explizit implementiert, obwohl durch @Data bereits generiert;
    // können entfernt werden, wenn Lombok genutzt wird)

    public UUID getBewertungId() {
        return bewertungId;
    }

    public void setBewertungId(UUID bewertungId) {
        this.bewertungId = bewertungId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Zwei Bewertungen gelten als gleich, wenn
     * - die IDs übereinstimmen UND
     * - die zugehörigen Benutzer, Produkte, Ratings und Kommentare identisch sind.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bewertung)) return false;
        Bewertung other = (Bewertung) o;
        return bewertungId != null && bewertungId.equals(other.bewertungId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bewertungId);
    }
}