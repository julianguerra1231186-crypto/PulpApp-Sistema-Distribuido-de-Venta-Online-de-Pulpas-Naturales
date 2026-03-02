package com.pulpapp.ms_users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;   // 👈 IMPORTANTE

import java.util.List;

import com.pulpapp.ms_users.dto.UserRequestDTO;
import com.pulpapp.ms_users.dto.UserResponseDTO;
import com.pulpapp.ms_users.service.IUserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // GET ALL
    @GetMapping
    public List<UserResponseDTO> getAll() {
        return userService.findAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public UserResponseDTO getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    // POST
    @PostMapping
    public UserResponseDTO create(@Valid @RequestBody UserRequestDTO dto) {  // Validacion
        return userService.save(dto);
    }

    // PUT
    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable Long id,
                                  @Valid @RequestBody UserRequestDTO dto) {  // Validacion
        return userService.update(id, dto);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}