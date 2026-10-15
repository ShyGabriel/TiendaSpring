package com.bodegaweb.direccion.dto;

public record DireccionResponse(
        Long id,
        String calle,
        String numero,
        String ciudad,
        String codigoPostal,
        String pais,
        Long usuarioId) {
}