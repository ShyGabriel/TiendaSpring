package com.bodegaweb.common.exception;

/** Regla de negocio incumplida (p. ej. stock negativo, ciclo de categorías). Mapea a HTTP 422. */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
