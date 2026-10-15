package com.bodegaweb.catalogo.categoria.dto;

import java.time.LocalDateTime;

/** Vista plana de una categoría. */
public record CategoriaResponse(
        Long id,
        String nombre,
        String slug,
        Long categoriaPadreId,
        String categoriaPadreNombre,
        LocalDateTime createdAt
) {
}
