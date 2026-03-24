package com.pulpapp.ms_users.mapper;

import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;
import com.pulpapp.ms_users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity -> ResponseDTO
    UserResponseDTO toResponseDto(User user);

    // RequestDTO -> Entity
    User toEntity(UserRequestDTO dto);

    // Update Entity (para PUT)
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget User entity);
}