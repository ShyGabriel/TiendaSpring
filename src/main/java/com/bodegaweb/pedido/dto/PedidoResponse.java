package com.bodegaweb.pedido.dto;

import com.bodegaweb.pedido.entity.EstadoPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long usuarioId,
        Long direccionId,
        EstadoPedido estado,
        BigDecimal total,
        LocalDateTime fechaPedido,
        List<PedidoItemResponse> items
) {}