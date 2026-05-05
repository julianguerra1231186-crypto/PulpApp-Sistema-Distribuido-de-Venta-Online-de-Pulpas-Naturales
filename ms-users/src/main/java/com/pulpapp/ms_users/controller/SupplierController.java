package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.SupplierRequestDTO;
import com.pulpapp.ms_users.dto.SupplierResponseDTO;
import com.pulpapp.ms_users.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public List<SupplierResponseDTO> getAll() { return supplierService.findAll(); }

    @GetMapping("/search")
    public List<SupplierResponseDTO> search(@RequestParam String q) { return supplierService.search(q); }

    @GetMapping("/{id}")
    public SupplierResponseDTO getById(@PathVariable Long id) { return supplierService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierResponseDTO create(@Valid @RequestBody SupplierRequestDTO request) { return supplierService.create(request); }

    @PutMapping("/{id}")
    public SupplierResponseDTO update(@PathVariable Long id, @Valid @RequestBody SupplierRequestDTO request) { return supplierService.update(id, request); }

    @PatchMapping("/{id}/active")
    public SupplierResponseDTO toggleActive(@PathVariable Long id, @RequestParam boolean active) { return supplierService.toggleActive(id, active); }
}
