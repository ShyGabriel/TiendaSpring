package com.bodegaweb.carrito.service;

import com.bodegaweb.carrito.dto.AgregarProductoRequest;
import com.bodegaweb.carrito.dto.CarritoItemResponse;
import com.bodegaweb.carrito.dto.CarritoResponse;
import com.bodegaweb.carrito.entity.Carrito;
import com.bodegaweb.carrito.entity.CarritoItem;
import com.bodegaweb.carrito.repository.CarritoItemRepository;
import com.bodegaweb.carrito.repository.CarritoRepository;
import com.bodegaweb.catalogo.producto.Producto;
import com.bodegaweb.catalogo.producto.ProductoRepository;
import com.bodegaweb.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository,
                               CarritoItemRepository carritoItemRepository,
                               ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public CarritoResponse obtenerPorUsuario(Long usuarioId) {
        return toResponse(obtenerOCrearCarrito(usuarioId));
    }

    @Override
    @Transactional
    public CarritoResponse agregarItem(Long usuarioId, AgregarProductoRequest request) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        CarritoItem item = carritoItemRepository
                .findByCarritoIdAndProductoId(carrito.getId(), request.productoId())
                .orElseGet(() -> {
                    CarritoItem nuevo = new CarritoItem();
                    nuevo.setCarrito(carrito);
                    nuevo.setProductoId(request.productoId());
                    nuevo.setCantidad(0);
                    Producto producto = productoRepository.findById(request.productoId())
                            .orElseThrow(() -> new ResourceNotFoundException("Producto", request.productoId()));
                    nuevo.setPrecioUnitario(producto.getPrecio());
                    return nuevo;
                });

        item.setCantidad(item.getCantidad() + request.cantidad());
        carritoItemRepository.save(item);

        return toResponse(carritoRepository.findById(carrito.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public CarritoResponse actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("CarritoItem no encontrado: " + itemId));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new NoSuchElementException("CarritoItem no encontrado: " + itemId);
        }

        item.setCantidad(cantidad);
        carritoItemRepository.save(item);
        return toResponse(carritoRepository.findById(carrito.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public void eliminarItem(Long usuarioId, Long itemId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("CarritoItem no encontrado: " + itemId));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new NoSuchElementException("CarritoItem no encontrado: " + itemId);
        }
        carritoItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    private Carrito obtenerOCrearCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuarioId(usuarioId);
                    return carritoRepository.save(nuevo);
                });
    }

    private CarritoResponse toResponse(Carrito carrito) {
        List<CarritoItemResponse> items = carrito.getItems().stream()
                .map(i -> new CarritoItemResponse(
                        i.getId(),
                        i.getProductoId(),
                        i.getCantidad(),
                        i.getPrecioUnitario(),
                        i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad()))
                ))
                .toList();

        BigDecimal total = items.stream()
                .map(CarritoItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CarritoResponse(carrito.getId(), carrito.getUsuarioId(), items, total);
    }
}