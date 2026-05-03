package com.pulpapp.ms_users.dto;

import lombok.Data;

/**
 * DTO para las acciones del admin sobre un pago (aprobar/rechazar).
 */
@Data
public class AdminPaymentActionDTO {

    /** Motivo de rechazo (obligatorio solo al rechazar). */
    private String rejectionReason;
}
