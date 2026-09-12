package com.resena.controller;

import com.bodegaweb.bodegaweb.common.ApiResponse;
import com.resena.dto.ResenaRequest;
import com.resena.dto.ResenaResponse;
import com.resena.service.ResenaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    private final ResenaService service;

    public ResenaController(ResenaService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<ResenaResponse>> listar(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) Long usuarioId) {
        if (productoId != null) {
            return ApiResponse.ok(service.listarPorProducto(productoId));
        }
        if (usuarioId != null) {
            return ApiResponse.ok(service.listarPorUsuario(usuarioId));
        }
        return ApiResponse.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ApiResponse<ResenaResponse> obtener(@PathVariable Long id) {
        return ApiResponse.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ResenaResponse>> crear(@Valid @RequestBody ResenaRequest request) {
        ResenaResponse creada = service.crear(request);
        return ResponseEntity
                .created(URI.create("/api/resenas/" + creada.id()))
                .body(ApiResponse.ok("Reseña creada", creada));
    }

    @PutMapping("/{id}")
    public ApiResponse<ResenaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ResenaRequest request) {
        return ApiResponse.ok("Reseña actualizada", service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
