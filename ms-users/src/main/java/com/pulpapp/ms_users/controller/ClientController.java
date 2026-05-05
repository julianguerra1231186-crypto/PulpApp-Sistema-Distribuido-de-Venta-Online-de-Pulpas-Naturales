package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.ClientRequestDTO;
import com.pulpapp.ms_users.dto.ClientResponseDTO;
import com.pulpapp.ms_users.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de Clientes del negocio.
 * Aislamiento multi-tenant: cada tenant solo ve sus propios clientes.
 */
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public List<ClientResponseDTO> getAll() {
        return clientService.findAll();
    }

    @GetMapping("/search")
    public List<ClientResponseDTO> search(@RequestParam String q) {
        return clientService.search(q);
    }

    @GetMapping("/{id}")
    public ClientResponseDTO getById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponseDTO create(@Valid @RequestBody ClientRequestDTO request) {
        return clientService.create(request);
    }

    @PutMapping("/{id}")
    public ClientResponseDTO update(@PathVariable Long id, @Valid @RequestBody ClientRequestDTO request) {
        return clientService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public ClientResponseDTO toggleActive(@PathVariable Long id, @RequestParam boolean active) {
        return clientService.toggleActive(id, active);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clientService.delete(id);
    }
}
