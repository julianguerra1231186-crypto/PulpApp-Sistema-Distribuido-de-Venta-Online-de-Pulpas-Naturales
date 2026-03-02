package com.pulpapp.ms_users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pulpapp.ms_users.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
