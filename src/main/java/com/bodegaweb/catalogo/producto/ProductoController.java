package com.bodegaweb.catalogo.producto;

import com.bodegaweb.common.ApiResponse;
import com.bodegaweb.common.PagedResponse;
import com.bodegaweb.catalogo.producto.dto.ProductoRequest;
import com.bodegaweb.catalogo.producto.dto.ProductoResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PagedResponse<ProductoResponse>> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(
                PagedResponse.of(service.listar(categoriaId, activo, q, pageable)));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductoResponse> obtener(@PathVariable Long id) {
        return ApiResponse.ok(service.obtener(id));
    }

    @GetMapping("/sku/{sku}")
    public ApiResponse<ProductoResponse> obtenerPorSku(@PathVariable String sku) {
        return ApiResponse.ok(service.obtenerPorSku(sku));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(@Valid @RequestBody ProductoRequest req) {
        ProductoResponse creado = service.crear(req);
        return ResponseEntity
                .created(URI.create("/api/productos/" + creado.id()))
                .body(ApiResponse.ok("Producto creado", creado));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest req) {
        return ApiResponse.ok("Producto actualizado", service.actualizar(id, req));
    }

    @PatchMapping("/{id}/estado")
    public ApiResponse<ProductoResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        return ApiResponse.ok("Estado actualizado", service.cambiarEstado(id, activo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
