package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación creada por el administrador.
 *
 * Las notificaciones activas se muestran en el frontend (navbar/banner).
 * El admin puede desactivarlas sin eliminarlas para mantener historial.
 */
@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título corto de la notificación. Ej: "¡Nuevos productos disponibles!". */
    @Column(nullable = false, length = 150)
    private String title;

    /** Cuerpo del mensaje. Puede contener texto largo. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Indica si la notificación está activa y debe mostrarse en el frontend.
     * El admin puede desactivarla sin eliminarla.
     */
    @Column(nullable = false)
    private Boolean active = true;

    /** Fecha y hora de creación. Se asigna automáticamente al persistir. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
