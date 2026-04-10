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
@EnableMethodSecurity          // habilita @PreAuthorize en controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final UserDetailsServiceImpl userDetailsService;

    // ---------------------------------------------------------------
    // Cadena de filtros principal
    // ---------------------------------------------------------------

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Sin estado: JWT no necesita sesión ni CSRF
            .csrf(AbstractHttpConfigurer::disable)
            // CORS delegado al CorsConfig (WebMvcConfigurer) ya existente
            .cors(cors -> cors.configure(http))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Reglas de autorización por ruta y rol
            .authorizeHttpRequests(auth -> auth

                // Rutas públicas — login y registro no requieren token
                .requestMatchers("/auth/**").permitAll()

                // Gestión de usuarios — solo ADMIN
                .requestMatchers("/users/**").hasAuthority("ROLE_ADMIN")

                // Productos — lectura libre para autenticados, escritura solo ADMIN
                .requestMatchers(HttpMethod.GET,    "/products/**").authenticated()
                .requestMatchers(HttpMethod.POST,   "/products/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/products/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/products/**").hasAuthority("ROLE_ADMIN")

                // Pedidos — ADMIN y SELLER pueden crear y consultar
                .requestMatchers("/orders/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SELLER")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )

            // Proveedor de autenticación con BCrypt
            .authenticationProvider(authenticationProvider())

            // Respuesta 401 en JSON para requests sin token válido
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(jwtAuthEntryPoint))

            // Registra el filtro JWT antes del filtro estándar de usuario/contraseña
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ---------------------------------------------------------------
    // Beans de infraestructura de autenticación
    // ---------------------------------------------------------------

    /**
     * Proveedor que conecta UserDetailsService + PasswordEncoder.
     * Spring Security lo usa para validar credenciales en el login.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager expuesto como bean para que AuthService
     * pueda invocar la autenticación programáticamente en /auth/login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt strength 12 — balance entre seguridad y rendimiento.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
