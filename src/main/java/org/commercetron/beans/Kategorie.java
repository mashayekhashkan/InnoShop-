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
}
