package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.InventoryItemRequestDTO;
import com.pulpapp.ms_users.dto.InventoryItemResponseDTO;
import com.pulpapp.ms_users.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<InventoryItemResponseDTO> getAll() {
        return inventoryService.findAll();
    }

    @GetMapping("/search")
    public List<InventoryItemResponseDTO> search(@RequestParam String q) {
        return inventoryService.search(q);
    }

    @GetMapping("/{id}")
    public InventoryItemResponseDTO getById(@PathVariable Long id) {
        return inventoryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryItemResponseDTO create(@Valid @RequestBody InventoryItemRequestDTO request) {
        return inventoryService.create(request);
    }

    @PutMapping("/{id}")
    public InventoryItemResponseDTO update(@PathVariable Long id, @Valid @RequestBody InventoryItemRequestDTO request) {
        return inventoryService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public InventoryItemResponseDTO toggleActive(@PathVariable Long id, @RequestParam boolean active) {
        return inventoryService.toggleActive(id, active);
    }
}
