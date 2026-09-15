package com.bodegaweb.pedido.dto;

import jakarta.validation.constraints.NotNull;

public record PedidoRequest(
        @NotNull(message = "el usuarioId es obligatorio")
        Long usuarioId,

        @NotNull(message = "la direccionId es obligatoria")
        Long direccionId
) {}