package com.bodegaweb.pago.dto;

import com.bodegaweb.pago.enums.EstadoPago;
import com.bodegaweb.pago.enums.MetodoPago;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        Long pedidoId,
        BigDecimal monto,
        MetodoPago metodoPago,
        EstadoPago estado,
        String referenciaExterna,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
