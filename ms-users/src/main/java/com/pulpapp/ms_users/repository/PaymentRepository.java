package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.Payment;
import com.pulpapp.ms_users.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByUserId(Long userId);

    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
}
