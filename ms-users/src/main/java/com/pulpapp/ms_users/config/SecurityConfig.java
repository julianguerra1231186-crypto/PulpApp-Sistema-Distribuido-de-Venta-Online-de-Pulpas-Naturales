package com.pulpapp.ms_users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * BCrypt con strength 12 — balance entre seguridad y rendimiento.
     * Se declara aquí para evitar dependencias circulares con AuthService.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // TODO Paso 5: SecurityFilterChain completo con JWT filter y reglas RBAC.
    // Por ahora se deja todo abierto para no romper el arranque del servicio
    // mientras se construyen los componentes de seguridad incrementalmente.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
