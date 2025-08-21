package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.sql.Blob;
import java.sql.Types;
import java.util.*;


/**
 * Repräsentiert ein Produkt im E-Commerce-System.
 *
 * <p>Die Klasse ist als JPA-Entity annotiert und wird in der Tabelle
 * {@code products} gespeichert. Jedes Produkt gehört zu einer {@link Kategorie}
 * und kann mehrere {@link Bewertung Bewertungen} besitzen.</p>
 *
 * <p>Felder:</p>
 * <ul>
 *   <li>{@code productsId} – Primärschlüssel (UUID)</li>
 *   <li>{@code productsName} – Name des Produkts (z. B. „Laptop“, „Sneaker“)</li>
 *   <li>{@code kategorie} – Zugehörige Kategorie (Many-to-One Beziehung)</li>
 *   <li>{@code bestand} – Aktueller Lagerbestand</li>
 *   <li>{@code status} – Freitextstatus (z. B. „lieferbar“, „ausverkauft“)</li>
 *   <li>{@code preis} – Preis des Produkts</li>
 *   <li>{@code aktiv} – Gibt an, ob das Produkt aktiv im Shop angezeigt wird</li>
 *   <li>{@code image} – Bilddaten (als Byte-Array, in DB als {@code bytea} gespeichert)</li>
 *   <li>{@code bewertungen} – Liste von Kundenbewertungen (One-to-Many Beziehung)</li>
 * </ul>
 *
 * <p>Hinweis: Für bessere Datenkonsistenz könnte man für {@code status}
 * ein Enum statt eines freien Strings verwenden.</p>
 */
@Entity
//@Data  // Lombok wird hier bewusst nicht verwendet, da equals/hashCode und Getter/Setter manuell definiert sind
@Table(name = "products")
public class Products {

    /**
     * Eindeutiger Primärschlüssel des Produkts.
     * <p>Wird automatisch als UUID generiert.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "products_id", nullable = false, updatable = false)
    private UUID productsId;

    /**
     * Name des Produkts.
     * <p>Beispiel: „Smartphone“, „Sneaker“.</p>
     */
    @Column(name = "name", nullable = false)
    private String productsName;

    /**
     * Zugehörige Kategorie des Produkts.
     * <p>Beziehung: Viele Produkte können zu einer Kategorie gehören.</p>
     */
    @ManyToOne
    @JoinColumn(name = "kategorie_id", nullable = false)
    private Kategorie kategorie;

    /**
     * Aktueller Lagerbestand.
     * <p>Darf nicht negativ sein.</p>
     */
    @Column(name = "bestand", nullable = false)
    private int bestand;

    /**
     * Status des Produkts.
     * <p>Beispiel: „lieferbar“, „ausverkauft“, „vorbestellbar“.</p>
     */
    @Column(name = "status", nullable = false)
    private String status;

    /**
     * Preis des Produkts.
     */
    @Column(name = "preis", nullable = false)
    private double preis;

    /**
     * Gibt an, ob das Produkt aktiv im Shop angezeigt wird.
     */
    @Column(name = "aktiv", nullable = false)
    private boolean aktiv = true;

    /**
     * Bilddaten des Produkts (z. B. Vorschaubild).
     * <p>Wird als Byte-Array in der Datenbank gespeichert.</p>
     * <p>DB-Typ: {@code bytea} (PostgreSQL).</p>
     */
    @Lob
    @JdbcTypeCode(Types.BINARY)
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "image", columnDefinition = "bytea")
    private byte[] image;

    /**
     * Liste der Kundenbewertungen für dieses Produkt.
     * <p>Beziehung: Ein Produkt kann mehrere Bewertungen haben.</p>
     */
    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bewertung> bewertungen = new ArrayList<>();

    // --- Getter & Setter (manuell implementiert) ---

    public UUID getProductsId() {
        return productsId;
    }

    public void setProductsId(UUID productsId) {
        this.productsId = productsId;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public Kategorie getKategorie() {
        return kategorie;
    }

    public void setKategorie(Kategorie kategorie) {
        this.kategorie = kategorie;
    }

    public int getBestand() {
        return bestand;
    }

    public void setBestand(int bestand) {
        this.bestand = bestand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public List<Bewertung> getBewertungen() {
        return bewertungen;
    }

    public void setBewertungen(List<Bewertung> bewertungen) {
        this.bewertungen = bewertungen;
    }

    // --- equals & hashCode ---
    // Wichtig für Entity-Vergleiche (z. B. in Sets oder bei Hibernate-Caching)

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Products products = (Products) o;
        return bestand == products.bestand
                && Double.compare(preis, products.preis) == 0
                && aktiv == products.aktiv
                && Objects.equals(productsId, products.productsId)
                && Objects.equals(productsName, products.productsName)
                && Objects.equals(kategorie, products.kategorie)
                && Objects.equals(status, products.status)
                && Objects.deepEquals(image, products.image)
                && Objects.equals(bewertungen, products.bewertungen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                productsId, productsName, kategorie, bestand,
                status, preis, aktiv, Arrays.hashCode(image), bewertungen
        );
    }
}
