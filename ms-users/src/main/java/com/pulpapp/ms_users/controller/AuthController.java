package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.AuthResponseDTO;
import com.pulpapp.ms_users.dto.LoginRequestDTO;
import com.pulpapp.ms_users.dto.RegisterRequestDTO;
import com.pulpapp.ms_users.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints públicos de autenticación.
 * No requieren token JWT (configurado en SecurityConfig).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /auth/login
     * Body: { "email": "...", "password": "..." }
     * Response: { "token": "...", "email": "...", "name": "...", "role": "..." }
     */
    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    /**
     * POST /auth/register
     * Body: { "cedula": "...", "telefono": "...", "name": "...",
     *         "email": "...", "password": "...", "direccion": "...", "role": "ROLE_SELLER" }
     * Response: { "token": "...", "email": "...", "name": "...", "role": "..." }
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        return authService.register(request);
    }
}
