package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

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
    @ManyToOne(optional = false)
    @JoinColumn(name = "products_id")
    private Products products;
}
