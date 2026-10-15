package com.bodegaweb.carrito.service;

import com.bodegaweb.carrito.dto.AgregarProductoRequest;
import com.bodegaweb.carrito.dto.CarritoResponse;

public interface CarritoService {
    CarritoResponse obtenerPorUsuario(Long usuarioId);
    CarritoResponse agregarItem(Long usuarioId, AgregarProductoRequest request);
    CarritoResponse actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad);
    void eliminarItem(Long usuarioId, Long itemId);
    void vaciarCarrito(Long usuarioId);
}