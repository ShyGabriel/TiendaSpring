package com.bodegaweb.bodegaweb.producto.dto;

import java.time.LocalDateTime;

public record InventarioResponse(
        Long productoId,
        Integer stockDisponible,
        Integer stockReservado,
        Integer stockTotal,
        LocalDateTime actualizadoEn
) {
}
