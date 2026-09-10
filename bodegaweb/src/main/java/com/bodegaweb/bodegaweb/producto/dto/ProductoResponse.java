package com.bodegaweb.bodegaweb.producto.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoResponse(
        Long id,
        String sku,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Long categoriaId,
        String categoriaNombre,
        String imagenUrl,
        Boolean activo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        InventarioResponse inventario
) {
}
