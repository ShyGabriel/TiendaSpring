package com.bodegaweb.usuario.dto;

import com.bodegaweb.usuario.entity.Usuario.Rol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El apellido es obligatorio") String apellido,
        @NotBlank(message = "El email es obligatorio") @Email(message = "Formato de email invalido") String email,
        @NotBlank(message = "La contrasena es obligatoria") @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres") String password,
        @Size(max = 20, message = "El telefono no puede superar 20 caracteres") String telefono,
        Rol rol) {
}