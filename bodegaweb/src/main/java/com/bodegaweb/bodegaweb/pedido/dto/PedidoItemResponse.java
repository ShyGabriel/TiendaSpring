package com.bodegaweb.bodegaweb.pedido.dto;

import java.math.BigDecimal;

public record PedidoItemResponse(
        Long id,
        Long productoId,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}