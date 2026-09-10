package com.bodegaweb.bodegaweb.producto;

import com.bodegaweb.bodegaweb.common.exception.BusinessException;
import com.bodegaweb.bodegaweb.common.exception.ResourceNotFoundException;
import com.bodegaweb.bodegaweb.producto.dto.AjusteStockRequest;
import com.bodegaweb.bodegaweb.producto.dto.InventarioRequest;
import com.bodegaweb.bodegaweb.producto.dto.InventarioResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    public InventarioService(ProductoRepository productoRepository,
                             InventarioRepository inventarioRepository) {
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Transactional(readOnly = true)
    public InventarioResponse obtener(Long productoId) {
        return ProductoMapper.toResponse(buscar(productoId));
    }

    /** PUT: fija valores absolutos. Crea el inventario si aún no existía. */
    public InventarioResponse reemplazar(Long productoId, InventarioRequest req) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseGet(() -> crearVacio(productoId));
        inventario.setStockDisponible(req.stockDisponible());
        inventario.setStockReservado(req.stockReservado());
        return ProductoMapper.toResponse(inventarioRepository.save(inventario));
    }

    /** PATCH: ajuste relativo; no permite dejar el stock por debajo de cero. */
    public InventarioResponse ajustar(Long productoId, AjusteStockRequest req) {
        Inventario inventario = buscar(productoId);
        int delta = req.delta();

        switch (req.tipo()) {
            case DISPONIBLE -> {
                int nuevo = inventario.getStockDisponible() + delta;
                if (nuevo < 0) {
                    throw new BusinessException(
                            "El ajuste dejaría stock_disponible en " + nuevo + " (no permitido)");
                }
                inventario.setStockDisponible(nuevo);
            }
            case RESERVADO -> {
                int nuevo = inventario.getStockReservado() + delta;
                if (nuevo < 0) {
                    throw new BusinessException(
                            "El ajuste dejaría stock_reservado en " + nuevo + " (no permitido)");
                }
                inventario.setStockReservado(nuevo);
            }
        }
        return ProductoMapper.toResponse(inventarioRepository.save(inventario));
    }

    // ---------------------------------------------------------------------

    private Inventario buscar(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventario", "productoId=" + productoId));
    }

    private Inventario crearVacio(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productoId));
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setStockDisponible(0);
        inventario.setStockReservado(0);
        return inventario;
    }
}
