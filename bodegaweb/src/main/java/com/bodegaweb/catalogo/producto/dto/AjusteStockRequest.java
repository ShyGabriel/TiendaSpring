package com.bodegaweb.catalogo.producto.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Ajuste relativo de stock (PATCH {@code /inventario/ajuste}).
 * {@code delta} puede ser negativo; el resultado no puede quedar bajo cero.
 */
public record AjusteStockRequest(

        @NotNull(message = "tipo es obligatorio (DISPONIBLE | RESERVADO)")
        TipoStock tipo,

        @NotNull(message = "delta es obligatorio")
        Integer delta
) {

    public enum TipoStock {
        DISPONIBLE,
        RESERVADO
    }
}
