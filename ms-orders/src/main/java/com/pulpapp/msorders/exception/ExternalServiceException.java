package com.pulpapp.msorders.exception;

/**
 * Excepcion para encapsular fallos al consumir otros microservicios.
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }
}
