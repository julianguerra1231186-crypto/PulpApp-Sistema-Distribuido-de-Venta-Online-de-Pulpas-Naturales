package com.pulpapp.msorders.controller;

import com.pulpapp.msorders.dto.SellerOrderDTO;
import com.pulpapp.msorders.service.SellerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expone el endpoint de pedidos enriquecidos para el rol SELLER y ADMIN.
 *
 * Seguridad:
 * Este microservicio no incluye Spring Security. El control de acceso
 * por rol (ROLE_SELLER / ROLE_ADMIN) se aplica en ms-users mediante
 * el JwtAuthFilter y SecurityConfig, que validan el JWT antes de que
 * la petición llegue aquí a través del API Gateway.
 *
 * El endpoint GET /orders/seller está configurado en SecurityConfig de ms-users
 * para requerir ROLE_SELLER o ROLE_ADMIN.
 *
 * Separación de responsabilidades:
 * Este controlador es independiente de OrderController — no modifica
 * ni extiende el contrato existente de /orders.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class SellerOrderController {

    private final SellerOrderService sellerOrderService;

    /**
     * GET /orders/seller
     *
     * Retorna todos los pedidos enriquecidos con:
     * - Nombre y email del cliente (resuelto desde ms-users)
     * - Nombre de cada producto (resuelto desde ms-products)
     * - Total, fecha y hora del pedido
     *
     * Acceso: ROLE_SELLER, ROLE_ADMIN
     *
     * @return lista de pedidos en formato SellerOrderDTO
     */
    @GetMapping("/seller")
    public List<SellerOrderDTO> getOrdersForSeller() {
        return sellerOrderService.findAllForSeller();
    }
}
