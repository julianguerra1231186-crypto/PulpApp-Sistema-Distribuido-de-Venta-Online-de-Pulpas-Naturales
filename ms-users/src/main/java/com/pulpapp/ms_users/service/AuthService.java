package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.AuthResponseDTO;
import com.pulpapp.ms_users.dto.LoginRequestDTO;
import com.pulpapp.ms_users.dto.RegisterRequestDTO;
import com.pulpapp.ms_users.entity.Role;
import com.pulpapp.ms_users.entity.Tenant;
import com.pulpapp.ms_users.entity.TenantRole;
import com.pulpapp.ms_users.entity.User;
import com.pulpapp.ms_users.entity.UserStatus;
import com.pulpapp.ms_users.exception.BadCredentialsException;
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

        return buildAuthResponse(token, user, resolvedRole);
    }

    // ---------------------------------------------------------------
    // Registro
    // ---------------------------------------------------------------

    /**
     * Registra un nuevo usuario en el flujo SaaS.
     *
     * Fase 4 — Cambio crítico:
     *  - El usuario se registra con status = PENDING_PAYMENT
     *  - tenant_id = NULL (se asigna cuando el admin aprueba el pago)
     *  - Recibe un JWT limitado para poder subir comprobante de pago
     *  - NO tiene acceso completo al sistema hasta que sea ACTIVE
     */
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (userRepository.existsByCedula(request.getCedula())) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        User user = new User();
        user.setCedula(request.getCedula());
        user.setTelefono(request.getTelefono());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDireccion(request.getDireccion());
        // Fase 4 SaaS: usuario inicia sin tenant y pendiente de pago
        user.setTenantId(null);
        user.setRole(Role.ROLE_CLIENT);
        user.setStatus(UserStatus.PENDING_PAYMENT);

        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(user);
        // JWT sin tenantId (null) — acceso limitado
        String token = jwtService.generateToken(principal, user.getRole().name(), user.getTenantId());

        log.info("Registro SaaS: email={}, status=PENDING_PAYMENT", user.getEmail());

        return buildAuthResponse(token, user, user.getRole().name());
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
