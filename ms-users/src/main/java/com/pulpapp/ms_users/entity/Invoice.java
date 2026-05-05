package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Invoice — Factura de venta del negocio.
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    /** Número de factura auto-generado: FAC-2026-000001 */
    @Column(name = "invoice_number", nullable = false, unique = true, length = 30)
    private String invoiceNumber;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "client_name", length = 150)
    private String clientName;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    /** Porcentaje o monto de descuento */
    @Column(precision = 12, scale = 2)
    private BigDecimal discount;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal total;

    /** EFECTIVO, TRANSFERENCIA, CREDITO */
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    /** Para crédito: fecha de vencimiento */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(columnDefinition = "TEXT")
    private String observations;

    /** REGISTRADA, PAGADA, ANULADA */
    @Column(nullable = false, length = 20)
    private String status = "REGISTRADA";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "REGISTRADA";
    }

    public void addItem(InvoiceItem item) {
        item.setInvoice(this);
        items.add(item);
    }
}
