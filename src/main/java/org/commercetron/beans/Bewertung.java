package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "bewertung")
public class Bewertung {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bewertung_id", nullable = false, updatable = false)
    private UUID bewertungId;
    @OneToOne
    @JoinColumn(name = "customer_id")
    private User user;
    @OneToOne
    @JoinColumn(name = "products_id")
    private Products products;
    @Column(name = "rating", nullable = false)
    private int rating;
    @Column(name = "comment", nullable = false)
    private String comment;
}
