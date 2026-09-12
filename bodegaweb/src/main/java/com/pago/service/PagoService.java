package com.pago.service;

import com.pago.dto.PagoRequest;
import com.pago.dto.PagoResponse;
import com.pago.enums.EstadoPago;
import java.util.List;

public interface PagoService {

    PagoResponse crear(PagoRequest request);

    PagoResponse obtener(Long id);

    PagoResponse obtenerPorPedido(Long pedidoId);

    List<PagoResponse> listar();

    PagoResponse actualizarEstado(Long id, EstadoPago nuevoEstado);
}
