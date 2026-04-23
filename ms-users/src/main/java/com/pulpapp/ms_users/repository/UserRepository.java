package com.pulpapp.ms_users.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pulpapp.ms_users.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByCedula(String cedula);

    Optional<User> findByCedulaAndTelefono(String cedula, String telefono);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCedula(String cedula);
}