package com.bodegaweb.pago.controller;

import com.bodegaweb.auth.AccesoService;
import com.bodegaweb.common.ApiResponse;
import com.bodegaweb.pago.dto.PagoRequest;
import com.bodegaweb.pago.dto.PagoResponse;
import com.bodegaweb.pago.enums.EstadoPago;
import com.bodegaweb.pago.service.PagoService;
import com.bodegaweb.pedido.service.PedidoService;
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
    private final PedidoService pedidoService;
    private final AccesoService acceso;

    public PagoController(PagoService service, PedidoService pedidoService, AccesoService acceso) {
        this.service = service;
        this.pedidoService = pedidoService;
        this.acceso = acceso;
    }

    @GetMapping
    public ApiResponse<List<PagoResponse>> listar() {
        acceso.soloAdmin();
        return ApiResponse.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ApiResponse<PagoResponse> obtener(@PathVariable Long id) {
        PagoResponse pago = service.obtener(id);
        verificarDuenoDelPedido(pago.pedidoId());
        return ApiResponse.ok(pago);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ApiResponse<PagoResponse> obtenerPorPedido(@PathVariable Long pedidoId) {
        verificarDuenoDelPedido(pedidoId);
        return ApiResponse.ok(service.obtenerPorPedido(pedidoId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponse>> crear(@Valid @RequestBody PagoRequest request) {
        verificarDuenoDelPedido(request.pedidoId());
        PagoResponse creado = service.crear(request);
        return ResponseEntity
                .created(URI.create("/api/pagos/" + creado.id()))
                .body(ApiResponse.ok("Pago registrado", creado));
    }

    @PatchMapping("/{id}/estado")
    public ApiResponse<PagoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPago estado) {
        acceso.soloAdmin();
        return ApiResponse.ok("Estado de pago actualizado", service.actualizarEstado(id, estado));
    }

    /** Un pago pertenece al usuario dueño de su pedido. */
    private void verificarDuenoDelPedido(Long pedidoId) {
        acceso.verificarPropietario(() -> pedidoService.obtener(pedidoId).usuarioId());
    }
}
