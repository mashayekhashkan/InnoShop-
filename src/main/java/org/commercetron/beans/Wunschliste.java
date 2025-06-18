package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "wunschliste")
public class Wunschliste {
    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private UUID customerId;
    @ManyToMany
    @JoinColumn(name = "products_id")
    private UUID productsId;
}
