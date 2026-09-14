package com.bodegaweb.catalogo.categoria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload de creación/actualización de categoría.
 * Si {@code slug} viene vacío se genera a partir de {@code nombre}.
 */
public record CategoriaRequest(

        @NotBlank(message = "el nombre es obligatorio")
        @Size(max = 100, message = "el nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 120, message = "el slug no puede superar 120 caracteres")
        String slug,

        Long categoriaPadreId
) {
}
