package com.pulpapp.msorders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada principal del microservicio ms-orders.
 */
@SpringBootApplication
public class MsOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsOrdersApplication.class, args);
    }
}
