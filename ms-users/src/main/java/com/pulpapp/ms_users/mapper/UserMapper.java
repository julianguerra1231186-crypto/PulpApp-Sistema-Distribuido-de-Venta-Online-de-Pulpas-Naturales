package com.pulpapp.ms_users.mapper;

import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;
import com.pulpapp.ms_users.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements EntityMapper<User, UserResponseDTO, UserRequestDTO> {

    @Override
    public UserResponseDTO toResponseDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setCedula(user.getCedula());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDireccion(user.getDireccion());
        dto.setTelefono(user.getTelefono());
        return dto;
    }

    @Override
    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setCedula(dto.getCedula());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setDireccion(dto.getDireccion());
        user.setTelefono(dto.getTelefono());
        return user;
    }

    @Override
    public void updateEntityFromDto(UserRequestDTO dto, User user) {
        user.setCedula(dto.getCedula());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setDireccion(dto.getDireccion());
        user.setTelefono(dto.getTelefono());
    }
}