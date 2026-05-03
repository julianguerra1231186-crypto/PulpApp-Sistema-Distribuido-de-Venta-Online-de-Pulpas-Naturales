package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.AdminPaymentActionDTO;
import com.pulpapp.ms_users.dto.PaymentResponseDTO;
import com.pulpapp.ms_users.security.UserPrincipal;
import com.pulpapp.ms_users.service.OnboardingPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints administrativos para validación de pagos del onboarding SaaS.
 * Solo accesible por ROLE_ADMIN (configurado en SecurityConfig).
 */
@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final OnboardingPaymentService paymentService;

    /**
     * GET /admin/payments — Lista pagos pendientes de aprobación.
     */
    @GetMapping
    public List<PaymentResponseDTO> getPendingPayments() {
        return paymentService.findPendingPayments();
    }

    /**
     * PUT /admin/payments/{id}/approve — Aprueba un pago.
     * Crea tenant, activa usuario, asigna ROLE_ADMIN del tenant.
     */
    @PutMapping("/{id}/approve")
    public PaymentResponseDTO approvePayment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return paymentService.approvePayment(id, principal.getUser().getId());
    }

    /**
     * PUT /admin/payments/{id}/reject — Rechaza un pago.
     */
    @PutMapping("/{id}/reject")
    public PaymentResponseDTO rejectPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) AdminPaymentActionDTO action) {
        return paymentService.rejectPayment(id, principal.getUser().getId(), action);
    }
}
