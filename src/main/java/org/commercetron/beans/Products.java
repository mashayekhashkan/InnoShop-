package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.sql.Blob;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;


@Entity
//@Data
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
@JdbcTypeCode(Types.BINARY)
@Basic(fetch = FetchType.EAGER)
@Column(name = "image", columnDefinition = "bytea")
private byte[] image;

    public UUID getProductsId() {
        return productsId;
    }

    public void setProductsId(UUID productsId) {
        this.productsId = productsId;
    }

    public String getProductsName() {
        return productsName;
    }

    public void setProductsName(String productsName) {
        this.productsName = productsName;
    }

    public Kategorie getKategorie() {
        return kategorie;
    }

    public void setKategorie(Kategorie kategorie) {
        this.kategorie = kategorie;
    }

    public int getBestand() {
        return bestand;
    }

    public void setBestand(int bestand) {
        this.bestand = bestand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Products products = (Products) o;
        return bestand == products.bestand && Double.compare(preis, products.preis) == 0 && Objects.equals(productsId, products.productsId) && Objects.equals(productsName, products.productsName) && Objects.equals(kategorie, products.kategorie) && Objects.equals(status, products.status) && Objects.deepEquals(image, products.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productsId, productsName, kategorie, bestand, status, preis, Arrays.hashCode(image));
    }
}
