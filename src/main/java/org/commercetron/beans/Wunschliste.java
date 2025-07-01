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
    @OneToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User user;
    @ManyToMany
    @JoinTable(
            name = "wunsch_liste",
            joinColumns = @JoinColumn(name = "Wunschliste"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Products> products = new HashSet<>();
}
