package com.pulpapp.msorders.controller;

import com.pulpapp.msorders.dto.FrequentClientDTO;
import com.pulpapp.msorders.service.FrequentClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints administrativos del microservicio de pedidos.
 *
 * Seguridad:
 * Este microservicio no incluye Spring Security. El control de acceso
 * por rol (ROLE_ADMIN) se aplica en ms-users mediante JwtAuthFilter
 * y SecurityConfig, que validan el JWT antes de que la petición llegue
 * aquí a través del API Gateway.
 *
 * Separación de responsabilidades:
 * Controlador independiente de OrderController y SellerOrderController.
 * No modifica contratos existentes.
 */
@RestController
@RequestMapping("/orders/admin")
@RequiredArgsConstructor
public class AdminOrderController {

    private final FrequentClientService frequentClientService;

    /**
     * GET /orders/admin/frequent-clients
     *
     * Retorna la lista de clientes frecuentes ordenada por número de pedidos (DESC).
     * Cada entrada incluye:
     * - userId, clientName, clientEmail (resueltos desde ms-users)
     * - orderCount: número de pedidos realizados
     * - totalSpent: suma total gastada
     *
     * Acceso: ROLE_ADMIN
     */
    @GetMapping("/frequent-clients")
    public List<FrequentClientDTO> getFrequentClients() {
        return frequentClientService.findFrequentClients();
    }
}
