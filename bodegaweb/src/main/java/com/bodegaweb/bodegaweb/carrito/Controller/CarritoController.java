package com.bodegaweb.bodegaweb.carrito.Controller;

import com.bodegaweb.bodegaweb.carrito.Service.CarritoService;
import com.bodegaweb.bodegaweb.carrito.dto.AgregarProductoRequest;
import com.bodegaweb.bodegaweb.carrito.dto.CarritoResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @GetMapping("/usuario/{usuarioId}")
    public CarritoResponse obtener(@PathVariable Long usuarioId) {
        return service.obtenerPorUsuario(usuarioId);
    }

    @PostMapping("/usuario/{usuarioId}/items")
    public CarritoResponse agregarItem(@PathVariable Long usuarioId,
                                        @Valid @RequestBody AgregarProductoRequest request) {
        return service.agregarItem(usuarioId, request);
    }

    @PatchMapping("/usuario/{usuarioId}/items/{itemId}")
    public CarritoResponse actualizarCantidad(@PathVariable Long usuarioId,
                                               @PathVariable Long itemId,
                                               @RequestParam Integer cantidad) {
        return service.actualizarCantidad(usuarioId, itemId, cantidad);
    }

    @DeleteMapping("/usuario/{usuarioId}/items/{itemId}")
    public void eliminarItem(@PathVariable Long usuarioId, @PathVariable Long itemId) {
        service.eliminarItem(usuarioId, itemId);
    }

    @DeleteMapping("/usuario/{usuarioId}")
    public void vaciar(@PathVariable Long usuarioId) {
        service.vaciarCarrito(usuarioId);
    }
}