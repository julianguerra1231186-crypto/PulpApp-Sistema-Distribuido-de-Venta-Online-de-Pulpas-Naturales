package com.pulpapp.ms_users.mapper;

import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;
import com.pulpapp.ms_users.entity.Role;
import com.pulpapp.ms_users.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toResponseDto(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setCedula(user.getCedula());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDireccion(user.getDireccion());
        dto.setTelefono(user.getTelefono());
        dto.setRole(user.getRole());
        return dto;
    }

    public User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;

        User entity = new User();
        entity.setCedula(dto.getCedula());
        entity.setTelefono(dto.getTelefono());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setDireccion(dto.getDireccion());
        // Si no se envía rol, se asigna ROLE_SELLER por defecto
        entity.setRole(dto.getRole() != null ? dto.getRole() : Role.ROLE_SELLER);
        return entity;
    }

    public void updateEntityFromDto(UserRequestDTO dto, User entity) {
        if (dto == null || entity == null) return;

        entity.setCedula(dto.getCedula());
        entity.setTelefono(dto.getTelefono());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setDireccion(dto.getDireccion());
        if (dto.getRole() != null) {
            entity.setRole(dto.getRole());
        }
    }
}
