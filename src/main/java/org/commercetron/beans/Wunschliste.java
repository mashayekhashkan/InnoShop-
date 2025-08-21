package org.commercetron.beans;

import org.commercetron.beans.Products;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Repräsentiert die Wunschliste eines Benutzers.
 *
 * Eine Wunschliste enthält Produkte, die der Benutzer
 * für später vorgemerkt hat, aber noch nicht in den Warenkorb gelegt hat.
 *
 * Beziehungen:
 * - Jeder {@link User} hat genau eine Wunschliste.
 * - Eine Wunschliste kann mehrere {@link Products} enthalten.
 */
@Entity
@Data
@Table(name = "wunschliste")
public class Wunschliste {

    /**
     * Eindeutige ID der Wunschliste.
     * Wird automatisch als UUID generiert.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wunschliste_id", nullable = false, updatable = false)
    private UUID id;

    /**
     * Benutzer, dem diese Wunschliste gehört.
     * Ein Benutzer hat genau eine Wunschliste.
     */
    @OneToOne(mappedBy = "wunschliste")
    private User user;

    /**
     * Produkte, die in der Wunschliste gespeichert sind.
     *
     * Mapping:
     * - Die Zwischentabelle {@code wunsch_liste} verbindet Wunschlisten mit Produkten.
     * - {@code Wunschliste}: Fremdschlüssel auf diese Tabelle
     * - {@code product_id}: Fremdschlüssel auf Produkte
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "wunsch_liste",
            joinColumns = @JoinColumn(name = "Wunschliste"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Products> products = new HashSet<>();

    // Getter und Setter

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Products> getProducts() {
        return products;
    }

    public void setProducts(Set<Products> products) {
        this.products = products;
    }
}