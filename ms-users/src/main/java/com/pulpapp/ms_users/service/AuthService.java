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
     * Registra un nuevo usuario, encripta su contraseña y devuelve un JWT
     * para que pueda operar de inmediato sin necesidad de hacer login aparte.
     *
     * Fase 1 Multi-Tenant:
     *  - El usuario se asigna al tenant por defecto ("PulpApp").
     *  - Si el tenant no existe, se crea automáticamente.
     *  - El JWT incluye tenantId desde el primer momento.
     */
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (userRepository.existsByCedula(request.getCedula())) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        // Obtener o crear el tenant por defecto
        Tenant defaultTenant = tenantService.getOrCreateDefaultTenant();

        User user = new User();
        user.setCedula(request.getCedula());
        user.setTelefono(request.getTelefono());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDireccion(request.getDireccion());
        user.setTenantId(defaultTenant.getId());
        // El registro público SIEMPRE asigna ROLE_CLIENT
        // Los vendedores solo los crea el ADMIN desde el panel
        user.setRole(Role.ROLE_CLIENT);

        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal, user.getRole().name(), user.getTenantId());

        log.info("Registro exitoso: email={}, tenantId={}", user.getEmail(), user.getTenantId());

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
                .build();
    }
}
