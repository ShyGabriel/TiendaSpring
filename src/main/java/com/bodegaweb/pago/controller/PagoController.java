package com.bodegaweb.pago.controller;

import com.bodegaweb.common.ApiResponse;
import com.bodegaweb.pago.dto.PagoRequest;
import com.bodegaweb.pago.dto.PagoResponse;
import com.bodegaweb.pago.enums.EstadoPago;
import com.bodegaweb.pago.service.PagoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<PagoResponse>> listar() {
        return ApiResponse.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ApiResponse<PagoResponse> obtener(@PathVariable Long id) {
        return ApiResponse.ok(service.obtener(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ApiResponse<PagoResponse> obtenerPorPedido(@PathVariable Long pedidoId) {
        return ApiResponse.ok(service.obtenerPorPedido(pedidoId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponse>> crear(@Valid @RequestBody PagoRequest request) {
        PagoResponse creado = service.crear(request);
        return ResponseEntity
                .created(URI.create("/api/pagos/" + creado.id()))
                .body(ApiResponse.ok("Pago registrado", creado));
    }

    @PatchMapping("/{id}/estado")
    public ApiResponse<PagoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPago estado) {
        return ApiResponse.ok("Estado de pago actualizado", service.actualizarEstado(id, estado));
    }
}
