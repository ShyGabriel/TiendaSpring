package com.bodegaweb.pedido.controller;

import com.bodegaweb.auth.AccesoService;
import com.bodegaweb.direccion.service.DireccionService;
import com.bodegaweb.pedido.service.PedidoService;
import com.bodegaweb.pedido.dto.PedidoRequest;
import com.bodegaweb.pedido.dto.PedidoResponse;
import com.bodegaweb.pedido.entity.EstadoPedido;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;
    private final DireccionService direccionService;
    private final AccesoService acceso;

    public PedidoController(PedidoService service, DireccionService direccionService, AccesoService acceso) {
        this.service = service;
        this.direccionService = direccionService;
        this.acceso = acceso;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        acceso.verificarPropietario(request.usuarioId());
        // La dirección de envío también debe ser del usuario.
        acceso.verificarPropietario(() -> direccionService.findById(request.direccionId()).usuarioId());
        PedidoResponse creado = service.crearDesdeCarrito(request);
        return ResponseEntity.created(URI.create("/api/pedidos/" + creado.id())).body(creado);
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id) {
        PedidoResponse pedido = service.obtener(id);
        acceso.verificarPropietario(pedido.usuarioId());
        return pedido;
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<PedidoResponse> listarPorUsuario(@PathVariable Long usuarioId) {
        acceso.verificarPropietario(usuarioId);
        return service.listarPorUsuario(usuarioId);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse actualizarEstado(@PathVariable Long id, @RequestParam EstadoPedido estado) {
        acceso.soloAdmin();
        return service.actualizarEstado(id, estado);
    }
}
