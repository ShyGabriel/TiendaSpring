package com.bodegaweb.resena.dto;

import java.time.LocalDateTime;

public record ResenaResponse(
        Long id,
        Long productoId,
        Long usuarioId,
        Integer calificacion,
        String comentario,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
