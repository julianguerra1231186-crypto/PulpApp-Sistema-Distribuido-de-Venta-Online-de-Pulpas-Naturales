package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cedula;

    // Se deja nullable a nivel de esquema para permitir la migracion sobre registros
    // historicos ya existentes. Los usuarios nuevos siguen obligados a enviar telefono
    // porque UserRequestDTO lo valida como campo requerido.
    @Column
    private String telefono;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String direccion;

}
