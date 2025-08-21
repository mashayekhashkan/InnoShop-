package org.commercetron.beans;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

/**
 * Repräsentiert einen Administrator im System.
 *
 * <p>Die Klasse ist als JPA-Entity annotiert und wird in der Tabelle
 * {@code admin} gespeichert. Jeder Admin besitzt eine eindeutige ID,
 * einen Benutzernamen sowie ein Passwort. Die {@link Lombok @Data}-Annotation
 * generiert zusätzlich Standardmethoden wie {@code equals()}, {@code hashCode()} und {@code toString()}.
 *
 * <p>Validierungen:
 * <ul>
 *   <li>{@code name}: darf nicht null sein und muss eindeutig sein</li>
 *   <li>{@code password}: darf nicht leer sein und muss mindestens 8 Zeichen enthalten</li>
 * </ul>
 *
 * <p>Hinweis: In einer produktiven Anwendung sollte das Passwort niemals im Klartext gespeichert
 * werden, sondern ausschließlich gehasht (z. B. mit BCrypt).</p>
 */
@Entity
@Data
@Table(name = "admin")
public class Admin {

    /**
     * Primärschlüssel der Admin-Entität.
     * <p>Wird automatisch als UUID generiert und ist unveränderlich.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "admin_id", nullable = false, updatable = false)
    private UUID adminId;

    /**
     * Benutzername des Administrators.
     * <p>Muss eindeutig sein und darf nicht null sein.</p>
     */
    @Column(name = "admin_name", nullable = false, unique = true)
    private String name;

    /**
     * Passwort des Administrators.
     * <p>Muss mindestens 8 Zeichen lang sein. Darf nicht leer sein.</p>
     * <p><b>Achtung:</b> In dieser Implementierung wird das Passwort im Klartext gespeichert.
     * In einem produktiven System sollte das Passwort unbedingt gehasht werden.</p>
     */
    @NotBlank
    @Size(min = 8)
    @Column(name = "admin_password", nullable = false, length = 225)
    private String password;

    // Getter und Setter explizit implementiert, obwohl durch @Data bereits vorhanden.
    // Kann entfernt werden, wenn man sich vollständig auf Lombok verlässt.

    public UUID getAdminId() {
        return adminId;
    }

    public void setAdminId(UUID adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}