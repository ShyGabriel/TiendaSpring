package com.bodegaweb.usuario.dto;

import java.time.LocalDateTime;

import com.bodegaweb.usuario.entity.Usuario.Rol;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        Rol rol,
        LocalDateTime createdAt) {
}