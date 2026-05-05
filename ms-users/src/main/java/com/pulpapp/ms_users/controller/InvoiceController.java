package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.CreateInvoiceRequestDTO;
import com.pulpapp.ms_users.dto.InvoiceResponseDTO;
import com.pulpapp.ms_users.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public List<InvoiceResponseDTO> getAll() { return invoiceService.findAll(); }

    @GetMapping("/{id}")
    public InvoiceResponseDTO getById(@PathVariable Long id) { return invoiceService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponseDTO create(@Valid @RequestBody CreateInvoiceRequestDTO request) { return invoiceService.create(request); }
}
