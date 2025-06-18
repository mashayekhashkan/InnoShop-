package org.commercetron.beans;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "admin_id", nullable = false, updatable = false)
    private UUID adminId;
    @Column(name = "admin_name", nullable = false, unique = true)
    private String name;
    @NotBlank
    @Size(min = 8)
    @Column(name = "admin_password", nullable = false, length = 225)
    private String password;
}
