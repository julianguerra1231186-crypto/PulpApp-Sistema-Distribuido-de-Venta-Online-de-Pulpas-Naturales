package com.pulpapp.ms_users.service;

import java.util.List;
import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;

public interface IUserService {

    List<UserResponseDTO> findAll();

    UserResponseDTO findById(Long id);

    UserResponseDTO save(UserRequestDTO dto);

    UserResponseDTO update(Long id, UserRequestDTO dto);

    void delete(Long id);

    UserResponseDTO findByCedula(String cedula);

    // VALIDAR CEDULA + TELEFONO
    UserResponseDTO validarUsuario(String cedula, String telefono);
}