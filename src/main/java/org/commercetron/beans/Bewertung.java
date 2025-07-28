package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;
import java.util.UUID;

@Entity
@Data
@Table(name = "bewertung")
public class Bewertung {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bewertung_id", nullable = false, updatable = false)
    private UUID bewertungId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "products_id")
    private Products products;
    @Column(name = "rating", nullable = false)
    private String rating;
    @Column(name = "comment", nullable = false)
    private String comment;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Bewertung bewertung = (Bewertung) o;
        return Objects.equals(bewertungId, bewertung.bewertungId) && Objects.equals(user, bewertung.user) && Objects.equals(products, bewertung.products) && Objects.equals(rating, bewertung.rating) && Objects.equals(comment, bewertung.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bewertungId, user, products, rating, comment);
    }
}
