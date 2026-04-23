package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.NotificationRequestDTO;
import com.pulpapp.ms_users.dto.NotificationResponseDTO;
import com.pulpapp.ms_users.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de gestión y consulta de notificaciones del sistema.
 *
 * Seguridad (configurada en SecurityConfig):
 * - POST /admin/notifications → solo ROLE_ADMIN
 * - GET  /admin/notifications → solo ROLE_ADMIN (vista completa con inactivas)
 * - GET  /notifications       → público (solo activas, para el frontend)
 * - DELETE /admin/notifications/{id} → solo ROLE_ADMIN (desactiva, no elimina)
 */
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * POST /admin/notifications
     *
     * Crea una nueva notificación activa.
     * Acceso: ROLE_ADMIN
     */
    @PostMapping("/admin/notifications")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponseDTO create(@Valid @RequestBody NotificationRequestDTO dto) {
        return notificationService.create(dto);
    }

    /**
     * GET /admin/notifications
     *
     * Retorna todas las notificaciones (activas e inactivas) para el panel admin.
     * Acceso: ROLE_ADMIN
     */
    @GetMapping("/admin/notifications")
    public List<NotificationResponseDTO> getAllForAdmin() {
        return notificationService.findAll();
    }

    /**
     * GET /notifications
     *
     * Retorna solo las notificaciones activas para mostrar en el frontend.
     * Acceso: público (sin JWT)
     */
    @GetMapping("/notifications")
    public List<NotificationResponseDTO> getActive() {
        return notificationService.findActive();
    }

    /**
     * DELETE /admin/notifications/{id}
     *
     * Desactiva una notificación (soft delete — no la elimina de la BD).
     * Acceso: ROLE_ADMIN
     */
    @DeleteMapping("/admin/notifications/{id}")
    public NotificationResponseDTO deactivate(@PathVariable Long id) {
        return notificationService.deactivate(id);
    }
}
