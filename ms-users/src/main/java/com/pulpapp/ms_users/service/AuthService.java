package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.AuthResponseDTO;
import com.pulpapp.ms_users.dto.LoginRequestDTO;
import com.pulpapp.ms_users.dto.RegisterRequestDTO;
import com.pulpapp.ms_users.entity.Role;
import com.pulpapp.ms_users.entity.Tenant;
import com.pulpapp.ms_users.entity.TenantRole;
import com.pulpapp.ms_users.entity.TenantStatus;
import com.pulpapp.ms_users.entity.User;
import com.pulpapp.ms_users.entity.UserStatus;
import com.pulpapp.ms_users.exception.BadCredentialsException;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.repository.TenantRepository;
import com.pulpapp.ms_users.repository.UserRepository;
import com.pulpapp.ms_users.security.JwtService;
import com.pulpapp.ms_users.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio de autenticación: login y registro de usuarios.
 *
 * Fase 1 Multi-Tenant:
 *  - El registro asigna el tenant por defecto al nuevo usuario.
 *  - El login incluye tenantId en el JWT.
 *  - Compatibilidad total: usuarios sin tenantId siguen funcionando.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TenantService tenantService;
    private final UserTenantRoleService userTenantRoleService;

    // ---------------------------------------------------------------
    // Login
    // ---------------------------------------------------------------

    /**
     * Autentica al usuario con email + password y devuelve un JWT.
     *
     * Fase 5 RBAC: El rol se resuelve desde UserTenantRole (fuente de verdad).
     * Si no hay registro en user_tenant_roles, usa el campo legacy User.role.
     */
    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Email o contraseña incorrectos");
        }

        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email o contraseña incorrectos"));

        // Fase 5: resolver rol desde user_tenant_roles
        String resolvedRole = resolveRole(user);

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal, resolvedRole, user.getTenantId());

        log.info("Login exitoso: email={}, tenantId={}, role={}", user.getEmail(), user.getTenantId(), resolvedRole);

        // Update last login timestamp
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return buildAuthResponse(token, user, resolvedRole);
    }

    // ---------------------------------------------------------------
    // Registro
    // ---------------------------------------------------------------

    /**
     * Registra un nuevo usuario en el flujo SaaS.
     *
     * Si el usuario envía businessName, se crea un nuevo tenant automáticamente
     * y se asocia al usuario. El tenant queda ACTIVE inmediatamente.
     */
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (userRepository.existsByCedula(request.getCedula())) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        // Crear tenant si se proporcionó nombre de negocio
        Long tenantId = null;
        if (request.getBusinessName() != null && !request.getBusinessName().isBlank()) {
            Tenant tenant = new Tenant();
            String tenantName = request.getBusinessName().trim();
            // Evitar duplicados: si ya existe, agregar sufijo
            if (tenantRepository.existsByNameIgnoreCase(tenantName)) {
                tenantName = tenantName + " (" + request.getCedula() + ")";
            }
            tenant.setName(tenantName);
            tenant.setStatus(TenantStatus.ACTIVE);
            tenant = tenantRepository.save(tenant);
            tenantId = tenant.getId();
            log.info("Tenant creado en registro: id={}, name={}, businessType={}", tenant.getId(), tenantName, request.getBusinessType());
        }

        User user = new User();
        user.setCedula(request.getCedula());
        user.setTelefono(request.getTelefono());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDireccion(request.getDireccion());
        user.setTenantId(tenantId);
        user.setRole(Role.ROLE_CLIENT);
        // Si tiene tenant, activar inmediatamente; si no, pendiente de pago
        user.setStatus(tenantId != null ? UserStatus.ACTIVE : UserStatus.PENDING_PAYMENT);

        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal, user.getRole().name(), user.getTenantId());

        log.info("Registro SaaS: email={}, tenantId={}, status={}", user.getEmail(), tenantId, user.getStatus());

        return buildAuthResponse(token, user, user.getRole().name());
    }

    // ---------------------------------------------------------------
    // Reset Password (Admin)
    // ---------------------------------------------------------------

    @Transactional
    public java.util.Map<String, String> resetPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        // Generate random 8-char password
        String tempPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setForcePasswordChange(true);
        userRepository.save(user);
        log.info("Password reset for user id={}, email={}", userId, user.getEmail());
        return java.util.Map.of("tempPassword", tempPassword, "email", user.getEmail());
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    /**
     * Resuelve el rol del usuario desde user_tenant_roles (fuente de verdad).
     * Fallback al campo legacy User.role si no hay registro en la tabla RBAC.
     */
    private String resolveRole(User user) {
        if (user.getTenantId() != null) {
            TenantRole tenantRole = userTenantRoleService.getRoleInTenant(user.getId(), user.getTenantId());
            if (tenantRole != null) {
                return "ROLE_" + tenantRole.name();
            }
        }
        // Fallback: campo legacy
        return user.getRole().name();
    }

    private AuthResponseDTO buildAuthResponse(String token, User user, String resolvedRole) {
        return AuthResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .cedula(user.getCedula())
                .telefono(user.getTelefono())
                .direccion(user.getDireccion())
                .role(user.getRole())
                .tenantId(user.getTenantId())
                .status(user.getStatus() != null ? user.getStatus().name() : "ACTIVE")
                .tenantRole(resolvedRole)
                .build();
    }
}
