package com.bodegaweb.direccion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DireccionRequest(
        @NotBlank(message = "La calle es obligatoria") String calle,
        @NotBlank(message = "El numero es obligatorio") String numero,
        @NotBlank(message = "La ciudad es obligatoria") String ciudad,
        @NotBlank(message = "El codigo postal es obligatorio") String codigoPostal,
        @NotBlank(message = "El pais es obligatorio") String pais,
        @NotNull(message = "El usuarioId es obligatorio") Long usuarioId) {
}