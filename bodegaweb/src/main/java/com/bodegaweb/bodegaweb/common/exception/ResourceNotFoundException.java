package com.bodegaweb.bodegaweb.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String recurso, Object id) {
        super("%s no encontrado con id/clave: %s".formatted(recurso, id));
    }
}
