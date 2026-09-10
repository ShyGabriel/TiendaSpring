package com.bodegaweb.bodegaweb.common.exception;

/** Violación de unicidad de negocio (slug, sku, etc.). Mapea a HTTP 409. */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String recurso, String campo, Object valor) {
        super("%s ya existe con %s = %s".formatted(recurso, campo, valor));
    }
}
