package com.bodegaweb.bodegaweb.categoria.dto;

import java.util.List;

/** Vista jerárquica (árbol) de una categoría y sus descendientes. */
public record CategoriaTreeResponse(
        Long id,
        String nombre,
        String slug,
        Long categoriaPadreId,
        List<CategoriaTreeResponse> subcategorias
) {
}
