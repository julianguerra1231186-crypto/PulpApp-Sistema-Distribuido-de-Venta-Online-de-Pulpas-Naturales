package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;
import com.pulpapp.ms_users.entity.User;
import com.pulpapp.ms_users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponseDTO> findAll() {

        return userRepository.findAll().stream().map(this::mapToResponse).toList();

    }

    @Override
    public UserResponseDTO findById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    @Override
    public UserResponseDTO save(UserRequestDTO dto) {

        User user = new User();

        user.setCedula(dto.getCedula());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setDireccion(dto.getDireccion());

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setCedula(dto.getCedula());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setDireccion(dto.getDireccion());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    @Override
    public void delete(Long id) {

        userRepository.deleteById(id);

    }

    private UserResponseDTO mapToResponse(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setCedula(user.getCedula());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDireccion(user.getDireccion());

        return dto;
    }
}