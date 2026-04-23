package com.pulpapp.msorders.controller;

import com.pulpapp.msorders.dto.ApprovePaymentRequestDTO;
import com.pulpapp.msorders.dto.PaymentStatusDTO;
import com.pulpapp.msorders.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de gestión de pagos por transferencia.
 *
 * Seguridad (aplicada en SecurityConfig de ms-users via API Gateway):
 * - POST /orders/{id}/pay         → público (el cliente marca "Ya pagué")
 * - GET  /orders/admin/payments   → ROLE_ADMIN (ver pendientes)
 * - PUT  /orders/{id}/approve     → ROLE_ADMIN
 * - PUT  /orders/{id}/reject      → ROLE_ADMIN
 * - GET  /orders/{id}/payment     → ROLE_ADMIN, ROLE_SELLER
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /orders/{id}/pay
     * El cliente confirma que realizó la transferencia.
     * Cambia el estado a PENDING_APPROVAL para que el admin valide.
     */
    @PostMapping("/{id}/pay")
    public PaymentStatusDTO markAsPaid(@PathVariable Long id) {
        return paymentService.markAsPaid(id);
    }

    /**
     * GET /orders/admin/payments
     * Lista todos los pedidos en estado PENDING_APPROVAL.
     * Solo ROLE_ADMIN.
     */
    @GetMapping("/admin/payments")
    public List<PaymentStatusDTO> getPendingPayments() {
        return paymentService.findPendingApproval();
    }

    /**
     * GET /orders/{id}/payment
     * Consulta el estado de pago de un pedido específico.
     */
    @GetMapping("/{id}/payment")
    public PaymentStatusDTO getPaymentStatus(@PathVariable Long id) {
        return paymentService.getPaymentStatus(id);
    }

    /**
     * PUT /orders/{id}/approve
     * El admin aprueba el pago manualmente.
     * Registra el email del admin y el timestamp (auditoría).
     */
    @PutMapping("/{id}/approve")
    public PaymentStatusDTO approvePayment(
            @PathVariable Long id,
            @Valid @RequestBody ApprovePaymentRequestDTO request) {
        return paymentService.approvePayment(id, request);
    }

    /**
     * PUT /orders/{id}/reject
     * El admin rechaza el pago.
     */
    @PutMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public PaymentStatusDTO rejectPayment(
            @PathVariable Long id,
            @Valid @RequestBody ApprovePaymentRequestDTO request) {
        return paymentService.rejectPayment(id, request);
    }
}
