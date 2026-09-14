package com.bodegaweb.bodegaweb.pedido.Service;

import com.bodegaweb.bodegaweb.pedido.dto.PedidoRequest;
import com.bodegaweb.bodegaweb.pedido.dto.PedidoResponse;
import com.bodegaweb.bodegaweb.pedido.entity.EstadoPedido;
import java.util.List;

public interface PedidoService {
    PedidoResponse crearDesdeCarrito(PedidoRequest request);
    PedidoResponse obtener(Long id);
    List<PedidoResponse> listarPorUsuario(Long usuarioId);
    PedidoResponse actualizarEstado(Long id, EstadoPedido estado);
}