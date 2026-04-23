package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.NotificationRequestDTO;
import com.pulpapp.ms_users.dto.NotificationResponseDTO;
import com.pulpapp.ms_users.entity.Notification;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de lógica de negocio para notificaciones del sistema.
 *
 * Responsabilidades:
 * - Crear notificaciones (solo admin)
 * - Listar notificaciones activas (público — para el frontend)
 * - Listar todas las notificaciones (admin)
 * - Desactivar notificaciones (admin)
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    /**
     * Crea una nueva notificación activa.
     *
     * @param dto DTO con título y mensaje
     * @return DTO de la notificación creada
     */
    public NotificationResponseDTO create(NotificationRequestDTO dto) {
        Notification entity = new Notification();
        entity.setTitle(dto.getTitle());
        entity.setMessage(dto.getMessage());
        entity.setActive(true);

        return toResponseDto(repository.save(entity));
    }

    /**
     * Retorna solo las notificaciones activas, ordenadas por fecha descendente.
     * Endpoint público — cualquier usuario puede consultarlas.
     *
     * @return lista de notificaciones activas
     */
    public List<NotificationResponseDTO> findActive() {
        return repository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Retorna todas las notificaciones (activas e inactivas).
     * Solo para el panel admin.
     *
     * @return lista completa de notificaciones
     */
    public List<NotificationResponseDTO> findAll() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Desactiva una notificación sin eliminarla (soft delete).
     * Permite mantener historial de notificaciones pasadas.
     *
     * @param id identificador de la notificación
     * @return DTO de la notificación desactivada
     */
    public NotificationResponseDTO deactivate(Long id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + id));

        notification.setActive(false);
        return toResponseDto(repository.save(notification));
    }

    // ── Mapeo privado ──────────────────────────────────────────────────────

    private NotificationResponseDTO toResponseDto(Notification entity) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
