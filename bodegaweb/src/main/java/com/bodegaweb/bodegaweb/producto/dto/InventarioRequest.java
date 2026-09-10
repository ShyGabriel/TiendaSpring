package com.bodegaweb.bodegaweb.producto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Fija valores absolutos de stock (PUT del sub-recurso inventario). */
public record InventarioRequest(

        @NotNull(message = "stockDisponible es obligatorio")
        @Min(value = 0, message = "stockDisponible no puede ser negativo")
        Integer stockDisponible,

        @NotNull(message = "stockReservado es obligatorio")
        @Min(value = 0, message = "stockReservado no puede ser negativo")
        Integer stockReservado
) {
}
