package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.CashMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CashMovementRepository extends JpaRepository<CashMovement, Long> {

    List<CashMovement> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    List<CashMovement> findByTenantIdAndCreatedAtAfterOrderByCreatedAtDesc(Long tenantId, LocalDateTime after);

    @Query("SELECT COALESCE(SUM(m.amount), 0) FROM CashMovement m WHERE m.tenantId = :tid AND m.movementType = :type AND m.createdAt > :after")
    BigDecimal sumByTypeAndDate(@Param("tid") Long tenantId, @Param("type") String type, @Param("after") LocalDateTime after);
}
