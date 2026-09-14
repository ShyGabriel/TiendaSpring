package com.bodegaweb.bodegaweb.pedido.Controller;

import com.bodegaweb.bodegaweb.pedido.Service.PedidoService;
import com.bodegaweb.bodegaweb.pedido.dto.PedidoRequest;
import com.bodegaweb.bodegaweb.pedido.dto.PedidoResponse;
import com.bodegaweb.bodegaweb.pedido.entity.EstadoPedido;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse creado = service.crearDesdeCarrito(request);
        return ResponseEntity.created(URI.create("/api/pedidos/" + creado.id())).body(creado);
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<PedidoResponse> listarPorUsuario(@PathVariable Long usuarioId) {
        return service.listarPorUsuario(usuarioId);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse actualizarEstado(@PathVariable Long id, @RequestParam EstadoPedido estado) {
        return service.actualizarEstado(id, estado);
    }
}