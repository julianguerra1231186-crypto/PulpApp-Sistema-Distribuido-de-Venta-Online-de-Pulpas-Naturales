package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.AuthResponseDTO;
import com.pulpapp.ms_users.dto.LoginRequestDTO;
import com.pulpapp.ms_users.dto.RegisterRequestDTO;
import com.pulpapp.ms_users.entity.Role;
import com.pulpapp.ms_users.entity.Tenant;
import com.pulpapp.ms_users.entity.User;
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

    // ---------------------------------------------------------------
    // Login
    // ---------------------------------------------------------------

    /**
     * Autentica al usuario con email + password y devuelve un JWT.
     * AuthenticationManager delega en DaoAuthenticationProvider,
     * que usa UserDetailsServiceImpl + BCrypt internamente.
     *
     * Fase 1: El JWT ahora incluye tenantId si el usuario tiene uno asignado.
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

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal, user.getRole().name(), user.getTenantId());

        log.info("Login exitoso: email={}, tenantId={}", user.getEmail(), user.getTenantId());

        return buildAuthResponse(token, user);
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

        return buildAuthResponse(token, user);
    }

    // ---------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------

    private AuthResponseDTO buildAuthResponse(String token, User user) {
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
                .build();
    }
}
