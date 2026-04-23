package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para notificaciones del sistema.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Retorna solo las notificaciones activas, ordenadas por fecha descendente.
     * Usado por el endpoint público GET /notifications.
     */
    List<Notification> findByActiveTrueOrderByCreatedAtDesc();

    /**
     * Retorna todas las notificaciones (activas e inactivas), ordenadas por fecha descendente.
     * Usado por el panel admin.
     */
    List<Notification> findAllByOrderByCreatedAtDesc();
}
