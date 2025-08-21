package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Repräsentiert eine Produktkategorie im System.
 *
 * <p>Die Klasse ist als JPA-Entity annotiert und wird in der Tabelle
 * {@code kategorie} gespeichert. Kategorien dienen zur Gruppierung
 * von Produkten (z. B. „Elektronik“, „Bekleidung“, „Haushalt“).</p>
 *
 * <p>Felder:</p>
 * <ul>
 *   <li>{@code kategorieId} – Primärschlüssel (UUID)</li>
 *   <li>{@code name} – Name der Kategorie (z. B. „Elektronik“)</li>
 * </ul>
 *
 * <p>Hinweis: Es wäre sinnvoll, eine eindeutige Einschränkung
 * (Unique Constraint) auf {@code name} zu setzen, um doppelte Kategorien zu vermeiden.</p>
 */
@Entity
@Data
@Table(name = "kategorie")
public class Kategorie {

    /**
     * Primärschlüssel der Kategorie.
     * <p>Wird automatisch als UUID generiert.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID kategorieId;

    /**
     * Name der Kategorie.
     * <p>Darf nicht null sein. Beispiele: "Elektronik", "Kleidung", "Haushalt".</p>
     */
    @Column(name = "name", nullable = false)
    private String name;

    // --- Getter und Setter ---
    // (explizit implementiert, obwohl durch @Data bereits generiert;
    // können entfernt werden, wenn Lombok genutzt wird)

    public UUID getKategorieId() {
        return kategorieId;
    }

    public void setKategorieId(UUID kategorieId) {
        this.kategorieId = kategorieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}