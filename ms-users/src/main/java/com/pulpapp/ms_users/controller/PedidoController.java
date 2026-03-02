package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.PedidoDTO;
import com.pulpapp.ms_users.service.IPedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final IPedidoService pedidoService;

    @PostMapping
    public PedidoDTO create(@RequestBody PedidoDTO dto) {
        return pedidoService.save(dto);
    }

    @GetMapping
    public List<PedidoDTO> getAll() {
        return pedidoService.findAll();
    }

    @GetMapping("/{id}")
    public PedidoDTO getById(@PathVariable Long id) {
        return pedidoService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pedidoService.delete(id);
    }
}