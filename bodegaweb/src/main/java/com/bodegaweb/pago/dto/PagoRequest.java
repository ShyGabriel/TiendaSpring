package com.bodegaweb.pago.dto;

import com.bodegaweb.pago.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PagoRequest(

        @NotNull(message = "el pedidoId es obligatorio")
        Long pedidoId,

        @NotNull(message = "el monto es obligatorio")
        @DecimalMin(value = "0.01", message = "el monto debe ser mayor a 0")
        @Digits(integer = 10, fraction = 2, message = "el monto admite máximo 10 enteros y 2 decimales")
        BigDecimal monto,

        @NotNull(message = "el metodoPago es obligatorio")
        MetodoPago metodoPago,

        @Size(max = 120, message = "la referenciaExterna no puede superar 120 caracteres")
        String referenciaExterna
) {
}
