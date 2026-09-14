package com.bodegaweb.catalogo.producto.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Payload de creación/actualización de producto.
 * En la creación, {@code inventarioInicial} es opcional (por defecto 0/0).
 * En la actualización el inventario se ignora (se gestiona por su sub-recurso).
 */
public record ProductoRequest(

        @NotBlank(message = "el sku es obligatorio")
        @Size(max = 50, message = "el sku no puede superar 50 caracteres")
        String sku,

        @NotBlank(message = "el nombre es obligatorio")
        @Size(max = 200, message = "el nombre no puede superar 200 caracteres")
        String nombre,

        String descripcion,

        @NotNull(message = "el precio es obligatorio")
        @DecimalMin(value = "0.0", message = "el precio no puede ser negativo")
        @Digits(integer = 10, fraction = 2, message = "el precio admite máximo 10 enteros y 2 decimales")
        BigDecimal precio,

        Long categoriaId,

        @Size(max = 500, message = "la imagen_url no puede superar 500 caracteres")
        String imagenUrl,

        Boolean activo,

        @Valid
        InventarioRequest inventarioInicial
) {
}
