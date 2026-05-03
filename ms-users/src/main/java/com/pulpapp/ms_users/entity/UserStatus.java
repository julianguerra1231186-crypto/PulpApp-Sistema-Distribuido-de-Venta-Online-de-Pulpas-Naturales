package com.pulpapp.ms_users.entity;

/**
 * Estados del ciclo de vida de un usuario en el flujo SaaS.
 *
 * PENDING_PAYMENT — Recién registrado, debe subir comprobante de pago.
 * PENDING_APPROVAL — Comprobante subido, admin debe validar.
 * ACTIVE — Pago aprobado, acceso completo al sistema.
 * SUSPENDED — Suspendido por el admin (puede reactivarse).
 */
public enum UserStatus {
    PENDING_PAYMENT,
    PENDING_APPROVAL,
    ACTIVE,
    SUSPENDED
}
