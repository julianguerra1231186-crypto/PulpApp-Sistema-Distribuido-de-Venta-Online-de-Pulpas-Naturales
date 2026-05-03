package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.PaymentRequestDTO;
import com.pulpapp.ms_users.dto.PaymentResponseDTO;
import com.pulpapp.ms_users.security.UserPrincipal;
import com.pulpapp.ms_users.service.OnboardingPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de pago para el onboarding SaaS.
 * El usuario autenticado sube su comprobante de pago.
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final OnboardingPaymentService paymentService;

    /**
     * POST /payments — Usuario sube comprobante de pago.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDTO submitPayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PaymentRequestDTO request) {
        return paymentService.submitPayment(principal.getUser().getId(), request);
    }

    /**
     * GET /payments/my — Historial de pagos del usuario autenticado.
     */
    @GetMapping("/my")
    public List<PaymentResponseDTO> getMyPayments(
            @AuthenticationPrincipal UserPrincipal principal) {
        return paymentService.findByUserId(principal.getUser().getId());
    }
}
