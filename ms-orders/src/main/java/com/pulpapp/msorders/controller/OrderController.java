package com.pulpapp.msorders.controller;

import com.pulpapp.msorders.dto.CreateOrderRequestDTO;
import com.pulpapp.msorders.dto.OrderResponseDTO;
import com.pulpapp.msorders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expone el API REST de pedidos.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Endpoint para crear un nuevo pedido.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO create(@Valid @RequestBody CreateOrderRequestDTO request) {
        return orderService.createOrder(request);
    }

    /**
     * Endpoint para listar todos los pedidos.
     */
    @GetMapping
    public List<OrderResponseDTO> getAll() {
        return orderService.findAll();
    }

    /**
     * Endpoint para consultar un pedido especifico por id.
     */
    @GetMapping("/{id}")
    public OrderResponseDTO getById(@PathVariable Long id) {
        return orderService.findById(id);
    }
}
