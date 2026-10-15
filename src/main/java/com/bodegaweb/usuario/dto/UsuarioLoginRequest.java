package com.bodegaweb.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioLoginRequest(
        @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email invalido") String email,
        @NotBlank(message = "La contrasena es obligatoria") String password) {
}