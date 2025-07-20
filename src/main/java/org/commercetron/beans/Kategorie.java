package org.commercetron.beans;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "kategorie")
public class Kategorie {
@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(name = "id")
private UUID kategorieId;
@Column(name = "name", nullable = false)
private String name;

    public UUID getKategorieId() {

        return kategorieId;
    }

    public void setKategorieId(UUID kategorieId) {
        this.kategorieId = kategorieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
