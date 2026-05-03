package com.pulpapp.msorders.service;

import com.pulpapp.msorders.dto.ApprovePaymentRequestDTO;
import com.pulpapp.msorders.dto.PaymentStatusDTO;
import com.pulpapp.msorders.entity.Order;
import com.pulpapp.msorders.exception.ResourceNotFoundException;
import com.pulpapp.msorders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de lógica de negocio para pagos por transferencia.
 *
 * Flujo:
 * 1. Al crear el pedido, se asigna un uniqueAmount (total + centavos aleatorios).
 * 2. El cliente transfiere ese monto exacto y presiona "Ya pagué" → PENDING_APPROVAL.
 * 3. El admin valida en su banco y aprueba → APPROVED (o rechaza → REJECTED).
 *
 * Auditoría: se registra quién aprobó y cuándo.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;

    // ── Constantes de estado ───────────────────────────────────────
    public static final String PENDING_PAYMENT  = "PENDING_PAYMENT";
    public static final String PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String APPROVED         = "APPROVED";
    public static final String REJECTED         = "REJECTED";

    /**
     * El cliente marca el pedido como pagado.
     * Cambia el estado de PENDING_PAYMENT → PENDING_APPROVAL.
     *
     * @param orderId ID del pedido
     * @return DTO con el nuevo estado
     */
    @Transactional
    public PaymentStatusDTO markAsPaid(Long orderId) {
        Order order = findOrder(orderId);

        if (!PENDING_PAYMENT.equals(order.getPaymentStatus())) {
            throw new IllegalStateException(
                "El pedido #" + orderId + " no está en estado PENDING_PAYMENT. Estado actual: " + order.getPaymentStatus());
        }

        order.setPaymentStatus(PENDING_APPROVAL);
        order.setPaidAt(LocalDateTime.now());

        return toDTO(orderRepository.save(order));
    }

    /**
     * El admin aprueba el pago manualmente.
     * Cambia el estado de PENDING_APPROVAL → APPROVED.
     * Registra el email del admin y el timestamp de aprobación (auditoría).
     *
     * @param orderId ID del pedido
     * @param request DTO con el email del admin
     * @return DTO con el nuevo estado
     */
    @Transactional
    public PaymentStatusDTO approvePayment(Long orderId, ApprovePaymentRequestDTO request) {
        Order order = findOrder(orderId);

        if (!PENDING_APPROVAL.equals(order.getPaymentStatus())) {
            throw new IllegalStateException(
                "El pedido #" + orderId + " no está en estado PENDING_APPROVAL. Estado actual: " + order.getPaymentStatus());
        }

        order.setPaymentStatus(APPROVED);
        order.setApprovedBy(request.getAdminEmail());
        order.setApprovedAt(LocalDateTime.now());

        log.info("Pedido #{} aprobado por {}", orderId, request.getAdminEmail());
        return toDTO(orderRepository.save(order));
    }

    /**
     * El admin rechaza el pago.
     * Cambia el estado de PENDING_APPROVAL → REJECTED.
     *
     * @param orderId ID del pedido
     * @param request DTO con el email del admin
     * @return DTO con el nuevo estado
     */
    @Transactional
    public PaymentStatusDTO rejectPayment(Long orderId, ApprovePaymentRequestDTO request) {
        Order order = findOrder(orderId);

        if (!PENDING_APPROVAL.equals(order.getPaymentStatus())) {
            throw new IllegalStateException(
                "El pedido #" + orderId + " no está en estado PENDING_APPROVAL. Estado actual: " + order.getPaymentStatus());
        }

        order.setPaymentStatus(REJECTED);
        order.setApprovedBy(request.getAdminEmail());
        order.setApprovedAt(LocalDateTime.now());
        // Guardar el motivo de rechazo para que el cliente lo vea
        if (request.getRejectionReason() != null && !request.getRejectionReason().isBlank()) {
            order.setRejectionReason(request.getRejectionReason());
        }

        log.info("Pedido #{} rechazado por {} — motivo: {}", orderId, request.getAdminEmail(), request.getRejectionReason());
        return toDTO(orderRepository.save(order));
    }

    /**
     * Retorna todos los pedidos en estado PENDING_APPROVAL para el dashboard admin.
     *
     * @return lista de pedidos pendientes de aprobación
     */
    public List<PaymentStatusDTO> findPendingApproval() {
        return orderRepository.findAll().stream()
                .filter(o -> PENDING_APPROVAL.equals(o.getPaymentStatus()))
                .map(this::toDTO)
                .toList();
    }

    /**
     * Retorna el estado de pago de un pedido específico.
     *
     * @param orderId ID del pedido
     * @return DTO con el estado de pago
     */
    public PaymentStatusDTO getPaymentStatus(Long orderId) {
        return toDTO(findOrder(orderId));
    }

    // ── Helpers ───────────────────────────────────────────────────

    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
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
        return dto;
    }
}
