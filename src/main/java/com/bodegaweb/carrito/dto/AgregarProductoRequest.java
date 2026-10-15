package com.bodegaweb.carrito.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AgregarProductoRequest(
        @NotNull(message = "el productoId es obligatorio")
        Long productoId,

        @NotNull(message = "la cantidad es obligatoria")
        @Min(value = 1, message = "la cantidad debe ser mayor a 0")
        Integer cantidad
) {}
