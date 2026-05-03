package com.pulpapp.msorders.service;

import com.pulpapp.msorders.dto.ApprovePaymentRequestDTO;
import com.pulpapp.msorders.dto.PaymentStatusDTO;
import com.pulpapp.msorders.entity.Order;
import com.pulpapp.msorders.exception.ResourceNotFoundException;
import com.pulpapp.msorders.repository.OrderRepository;
import com.pulpapp.msorders.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de pagos por transferencia con aislamiento Multi-Tenant (Fase 3).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    public static final String PENDING_PAYMENT  = "PENDING_PAYMENT";
    public static final String PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String APPROVED         = "APPROVED";
    public static final String REJECTED         = "REJECTED";

    @Transactional
    public PaymentStatusDTO markAsPaid(Long orderId) {
        Order order = findOrderByTenant(orderId);

        if (!PENDING_PAYMENT.equals(order.getPaymentStatus())) {
            throw new IllegalStateException(
                "El pedido #" + orderId + " no está en estado PENDING_PAYMENT. Estado actual: " + order.getPaymentStatus());
        }

        order.setPaymentStatus(PENDING_APPROVAL);
        order.setPaidAt(LocalDateTime.now());

        return toDTO(orderRepository.save(order));
    }

    @Transactional
    public PaymentStatusDTO approvePayment(Long orderId, ApprovePaymentRequestDTO request) {
        Order order = findOrderByTenant(orderId);

        if (!PENDING_APPROVAL.equals(order.getPaymentStatus())) {
            throw new IllegalStateException(
                "El pedido #" + orderId + " no está en estado PENDING_APPROVAL. Estado actual: " + order.getPaymentStatus());
        }

        order.setPaymentStatus(APPROVED);
        order.setApprovedBy(request.getAdminEmail());
        order.setApprovedAt(LocalDateTime.now());

        log.info("Pedido #{} aprobado por {} (tenantId={})", orderId, request.getAdminEmail(), order.getTenantId());
        return toDTO(orderRepository.save(order));
    }

    @Transactional
    public PaymentStatusDTO rejectPayment(Long orderId, ApprovePaymentRequestDTO request) {
        Order order = findOrderByTenant(orderId);

        if (!PENDING_APPROVAL.equals(order.getPaymentStatus())) {
            throw new IllegalStateException(
                "El pedido #" + orderId + " no está en estado PENDING_APPROVAL. Estado actual: " + order.getPaymentStatus());
        }

        order.setPaymentStatus(REJECTED);
        order.setApprovedBy(request.getAdminEmail());
        order.setApprovedAt(LocalDateTime.now());
        if (request.getRejectionReason() != null && !request.getRejectionReason().isBlank()) {
            order.setRejectionReason(request.getRejectionReason());
        }

        log.info("Pedido #{} rechazado por {} — motivo: {} (tenantId={})",
                orderId, request.getAdminEmail(), request.getRejectionReason(), order.getTenantId());
        return toDTO(orderRepository.save(order));
    }

    public List<PaymentStatusDTO> findPendingApproval() {
        Long tenantId = resolveTenantId();
        return orderRepository.findAllByTenantId(tenantId).stream()
                .filter(o -> PENDING_APPROVAL.equals(o.getPaymentStatus()))
                .map(this::toDTO)
                .toList();
    }

    public PaymentStatusDTO getPaymentStatus(Long orderId) {
        return toDTO(findOrderByTenant(orderId));
    }

    // ── Helpers ───────────────────────────────────────────────────

    private Order findOrderByTenant(Long id) {
        Long tenantId = resolveTenantId();
        return orderRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private Long resolveTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) return tenantId;
        log.debug("No hay tenantId en contexto, usando default: {}", defaultTenantId);
        return defaultTenantId;
    }

    private PaymentStatusDTO toDTO(Order order) {
        PaymentStatusDTO dto = new PaymentStatusDTO();
        dto.setOrderId(order.getId());
        dto.setTotal(order.getTotal());
        dto.setUniqueAmount(order.getUniqueAmount());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setApprovedBy(order.getApprovedBy());
        dto.setPaidAt(order.getPaidAt());
        dto.setApprovedAt(order.getApprovedAt());
        dto.setRejectionReason(order.getRejectionReason());
        return dto;
    }
}
