package com.bodegaweb.bodegaweb.producto;

import com.bodegaweb.bodegaweb.categoria.Categoria;
import com.bodegaweb.bodegaweb.producto.dto.InventarioResponse;
import com.bodegaweb.bodegaweb.producto.dto.ProductoResponse;

final class ProductoMapper {

    private ProductoMapper() {
    }

    static InventarioResponse toResponse(Inventario inv) {
        if (inv == null) {
            return null;
        }
        int disp = inv.getStockDisponible() != null ? inv.getStockDisponible() : 0;
        int res = inv.getStockReservado() != null ? inv.getStockReservado() : 0;
        return new InventarioResponse(
                inv.getProducto() != null ? inv.getProducto().getId() : null,
                disp,
                res,
                disp + res,
                inv.getActualizadoEn()
        );
    }

    static ProductoResponse toResponse(Producto p) {
        Categoria cat = p.getCategoria();
        return new ProductoResponse(
                p.getId(),
                p.getSku(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                cat != null ? cat.getId() : null,
                cat != null ? cat.getNombre() : null,
                p.getImagenUrl(),
                p.getActivo(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                toResponse(p.getInventario())
        );
    }
}
