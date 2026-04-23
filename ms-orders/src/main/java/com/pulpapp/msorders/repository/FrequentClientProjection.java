package com.pulpapp.msorders.repository;

/**
 * Proyección JPA para la consulta de clientes frecuentes.
 *
 * Spring Data JPA mapea automáticamente los alias de la query JPQL
 * a los métodos de esta interfaz.
 *
 * Separación de responsabilidades: la proyección solo expone los datos
 * que la query necesita — no expone la entidad Order completa.
 */
public interface FrequentClientProjection {

    /** ID del usuario (userId de la entidad Order). */
    Long getUserId();

    /** Número de pedidos realizados por este usuario. */
    Long getOrderCount();

    /** Suma total gastada por este usuario. */
    Double getTotalSpent();
}
