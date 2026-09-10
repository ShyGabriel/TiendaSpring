package com.bodegaweb.bodegaweb.producto;

import com.bodegaweb.bodegaweb.common.ApiResponse;
import com.bodegaweb.bodegaweb.producto.dto.AjusteStockRequest;
import com.bodegaweb.bodegaweb.producto.dto.InventarioRequest;
import com.bodegaweb.bodegaweb.producto.dto.InventarioResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inventario como sub-recurso de producto:
 * {@code /api/productos/{productoId}/inventario}.
 */
@RestController
@RequestMapping("/api/productos/{productoId}/inventario")
public class InventarioController {

    private final InventarioService service;

    public InventarioController(InventarioService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<InventarioResponse> obtener(@PathVariable Long productoId) {
        return ApiResponse.ok(service.obtener(productoId));
    }

    @PutMapping
    public ApiResponse<InventarioResponse> reemplazar(
            @PathVariable Long productoId,
            @Valid @RequestBody InventarioRequest req) {
        return ApiResponse.ok("Inventario actualizado", service.reemplazar(productoId, req));
    }

    @PatchMapping("/ajuste")
    public ApiResponse<InventarioResponse> ajustar(
            @PathVariable Long productoId,
            @Valid @RequestBody AjusteStockRequest req) {
        return ApiResponse.ok("Stock ajustado", service.ajustar(productoId, req));
    }
}
