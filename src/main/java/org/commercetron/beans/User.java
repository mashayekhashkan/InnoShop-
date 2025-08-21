package org.commercetron.beans;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import java.time.LocalDate;
import java.util.UUID;

/**
 * Repräsentiert einen Benutzer (User) des Systems.
 *
 * Diese Entity speichert grundlegende Informationen eines Benutzers
 * wie Name, Adresse, Geburtstag, E-Mail und Passwort.
 *
 * Beziehungen:
 * - Jeder Benutzer hat genau einen {@link Warenkorb}.
 * - Jeder Benutzer kann mehrere {@link Bestellung} besitzen.
 * - Jeder Benutzer hat genau eine {@link Wunschliste}.
 */
@Entity
@Data
@Table(name = "users")
public class User {

    /**
     * Eindeutige ID des Benutzers.
     * Wird automatisch als UUID generiert.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    /**
     * Name des Benutzers.
     */
    @Column(name = "name", nullable = false)
    private String user;

    /**
     * Adresse des Benutzers.
     */
    @Column(name = "adresse", nullable = false)
    private String adresse;

    /**
     * Geburtstag des Benutzers.
     * Wird als {@link LocalDate} gespeichert.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "geburtstag", nullable = false)
    private LocalDate geburtstag;

    /**
     * E-Mail-Adresse des Benutzers.
     * Muss eindeutig und ein valides E-Mail-Format besitzen.
     */
    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Passwort des Benutzers.
     * Muss mindestens 8 Zeichen lang sein.
     */
    @Column(name = "password", nullable = false, length = 225)
    @NotBlank
    @Size(min = 8)
    private String password;

    /**
     * Warenkorb des Benutzers.
     * Jeder Benutzer besitzt genau einen Warenkorb.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "warenkorb_id", nullable = false)
    private Warenkorb warenkorb;

    /**
     * Liste der Bestellungen, die der Benutzer aufgegeben hat.
     * Ein Benutzer kann mehrere Bestellungen haben.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Bestellung> bestellung = new ArrayList<>();

    /**
     * Wunschliste des Benutzers.
     * Jeder Benutzer besitzt genau eine Wunschliste.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wunschliste_id", nullable = false)
    private Wunschliste wunschliste;

    // Getter und Setter

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDate getGeburtstag() {
        return geburtstag;
    }

    public void setGeburtstag(LocalDate geburtstag) {
        this.geburtstag = geburtstag;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Warenkorb getWarenkorb() {
        return warenkorb;
    }

    public void setWarenkorb(Warenkorb warenkorb) {
        this.warenkorb = warenkorb;
    }

    public List<Bestellung> getBestellung() {
        return bestellung;
    }

    public void setBestellung(List<Bestellung> bestellung) {
        this.bestellung = bestellung;
    }

    public Wunschliste getWunschliste() {
        return wunschliste;
    }

    public void setWunschliste(Wunschliste wunschliste) {
        this.wunschliste = wunschliste;
    }
}
