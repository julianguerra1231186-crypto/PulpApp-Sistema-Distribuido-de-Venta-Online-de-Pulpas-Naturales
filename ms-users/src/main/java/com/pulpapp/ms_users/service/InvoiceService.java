package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.CreateInvoiceRequestDTO;
import com.pulpapp.ms_users.dto.InvoiceItemDTO;
import com.pulpapp.ms_users.dto.InvoiceResponseDTO;
import com.pulpapp.ms_users.entity.Invoice;
import com.pulpapp.ms_users.entity.InvoiceItem;
import com.pulpapp.ms_users.entity.InventoryItem;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.repository.InvoiceRepository;
import com.pulpapp.ms_users.repository.InventoryItemRepository;
import com.pulpapp.ms_users.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InventoryItemRepository inventoryRepository;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    public List<InvoiceResponseDTO> findAll() {
        return invoiceRepository.findByTenantIdOrderByCreatedAtDesc(resolveTenantId()).stream().map(this::toDTO).toList();
    }

    public InvoiceResponseDTO findById(Long id) {
        Invoice inv = invoiceRepository.findByIdAndTenantId(id, resolveTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id: " + id));
        return toDTO(inv);
    }

    @Transactional
    public InvoiceResponseDTO create(CreateInvoiceRequestDTO request) {
        Long tenantId = resolveTenantId();

        Invoice invoice = new Invoice();
        invoice.setTenantId(tenantId);
        invoice.setInvoiceNumber(generateInvoiceNumber(tenantId));
        invoice.setClientId(request.getClientId());
        invoice.setClientName(request.getClientName());
        invoice.setPaymentMethod(request.getPaymentMethod());
        invoice.setObservations(request.getObservations());
        invoice.setDiscount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO);

        if (request.getDueDate() != null && !request.getDueDate().isBlank()) {
            invoice.setDueDate(LocalDateTime.parse(request.getDueDate() + "T00:00:00"));
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        for (InvoiceItemDTO itemDTO : request.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setProductId(itemDTO.getProductId());
            item.setProductCode(itemDTO.getProductCode());
            item.setProductName(itemDTO.getProductName());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setSubtotal(itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            invoice.addItem(item);
            subtotal = subtotal.add(item.getSubtotal());

            // Descontar stock del inventario
            if (itemDTO.getProductId() != null) {
                InventoryItem invItem = inventoryRepository.findByIdAndTenantId(itemDTO.getProductId(), tenantId).orElse(null);
                if (invItem != null) {
                    invItem.setStock(Math.max(0, invItem.getStock() - itemDTO.getQuantity()));
                    inventoryRepository.save(invItem);
                }
            }
        }

        invoice.setSubtotal(subtotal);
        BigDecimal discount = invoice.getDiscount() != null ? invoice.getDiscount() : BigDecimal.ZERO;
        invoice.setTotal(subtotal.subtract(discount));

        invoice = invoiceRepository.save(invoice);
        log.info("Factura creada: number={}, total={}, tenantId={}", invoice.getInvoiceNumber(), invoice.getTotal(), tenantId);
        return toDTO(invoice);
    }

    private String generateInvoiceNumber(Long tenantId) {
        Long maxId = invoiceRepository.findMaxIdByTenantId(tenantId);
        long next = maxId + 1;
        return String.format("FAC-%d-%06d", Year.now().getValue(), next);
    }

    private Long resolveTenantId() {
        Long t = TenantContext.getTenantId();
        return t != null ? t : defaultTenantId;
    }

    private InvoiceResponseDTO toDTO(Invoice inv) {
        List<InvoiceResponseDTO.InvoiceItemResponseDTO> items = inv.getItems().stream().map(i ->
            InvoiceResponseDTO.InvoiceItemResponseDTO.builder()
                .id(i.getId()).productId(i.getProductId()).productCode(i.getProductCode())
                .productName(i.getProductName()).quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice()).subtotal(i.getSubtotal()).build()
        ).toList();

        return InvoiceResponseDTO.builder()
                .id(inv.getId()).invoiceNumber(inv.getInvoiceNumber())
                .clientId(inv.getClientId()).clientName(inv.getClientName())
                .subtotal(inv.getSubtotal()).discount(inv.getDiscount()).total(inv.getTotal())
                .paymentMethod(inv.getPaymentMethod()).dueDate(inv.getDueDate())
                .observations(inv.getObservations()).status(inv.getStatus())
                .createdAt(inv.getCreatedAt()).items(items).build();
    }
}
