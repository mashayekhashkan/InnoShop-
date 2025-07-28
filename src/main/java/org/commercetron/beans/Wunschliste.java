package org.commercetron.beans;

import org.commercetron.beans.Products;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "wunschliste")
public class Wunschliste {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wunschliste_id", nullable = false, updatable = false)
    private UUID id;
    @OneToOne(mappedBy = "wunschliste")
    private User user;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "wunsch_liste",
            joinColumns = @JoinColumn(name = "Wunschliste"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Products> products = new HashSet<>();

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
