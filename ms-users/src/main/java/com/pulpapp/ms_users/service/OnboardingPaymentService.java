package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.AdminPaymentActionDTO;
import com.pulpapp.ms_users.dto.PaymentRequestDTO;
import com.pulpapp.ms_users.dto.PaymentResponseDTO;
import com.pulpapp.ms_users.entity.*;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.repository.PaymentRepository;
import com.pulpapp.ms_users.repository.TenantConfigRepository;
import com.pulpapp.ms_users.repository.TenantRepository;
import com.pulpapp.ms_users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de onboarding SaaS con pago manual.
 *
 * Flujo completo:
 *  1. Usuario se registra → status PENDING_PAYMENT, tenant_id NULL
 *  2. Sube comprobante → Payment PENDING, user status → PENDING_APPROVAL
 *  3. Admin aprueba → Crea Tenant, asigna tenant_id, user ACTIVE + ROLE_ADMIN
 *  4. Admin rechaza → Payment REJECTED, user vuelve a PENDING_PAYMENT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingPaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final TenantConfigRepository tenantConfigRepository;

    // ---------------------------------------------------------------
    // Subida de comprobante (usuario)
    // ---------------------------------------------------------------

    @Transactional
    public PaymentResponseDTO submitPayment(Long userId, PaymentRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setReference(request.getReference());
        payment.setProofUrl(request.getProofUrl());
        payment.setTenantName(request.getTenantName());
        payment.setStatus(PaymentStatus.PENDING);

        payment = paymentRepository.save(payment);

        // Actualizar estado del usuario a PENDING_APPROVAL
        user.setStatus(UserStatus.PENDING_APPROVAL);
        userRepository.save(user);

        log.info("Comprobante subido: paymentId={}, userId={}, method={}", payment.getId(), userId, request.getMethod());

        return toResponseDTO(payment, user);
    }

    // ---------------------------------------------------------------
    // Consultas
    // ---------------------------------------------------------------

    public List<PaymentResponseDTO> findPendingPayments() {
        return paymentRepository.findByStatus(PaymentStatus.PENDING).stream()
                .map(p -> {
                    User user = userRepository.findById(p.getUserId()).orElse(null);
                    return toResponseDTO(p, user);
                })
                .toList();
    }

    public List<PaymentResponseDTO> findByUserId(Long userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(p -> {
                    User user = userRepository.findById(p.getUserId()).orElse(null);
                    return toResponseDTO(p, user);
                })
                .toList();
    }

    // ---------------------------------------------------------------
    // Aprobación (admin)
    // ---------------------------------------------------------------

    @Transactional
    public PaymentResponseDTO approvePayment(Long paymentId, Long adminId) {
        Payment payment = findPayment(paymentId);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("El pago #" + paymentId + " no está en estado PENDING");
        }

        User user = userRepository.findById(payment.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 1. Crear tenant si el usuario no tiene uno
        if (user.getTenantId() == null) {
            String tenantName = payment.getTenantName() != null && !payment.getTenantName().isBlank()
                    ? payment.getTenantName()
                    : "Tenant de " + user.getName();

            // Evitar nombres duplicados
            String finalName = tenantName;
            int suffix = 1;
            while (tenantRepository.existsByNameIgnoreCase(finalName)) {
                finalName = tenantName + " " + suffix++;
            }

            Tenant tenant = new Tenant();
            tenant.setName(finalName);
            tenant.setStatus(TenantStatus.ACTIVE);
            tenant = tenantRepository.save(tenant);

            // Crear configuración inicial del tenant
            TenantConfig config = new TenantConfig();
            config.setTenantId(tenant.getId());
            config.setPrimaryColor("#4CAF50");
            config.setSecondaryColor("#FFFFFF");
            tenantConfigRepository.save(config);

            user.setTenantId(tenant.getId());
            log.info("Tenant creado: id={}, name={} para userId={}", tenant.getId(), finalName, user.getId());
        }

        // 2. Activar usuario y asignar ROLE_ADMIN del tenant
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(Role.ROLE_ADMIN);
        user.setActivo(true);
        userRepository.save(user);

        // 3. Actualizar payment
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setApprovedBy(adminId);
        payment.setApprovedAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        log.info("Pago #{} aprobado por adminId={}, userId={} activado con tenantId={}",
                paymentId, adminId, user.getId(), user.getTenantId());

        return toResponseDTO(payment, user);
    }

    // ---------------------------------------------------------------
    // Rechazo (admin)
    // ---------------------------------------------------------------

    @Transactional
    public PaymentResponseDTO rejectPayment(Long paymentId, Long adminId, AdminPaymentActionDTO action) {
        Payment payment = findPayment(paymentId);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("El pago #" + paymentId + " no está en estado PENDING");
        }

        User user = userRepository.findById(payment.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Rechazar payment
        payment.setStatus(PaymentStatus.REJECTED);
        payment.setApprovedBy(adminId);
        payment.setApprovedAt(LocalDateTime.now());
        payment.setRejectionReason(action != null ? action.getRejectionReason() : null);
        payment = paymentRepository.save(payment);

        // Usuario vuelve a PENDING_PAYMENT para que pueda reintentar
        user.setStatus(UserStatus.PENDING_PAYMENT);
        userRepository.save(user);

        log.info("Pago #{} rechazado por adminId={}, motivo: {}", paymentId, adminId,
                action != null ? action.getRejectionReason() : "sin motivo");

        return toResponseDTO(payment, user);
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Payment findPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
    }

    private PaymentResponseDTO toResponseDTO(Payment payment, User user) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .reference(payment.getReference())
                .proofUrl(payment.getProofUrl())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .approvedBy(payment.getApprovedBy())
                .approvedAt(payment.getApprovedAt())
                .rejectionReason(payment.getRejectionReason())
                .tenantName(payment.getTenantName())
                .userName(user != null ? user.getName() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .build();
    }
}
