package com.pulpapp.ms_users.mapper;

import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;
import com.pulpapp.ms_users.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Convierte la entidad persistida en un DTO de salida usando asignaciones explicitas.
    // Esto evita depender de generacion automatica de mapeo cuando hay incompatibilidades
    // entre procesadores de anotaciones.
    public UserResponseDTO toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setCedula(user.getCedula());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDireccion(user.getDireccion());
        dto.setTelefono(user.getTelefono());
        return dto;
    }

    // Convierte el DTO de entrada en la entidad de dominio para operaciones de creacion.
    public User toEntity(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        User entity = new User();
        entity.setCedula(dto.getCedula());
        entity.setTelefono(dto.getTelefono());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setDireccion(dto.getDireccion());
        return entity;
    }

    // Actualiza la entidad existente con los datos recibidos en el PUT.
    public void updateEntityFromDto(UserRequestDTO dto, User entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setCedula(dto.getCedula());
        entity.setTelefono(dto.getTelefono());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setDireccion(dto.getDireccion());
    }
}
