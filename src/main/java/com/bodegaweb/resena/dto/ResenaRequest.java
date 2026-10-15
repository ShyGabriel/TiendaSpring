package com.bodegaweb.resena.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResenaRequest(

        @NotNull(message = "el productoId es obligatorio")
        Long productoId,

        @NotNull(message = "el usuarioId es obligatorio")
        Long usuarioId,

        @NotNull(message = "la calificacion es obligatoria")
        @Min(value = 1, message = "la calificacion mínima es 1")
        @Max(value = 5, message = "la calificacion máxima es 5")
        Integer calificacion,

        @Size(max = 2000, message = "el comentario no puede superar 2000 caracteres")
        String comentario
) {
}
