package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.AddUserToTenantRequestDTO;
import com.pulpapp.ms_users.dto.UserTenantRoleDTO;
import com.pulpapp.ms_users.entity.*;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.repository.UserRepository;
import com.pulpapp.ms_users.repository.UserTenantRoleRepository;
import com.pulpapp.ms_users.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de gestión de roles por tenant (RBAC Multi-Tenant).
 *
 * Responsabilidades:
 *  - Asignar/actualizar/eliminar roles de usuarios dentro de un tenant
 *  - Listar usuarios de un tenant
 *  - Crear usuarios dentro de un tenant (invitación por admin)
 *  - Resolver el rol de un usuario en un tenant específico
 *
 * Seguridad: todas las operaciones validan que el caller pertenezca al tenant.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTenantRoleService {

    private final UserTenantRoleRepository userTenantRoleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ---------------------------------------------------------------
    // Consultas
    // ---------------------------------------------------------------

    /**
     * Lista todos los usuarios del tenant actual con sus roles.
     */
    public List<UserTenantRoleDTO> findUsersInMyTenant() {
        Long tenantId = TenantContext.requireTenantId();
        return userTenantRoleRepository.findByTenantId(tenantId).stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Obtiene el rol de un usuario en un tenant específico.
     * Retorna null si el usuario no pertenece al tenant.
     */
    public TenantRole getRoleInTenant(Long userId, Long tenantId) {
        return userTenantRoleRepository.findByUserIdAndTenantId(userId, tenantId)
                .map(UserTenantRole::getRole)
                .orElse(null);
    }

    // ---------------------------------------------------------------
    // Asignación de roles
    // ---------------------------------------------------------------

    /**
     * Asigna un rol a un usuario en el tenant actual.
     * Si ya tiene un rol, lo actualiza.
     */
    @Transactional
    public UserTenantRoleDTO assignRole(Long userId, TenantRole role) {
        Long tenantId = TenantContext.requireTenantId();
        log.info("assignRole: userId={}, tenantId={}, role={}", userId, tenantId, role);

        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));

        UserTenantRole utr = userTenantRoleRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseGet(() -> {
                    UserTenantRole newUtr = new UserTenantRole();
                    newUtr.setUserId(userId);
                    newUtr.setTenantId(tenantId);
                    return newUtr;
                });

        utr.setRole(role);
        utr = userTenantRoleRepository.save(utr);

        // Sincronizar el campo legacy User.role para compatibilidad
        syncLegacyRole(user, role);

        return toDTO(utr);
    }

    /**
     * Actualiza el rol de un usuario en el tenant actual.
     */
    @Transactional
    public UserTenantRoleDTO updateRole(Long userId, TenantRole newRole) {
        Long tenantId = TenantContext.requireTenantId();
        log.info("updateRole: userId={}, tenantId={}, newRole={}", userId, tenantId, newRole);

        UserTenantRole utr = userTenantRoleRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El usuario no pertenece a este tenant"));

        utr.setRole(newRole);
        utr = userTenantRoleRepository.save(utr);

        // Sincronizar legacy
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) syncLegacyRole(user, newRole);

        return toDTO(utr);
    }

    // ---------------------------------------------------------------
    // Crear usuario dentro del tenant
    // ---------------------------------------------------------------

    /**
     * Crea un nuevo usuario y lo asigna al tenant actual con el rol indicado.
     * Usado por el admin del tenant para invitar miembros a su equipo.
     */
    @Transactional
    public UserTenantRoleDTO addUserToTenant(AddUserToTenantRequestDTO request) {
        Long tenantId = TenantContext.requireTenantId();
        log.info("addUserToTenant: email={}, tenantId={}, role={}", request.getEmail(), tenantId, request.getRole());

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (userRepository.existsByCedula(request.getCedula())) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        // Crear usuario
        User user = new User();
        user.setCedula(request.getCedula());
        user.setTelefono(request.getTelefono());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDireccion(request.getDireccion());
        user.setTenantId(tenantId);
        user.setStatus(UserStatus.ACTIVE);
        // Sincronizar legacy role
        user.setRole(toLegacyRole(request.getRole()));
        user = userRepository.save(user);

        // Crear asignación de rol en el tenant
        UserTenantRole utr = new UserTenantRole();
        utr.setUserId(user.getId());
        utr.setTenantId(tenantId);
        utr.setRole(request.getRole());
        utr = userTenantRoleRepository.save(utr);

        log.info("Usuario creado en tenant: userId={}, tenantId={}, role={}", user.getId(), tenantId, request.getRole());

        return toDTO(utr);
    }

    // ---------------------------------------------------------------
    // Eliminar usuario del tenant
    // ---------------------------------------------------------------

    @Transactional
    public void removeUserFromTenant(Long userId) {
        Long tenantId = TenantContext.requireTenantId();
        log.info("removeUserFromTenant: userId={}, tenantId={}", userId, tenantId);

        if (!userTenantRoleRepository.existsByUserIdAndTenantId(userId, tenantId)) {
            throw new ResourceNotFoundException("El usuario no pertenece a este tenant");
        }

        userTenantRoleRepository.deleteByUserIdAndTenantId(userId, tenantId);

        // Desasociar usuario del tenant
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && tenantId.equals(user.getTenantId())) {
            user.setTenantId(null);
            user.setStatus(UserStatus.SUSPENDED);
            userRepository.save(user);
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    /**
     * Sincroniza el campo legacy User.role con el TenantRole.
     * Mantiene compatibilidad con SecurityConfig que usa ROLE_ADMIN, ROLE_SELLER, etc.
     */
    private void syncLegacyRole(User user, TenantRole tenantRole) {
        user.setRole(toLegacyRole(tenantRole));
        userRepository.save(user);
    }

    private Role toLegacyRole(TenantRole tenantRole) {
        return switch (tenantRole) {
            case ADMIN -> Role.ROLE_ADMIN;
            case SELLER -> Role.ROLE_SELLER;
            case CLIENT -> Role.ROLE_CLIENT;
        };
    }

    private UserTenantRoleDTO toDTO(UserTenantRole utr) {
        User user = userRepository.findById(utr.getUserId()).orElse(null);
        return UserTenantRoleDTO.builder()
                .id(utr.getId())
                .userId(utr.getUserId())
                .tenantId(utr.getTenantId())
                .role(utr.getRole())
                .createdAt(utr.getCreatedAt())
                .userName(user != null ? user.getName() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .build();
    }
}
