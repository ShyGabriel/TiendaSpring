package com.bodegaweb.catalogo.categoria;

import com.bodegaweb.catalogo.categoria.dto.CategoriaRequest;
import com.bodegaweb.catalogo.categoria.dto.CategoriaResponse;
import com.bodegaweb.catalogo.categoria.dto.CategoriaTreeResponse;
import com.bodegaweb.common.ApiResponse;
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
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    /** Sin {@code padreId}: categorías raíz. Con {@code padreId}: hijas directas de esa categoría. */
    @GetMapping
    public ApiResponse<List<CategoriaResponse>> listar(
            @RequestParam(name = "padreId", required = false) Long padreId) {
        return ApiResponse.ok(service.listar(padreId));
    }

    @GetMapping("/arbol")
    public ApiResponse<List<CategoriaTreeResponse>> arbol() {
        return ApiResponse.ok(service.arbol());
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoriaResponse> obtener(@PathVariable Long id) {
        return ApiResponse.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponse>> crear(@Valid @RequestBody CategoriaRequest req) {
        CategoriaResponse creada = service.crear(req);
        return ResponseEntity
                .created(URI.create("/api/categorias/" + creada.id()))
                .body(ApiResponse.ok("Categoría creada", creada));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoriaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest req) {
        return ApiResponse.ok("Categoría actualizada", service.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
