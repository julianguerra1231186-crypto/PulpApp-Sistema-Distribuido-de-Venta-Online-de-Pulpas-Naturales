package com.pulpapp.ms_users.security;

import com.pulpapp.ms_users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapter que envuelve la entidad User y la expone como UserDetails.
 * Mantiene la entidad de dominio limpia de dependencias de Spring Security.
 */
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final User user;

    /** Expone el usuario de dominio para que JwtAuthFilter pueda acceder al rol. */
    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // El rol ya incluye el prefijo ROLE_ (ej: ROLE_ADMIN)
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /** Spring Security usa el email como identificador principal (username). */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override public boolean isAccountNonExpired()  { return true; }
    @Override public boolean isAccountNonLocked()   { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()            { return true; }
}
