package org.commercetron.beans;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.util.UUID;


import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;
    @Column(name = "name", nullable = false)
    private String user;
    @Column(name = "adresse", nullable = false)
    private String adresse;
    @Temporal(TemporalType.DATE)
    @Column(name = "geburtstag", nullable = false)
    private LocalDate geburtstag;
    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false, length = 225)
    @NotBlank
    @Size(min = 8)
    private String password;
    @OneToOne
    @JoinColumn(name = "warenkorb_id", nullable = false)
    private Warenkorb warenkorb;
    @OneToMany
    @JoinColumn(name = "bestellung_id", nullable = false)
    private Bestellung bestellung;
    @OneToOne(optional = false)
    @JoinColumn(name = "wunschliste_id", nullable = false)
    private Wunschliste wunschliste;

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

    public Bestellung getBestellung() {
        return bestellung;
    }

    public void setBestellung(Bestellung bestellung) {
        this.bestellung = bestellung;
    }

    public Wunschliste getWunschliste() {
        return wunschliste;
    }

    public void setWunschliste(Wunschliste wunschliste) {
        this.wunschliste = wunschliste;
    }
}
