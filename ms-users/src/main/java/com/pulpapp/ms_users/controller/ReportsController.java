package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.repository.CashMovementRepository;
import com.pulpapp.ms_users.repository.InvoiceRepository;
import com.pulpapp.ms_users.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final InvoiceRepository invoiceRepository;
    private final CashMovementRepository cashRepository;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    @GetMapping("/summary")
    public Map<String, Object> getSummary(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        Long tenantId = TenantContext.getTenantId() != null ? TenantContext.getTenantId() : defaultTenantId;
        LocalDateTime start = from != null ? LocalDate.parse(from).atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = to != null ? LocalDate.parse(to).atTime(23, 59, 59) : LocalDateTime.now();

        BigDecimal totalSales = cashRepository.sumByTypeAndDate(tenantId, "INGRESO", start);
        BigDecimal totalExpenses = cashRepository.sumByTypeAndDate(tenantId, "GASTO", start);
        BigDecimal profit = totalSales.subtract(totalExpenses);
        long invoiceCount = invoiceRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .filter(i -> i.getCreatedAt().isAfter(start) && i.getCreatedAt().isBefore(end)).count();

        return Map.of(
                "totalSales", totalSales,
                "totalExpenses", totalExpenses,
                "grossProfit", profit,
                "invoiceCount", invoiceCount,
                "from", start.toLocalDate().toString(),
                "to", end.toLocalDate().toString()
        );
    }
}
