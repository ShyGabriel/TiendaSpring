package com.bodegaweb.carrito.dto;

import java.math.BigDecimal;

public record CarritoItemResponse(
        Long id,
        Long productoId,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}