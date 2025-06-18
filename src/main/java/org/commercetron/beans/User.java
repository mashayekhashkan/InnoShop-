package org.commercetron.beans;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;
@Entity
@Data
@Table(name = "user")
public class User {

@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(name = "customer_id", nullable = false, updatable = false)
private UUID customerId;
@Column(name = "name", nullable = false)
private String customerName;
@Column(name = "adresse", nullable = false)
private String adresse;
@Column(name = "email", nullable = false, unique = true)
private String email;
@Column(name = "password", nullable = false, length = 225)
@NotBlank
@Size(min = 8)
private String password;
}
