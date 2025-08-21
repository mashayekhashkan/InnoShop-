package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;
import org.commercetron.beans.Products;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Repräsentiert den Warenkorb eines Benutzers.
 *
 * Ein Warenkorb enthält alle vom Benutzer ausgewählten Produkte mit deren Mengen
 * sowie Preisangaben (Versand und Gesamtpreis).
 *
 * Beziehungen:
 * - Jeder {@link User} hat genau einen Warenkorb.
 * - Der Warenkorb speichert eine Menge von {@link Products} und deren jeweilige Anzahl.
 */
@Entity
@Data
@Table(name = "warenkorb")
public class Warenkorb {

    /**
     * Eindeutige ID des Warenkorbs.
     * Wird automatisch als UUID generiert.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "warenkorb_id", nullable = false, updatable = false)
    private UUID warenkorbId;

    /**
     * Benutzer, dem dieser Warenkorb zugeordnet ist.
     * Ein Benutzer hat genau einen Warenkorb.
     */
    @OneToOne(mappedBy = "warenkorb")
    private User user;

    /**
     * Enthält alle Produkte im Warenkorb sowie deren Mengen.
     *
     * Mapping:
     * - {@code product_id}: Fremdschlüssel zum Produkt
     * - {@code menge}: Anzahl des jeweiligen Produkts
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "warenkorb_produkte",
            joinColumns = @JoinColumn(name = "warenkorb_id")
    )
    @MapKeyJoinColumn(name = "product_id")
    @Column(name = "menge")
    private Map<Products, Integer> produkteMitMenge = new HashMap<>();

    /**
     * Versandkosten für den Warenkorb.
     */
    @Column(name = "versand_preis", nullable = false)
    private double versandPreis;

    /**
     * Gesamtpreis des Warenkorbs (inkl. Versand).
     */
    @Column(name = "gesamt_preis", nullable = false)
    private double gesamtPreis;

    // Getter und Setter

    public UUID getWarenkorbId() {
        return warenkorbId;
    }

    public void setWarenkorbId(UUID warenkorbId) {
        this.warenkorbId = warenkorbId;
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

    public double getVersandPreis() {
        return versandPreis;
    }

    public void setVersandPreis(double versandPreis) {
        this.versandPreis = versandPreis;
    }

    public double getGesamtPreis() {
        return gesamtPreis;
    }

    public void setGesamtPreis(double gesamtPreis) {
        this.gesamtPreis = gesamtPreis;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Warenkorb warenkorb = (Warenkorb) o;
        return Double.compare(versandPreis, warenkorb.versandPreis) == 0 && Double.compare(gesamtPreis, warenkorb.gesamtPreis) == 0 && Objects.equals(warenkorbId, warenkorb.warenkorbId) && Objects.equals(user, warenkorb.user) && Objects.equals(produkteMitMenge, warenkorb.produkteMitMenge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warenkorbId, user, produkteMitMenge, versandPreis, gesamtPreis);
    }
}
