package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.CashMovementRequestDTO;
import com.pulpapp.ms_users.dto.CashMovementResponseDTO;
import com.pulpapp.ms_users.entity.CashMovement;
import com.pulpapp.ms_users.repository.CashMovementRepository;
import com.pulpapp.ms_users.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashMovementService {

    private final CashMovementRepository repository;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    public List<CashMovementResponseDTO> findAll() {
        return repository.findByTenantIdOrderByCreatedAtDesc(resolveTenantId()).stream().map(this::toDTO).toList();
    }

    public List<CashMovementResponseDTO> findToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return repository.findByTenantIdAndCreatedAtAfterOrderByCreatedAtDesc(resolveTenantId(), startOfDay).stream().map(this::toDTO).toList();
    }

    public Map<String, Object> getSummary() {
        Long tenantId = resolveTenantId();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        BigDecimal income = repository.sumByTypeAndDate(tenantId, "INGRESO", startOfDay);
        BigDecimal expense = repository.sumByTypeAndDate(tenantId, "GASTO", startOfDay);
        BigDecimal balance = income.subtract(expense);
        return Map.of("income", income, "expense", expense, "balance", balance);
    }

    @Transactional
    public CashMovementResponseDTO create(CashMovementRequestDTO request) {
        Long tenantId = resolveTenantId();
        CashMovement m = new CashMovement();
        m.setTenantId(tenantId);
        m.setMovementType(request.getMovementType().toUpperCase());
        m.setDescription(request.getDescription());
        m.setAmount(request.getAmount());
        m.setPaymentMethod(request.getPaymentMethod());
        m.setObservations(request.getObservations());
        m = repository.save(m);
        log.info("Movimiento creado: type={}, amount={}, tenantId={}", m.getMovementType(), m.getAmount(), tenantId);
        return toDTO(m);
    }

    private Long resolveTenantId() {
        Long t = TenantContext.getTenantId();
        return t != null ? t : defaultTenantId;
    }

    private CashMovementResponseDTO toDTO(CashMovement m) {
        return CashMovementResponseDTO.builder()
                .id(m.getId()).movementType(m.getMovementType()).description(m.getDescription())
                .amount(m.getAmount()).paymentMethod(m.getPaymentMethod())
                .observations(m.getObservations()).createdAt(m.getCreatedAt()).build();
    }
}
