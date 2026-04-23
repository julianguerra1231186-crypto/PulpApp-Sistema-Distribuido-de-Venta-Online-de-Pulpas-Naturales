package com.pulpapp.msorders.dto;

import lombok.Data;

/**
 * DTO de salida para la vista de clientes frecuentes.
 *
 * Agrupa pedidos por userId y enriquece con datos del cliente
 * resueltos desde ms-users (nombre y email).
 *
 * Ordenado por orderCount descendente — los clientes más activos primero.
 */
@Data
public class FrequentClientDTO {

    /** ID del usuario en ms-users. */
    private Long userId;

    /**
     * Nombre del cliente resuelto desde ms-users.
     * Fallback: "Cliente #userId" si ms-users no responde.
     */
    private String clientName;

    /**
     * Email del cliente resuelto desde ms-users.
     * Fallback: cadena vacía si ms-users no responde.
     */
    private String clientEmail;

    /** Número total de pedidos realizados por este cliente. */
    private Long orderCount;

    /** Suma total gastada por el cliente en todos sus pedidos. */
    private Double totalSpent;
}
