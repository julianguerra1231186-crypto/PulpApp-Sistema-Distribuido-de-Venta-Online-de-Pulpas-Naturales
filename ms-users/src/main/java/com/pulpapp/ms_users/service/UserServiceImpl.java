package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.core.BaseServiceImpl;
import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;
import com.pulpapp.ms_users.entity.User;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.mapper.UserMapper;
import com.pulpapp.ms_users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserServiceImpl
        extends BaseServiceImpl<User, UserResponseDTO, UserRequestDTO, UserRepository>
        implements IUserService {

    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, UserMapper mapper, PasswordEncoder passwordEncoder) {
        super(repository);
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected String getEntityName() { return "User"; }

    @Override
    protected Function<User, UserResponseDTO> toResponseMapper() { return mapper::toResponseDto; }

    @Override
    protected Function<UserRequestDTO, User> toEntityMapper() { return mapper::toEntity; }

    @Override
    protected void updateEntityFromDto(UserRequestDTO dto, User entity) {
        mapper.updateEntityFromDto(dto, entity);
    }

    // Sobreescribe save para encriptar la contraseña antes de persistir
    @Override
    public UserResponseDTO save(UserRequestDTO dto) {
        User entity = mapper.toEntity(dto);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        return mapper.toResponseDto(repository.save(entity));
    }

    // Sobreescribe update para re-encriptar si se cambia la contraseña
    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        mapper.updateEntityFromDto(dto, entity);
        // Solo re-encripta si la contraseña enviada no parece un hash BCrypt
        if (dto.getPassword() != null && !dto.getPassword().startsWith("$2a$")) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return mapper.toResponseDto(repository.save(entity));
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
                                "User not found with cedula: " + cedula + " and telefono: " + telefono));
        return mapper.toResponseDto(user);
    }

    @Override
    public UserResponseDTO findByEmail(String email) {
        User user = repository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));
        return mapper.toResponseDto(user);
    }

    @Override
    public UserResponseDTO toggleActivo(Long id, boolean activo) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActivo(activo);
        return mapper.toResponseDto(repository.save(user));
    }

    @Override
    public UserResponseDTO resetPassword(Long id, String newPassword) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        return mapper.toResponseDto(repository.save(user));
    }
}