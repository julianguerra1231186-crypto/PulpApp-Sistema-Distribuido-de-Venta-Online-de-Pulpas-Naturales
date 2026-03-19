package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.core.BaseServiceImpl;
import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;
import com.pulpapp.ms_users.entity.User;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.mapper.UserMapper;
import com.pulpapp.ms_users.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl
        extends BaseServiceImpl<User, UserResponseDTO, UserRequestDTO, UserRepository>
        implements IUserService {

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected String getEntityName() {
        return "User";
    }

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
                        new ResourceNotFoundException("User not found with cedula and telefono"));
        return mapper.toResponseDto(user);
    }
}