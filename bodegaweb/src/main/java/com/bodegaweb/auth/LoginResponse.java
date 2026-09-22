package com.bodegaweb.auth;

import com.bodegaweb.usuario.dto.UsuarioResponse;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEnMs,
        UsuarioResponse usuario) {
}