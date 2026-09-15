package com.bodegaweb.pedido.service;

import com.bodegaweb.pedido.dto.PedidoRequest;
import com.bodegaweb.pedido.dto.PedidoResponse;
import com.bodegaweb.pedido.entity.EstadoPedido;
import java.util.List;

public interface PedidoService {
    PedidoResponse crearDesdeCarrito(PedidoRequest request);
    PedidoResponse obtener(Long id);
    List<PedidoResponse> listarPorUsuario(Long usuarioId);
    PedidoResponse actualizarEstado(Long id, EstadoPedido estado);
}