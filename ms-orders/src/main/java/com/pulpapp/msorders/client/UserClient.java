package com.pulpapp.msorders.client;

import com.pulpapp.msorders.dto.UserSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP para consultar datos básicos de usuarios desde ms-users.
 *
 * Responsabilidad única: resolver nombre y email de un usuario por su ID.
 * Aplica un patrón de degradación elegante (graceful degradation):
 * si ms-users no responde, retorna un DTO con valores de fallback
 * en lugar de propagar una excepción que rompa la vista del vendedor.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserClient {

    private final RestClient usersRestClient;

    /**
     * Consulta el resumen de un usuario por su ID.
     *
     * @param userId identificador del usuario en ms-users
     * @return DTO con nombre y email, o valores de fallback si ms-users no responde
     */
    public UserSummaryDTO getUserById(Long userId) {
        try {
            UserSummaryDTO user = usersRestClient.get()
                    .uri("/users/{id}", userId)
                    .retrieve()
                    .body(UserSummaryDTO.class);

            if (user == null) {
                log.warn("ms-users retornó respuesta vacía para userId={}", userId);
                return buildFallback(userId);
            }

            return user;

        } catch (RestClientException ex) {
            // Degradación elegante: el vendedor sigue viendo los pedidos aunque ms-users no responda
            log.warn("No se pudo resolver el usuario {} desde ms-users: {}", userId, ex.getMessage());
            return buildFallback(userId);
        }
    }

    /**
     * Construye un DTO de fallback cuando ms-users no está disponible.
     * Permite que la vista del vendedor siga funcionando con información parcial.
     */
    private UserSummaryDTO buildFallback(Long userId) {
        UserSummaryDTO fallback = new UserSummaryDTO();
        fallback.setId(userId);
        fallback.setName("Cliente #" + userId);
        fallback.setEmail("");
        return fallback;
    }
}
