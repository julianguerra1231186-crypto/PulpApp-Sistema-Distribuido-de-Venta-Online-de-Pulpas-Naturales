package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.core.BaseServiceImpl;
import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;
import com.pulpapp.ms_users.entity.User;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.mapper.UserMapper;
import com.pulpapp.ms_users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserServiceImpl
        extends BaseServiceImpl<User, UserResponseDTO, UserRequestDTO, UserRepository>
        implements IUserService {

    private final UserMapper mapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    protected String getEntityName() {
        return "User";
    }

    // MapStruct integración con BaseServiceImpl

    @Override
    protected Function<User, UserResponseDTO> toResponseMapper() {
        return mapper::toResponseDto;
    }

    @Override
    protected Function<UserRequestDTO, User> toEntityMapper() {
        return mapper::toEntity;
    }

    @Override
    protected void updateEntityFromDto(UserRequestDTO dto, User entity) {
        mapper.updateEntityFromDto(dto, entity);
    }

    // Lógica específica

    @Override
    public UserResponseDTO findByCedula(String cedula) {
        User user = repository.findByCedula(cedula)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with cedula: " + cedula));
        return mapper.toResponseDto(user);
    }

    @Override
    public UserResponseDTO validarUsuario(String cedula, String telefono) {
        User user = repository.findByCedulaAndTelefono(cedula, telefono)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with cedula: " + cedula + " and telefono: " + telefono
                        ));
        return mapper.toResponseDto(user);
    }
}