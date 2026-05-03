package com.pulpapp.ms_users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;
import com.pulpapp.ms_users.dto.ResetPasswordRequestDTO;
import com.pulpapp.ms_users.service.IUserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping
    public List<UserResponseDTO> getAll() {
        return userService.findAll();
    }

    // BUSCAR POR CEDULA
    @GetMapping("/cedula/{cedula}")
    public UserResponseDTO getByCedula(@PathVariable String cedula) {
        return userService.findByCedula(cedula);
    }

    // BUSCAR POR EMAIL (público — para cargar perfil propio)
    @GetMapping("/email")
    public UserResponseDTO getByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    // VALIDAR CEDULA + TELEFONO
    @GetMapping("/validar/{cedula}/{telefono}")
    public UserResponseDTO validarUsuario(
            @PathVariable String cedula,
            @PathVariable String telefono) {

        return userService.validarUsuario(cedula, telefono);
    }

    // BUSCAR POR ID (DEBE IR DESPUES)
    @GetMapping("/{id}")
    public UserResponseDTO getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public UserResponseDTO create(@Valid @RequestBody UserRequestDTO dto) {
        return userService.save(dto);
    }

    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable Long id,
                                  @Valid @RequestBody UserRequestDTO dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    // ACTIVAR / DESACTIVAR vendedor
    @PatchMapping("/{id}/activo")
    public UserResponseDTO toggleActivo(@PathVariable Long id,
                                        @RequestParam boolean activo) {
        return userService.toggleActivo(id, activo);
    }

    // RESET DE CONTRASEÑA — solo ADMIN (configurado en SecurityConfig)
    @PatchMapping("/{id}/password")
    public UserResponseDTO resetPassword(@PathVariable Long id,
                                         @Valid @RequestBody ResetPasswordRequestDTO dto) {
        return userService.resetPassword(id, dto.getNewPassword());
    }

}