package com.pulpapp.ms_users.config;

import com.pulpapp.ms_users.security.JwtAuthEntryPoint;
import com.pulpapp.ms_users.security.JwtAuthFilter;
import com.pulpapp.ms_users.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configure(http))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // ── Autenticación JWT ──────────────────────────────────────
                .requestMatchers("/auth/**").permitAll()

                // ── Postulaciones laborales ────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/job-applications").permitAll()
                .requestMatchers(HttpMethod.GET,  "/job-applications").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET,  "/job-applications/**").hasAuthority("ROLE_ADMIN")

                // ── Usuarios: operaciones del frontend sin token ───────────
                // El frontend crea, consulta y actualiza usuarios sin JWT
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .requestMatchers(HttpMethod.PUT,  "/users/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/users/cedula/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/users/validar/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/users/email").permitAll()

                // Listar todos los usuarios y eliminar: solo ADMIN
                .requestMatchers(HttpMethod.GET,    "/users").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/users/**").hasAuthority("ROLE_ADMIN")

                // ── Productos: lectura pública, escritura solo ADMIN ───────
                .requestMatchers(HttpMethod.GET,    "/products/**").permitAll()
                .requestMatchers(HttpMethod.GET,    "/products").permitAll()
                .requestMatchers(HttpMethod.POST,   "/products/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/products/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasAuthority("ROLE_ADMIN")

                // ── Pedidos: el frontend los crea sin token ────────────────
                .requestMatchers(HttpMethod.POST, "/orders").permitAll()
                // Vista enriquecida para vendedor y admin (Fase 1 — seller orders)
                .requestMatchers(HttpMethod.GET,  "/orders/seller").hasAnyAuthority("ROLE_ADMIN", "ROLE_SELLER")
                .requestMatchers(HttpMethod.GET,  "/orders/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SELLER", "ROLE_CLIENT")
                .requestMatchers(HttpMethod.GET,  "/orders").hasAnyAuthority("ROLE_ADMIN", "ROLE_SELLER", "ROLE_CLIENT")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )

            .authenticationProvider(authenticationProvider())

            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(jwtAuthEntryPoint))

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
