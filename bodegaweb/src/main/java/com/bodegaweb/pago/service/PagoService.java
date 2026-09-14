package com.bodegaweb.pago.service;

import com.bodegaweb.pago.dto.PagoRequest;
import com.bodegaweb.pago.dto.PagoResponse;
import com.bodegaweb.pago.enums.EstadoPago;
import java.util.List;

public interface PagoService {

    PagoResponse crear(PagoRequest request);

    PagoResponse obtener(Long id);

    PagoResponse obtenerPorPedido(Long pedidoId);

    List<PagoResponse> listar();

    PagoResponse actualizarEstado(Long id, EstadoPago nuevoEstado);
}
