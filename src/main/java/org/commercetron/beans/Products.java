package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Data
@Table(name = "products")
public class Products {
@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(name = "products_id", nullable = false, updatable = false)
private UUID productsId;
@Column(name = "name", nullable = false)
private String productsName;
@ManyToOne
@JoinColumn(name = "kategorie_id", nullable = false)
private Kategorie kategorie;
@Column(name = "bestand", nullable = false)
private int bestand;
@Column(name = "status", nullable = false)
private String status;
@Column(name = "preis", nullable = false)
private double preis;
@Lob
@Column(name = "image", columnDefinition = "bytea")
private byte[] image;
}
