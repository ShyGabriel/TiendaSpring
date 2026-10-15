package com.bodegaweb.catalogo.categoria;

import com.bodegaweb.catalogo.categoria.dto.CategoriaResponse;
import com.bodegaweb.catalogo.categoria.dto.CategoriaTreeResponse;
import java.util.List;
import java.util.Map;

final class CategoriaMapper {

    private CategoriaMapper() {
    }

    static CategoriaResponse toResponse(Categoria c) {
        Categoria padre = c.getCategoriaPadre();
        return new CategoriaResponse(
                c.getId(),
                c.getNombre(),
                c.getSlug(),
                padre != null ? padre.getId() : null,
                padre != null ? padre.getNombre() : null,
                c.getCreatedAt()
        );
    }

    /**
     * Construye el árbol a partir de todas las categorías ya cargadas,
     * agrupadas por id de padre (evita N+1).
     */
    static CategoriaTreeResponse toTree(Categoria c, Map<Long, List<Categoria>> hijosPorPadre) {
        List<CategoriaTreeResponse> hijos = hijosPorPadre.getOrDefault(c.getId(), List.of()).stream()
                .map(h -> toTree(h, hijosPorPadre))
                .toList();
        return new CategoriaTreeResponse(
                c.getId(),
                c.getNombre(),
                c.getSlug(),
                c.getCategoriaPadre() != null ? c.getCategoriaPadre().getId() : null,
                hijos
        );
    }
}
