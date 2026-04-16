package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.AuthResponseDTO;
import com.pulpapp.ms_users.dto.LoginRequestDTO;
import com.pulpapp.ms_users.dto.RegisterRequestDTO;
import com.pulpapp.ms_users.entity.Role;
import com.pulpapp.ms_users.entity.User;
import com.pulpapp.ms_users.exception.BadCredentialsException;
import com.pulpapp.ms_users.repository.UserRepository;
import com.pulpapp.ms_users.security.JwtService;
import com.pulpapp.ms_users.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación: login y registro de usuarios.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ---------------------------------------------------------------
    // Login
    // ---------------------------------------------------------------

    /**
     * Autentica al usuario con email + password y devuelve un JWT.
     * AuthenticationManager delega en DaoAuthenticationProvider,
     * que usa UserDetailsServiceImpl + BCrypt internamente.
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

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email o contraseña incorrectos"));

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal, user.getRole().name());

        return AuthResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .cedula(user.getCedula())
                .telefono(user.getTelefono())
                .direccion(user.getDireccion())
                .role(user.getRole())
                .build();
    }

    // ---------------------------------------------------------------
    // Registro
    // ---------------------------------------------------------------

    /**
     * Registra un nuevo usuario, encripta su contraseña y devuelve un JWT
     * para que pueda operar de inmediato sin necesidad de hacer login aparte.
     */
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
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
        // El registro público SIEMPRE asigna ROLE_CLIENT
        // Los vendedores solo los crea el ADMIN desde el panel
        user.setRole(Role.ROLE_CLIENT);

        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal, user.getRole().name());

        return AuthResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .cedula(user.getCedula())
                .telefono(user.getTelefono())
                .direccion(user.getDireccion())
                .role(user.getRole())
                .build();
    }
}
