package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.CashMovementRequestDTO;
import com.pulpapp.ms_users.dto.CashMovementResponseDTO;
import com.pulpapp.ms_users.service.CashMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashMovementController {

    private final CashMovementService service;

    @GetMapping
    public List<CashMovementResponseDTO> getAll() { return service.findAll(); }

    @GetMapping("/today")
    public List<CashMovementResponseDTO> getToday() { return service.findToday(); }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() { return service.getSummary(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CashMovementResponseDTO create(@Valid @RequestBody CashMovementRequestDTO request) { return service.create(request); }
}
