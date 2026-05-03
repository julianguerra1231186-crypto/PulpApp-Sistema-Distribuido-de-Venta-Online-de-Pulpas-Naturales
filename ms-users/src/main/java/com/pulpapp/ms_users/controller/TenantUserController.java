package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.AddUserToTenantRequestDTO;
import com.pulpapp.ms_users.dto.AssignRoleRequestDTO;
import com.pulpapp.ms_users.dto.UserTenantRoleDTO;
import com.pulpapp.ms_users.service.UserTenantRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de gestión de usuarios dentro de un tenant.
 * Solo accesible por ADMIN del tenant (configurado en SecurityConfig).
 *
 * Seguridad: el TenantContext garantiza que el admin solo ve/modifica
 * usuarios de su propio tenant. Acceso cruzado → 404.
 */
@RestController
@RequestMapping("/tenant/users")
@RequiredArgsConstructor
public class TenantUserController {

    private final UserTenantRoleService userTenantRoleService;

    /**
     * GET /tenant/users — Lista usuarios del tenant actual con sus roles.
     */
    @GetMapping
    public List<UserTenantRoleDTO> getUsers() {
        return userTenantRoleService.findUsersInMyTenant();
    }

    /**
     * POST /tenant/users — Crea un usuario dentro del tenant actual.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserTenantRoleDTO addUser(@Valid @RequestBody AddUserToTenantRequestDTO request) {
        return userTenantRoleService.addUserToTenant(request);
    }

    /**
     * PUT /tenant/users/{userId}/role — Cambia el rol de un usuario en el tenant.
     */
    @PutMapping("/{userId}/role")
    public UserTenantRoleDTO updateRole(
            @PathVariable Long userId,
            @Valid @RequestBody AssignRoleRequestDTO request) {
        return userTenantRoleService.updateRole(userId, request.getRole());
    }

    /**
     * DELETE /tenant/users/{userId} — Elimina un usuario del tenant.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable Long userId) {
        userTenantRoleService.removeUserFromTenant(userId);
    }
}
