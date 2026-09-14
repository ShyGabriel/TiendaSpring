package com.bodegaweb.bodegaweb.carrito.Service;

import com.bodegaweb.bodegaweb.carrito.dto.AgregarProductoRequest;
import com.bodegaweb.bodegaweb.carrito.dto.CarritoResponse;

public interface CarritoService {
    CarritoResponse obtenerPorUsuario(Long usuarioId);
    CarritoResponse agregarItem(Long usuarioId, AgregarProductoRequest request);
    CarritoResponse actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad);
    void eliminarItem(Long usuarioId, Long itemId);
    void vaciarCarrito(Long usuarioId);
}