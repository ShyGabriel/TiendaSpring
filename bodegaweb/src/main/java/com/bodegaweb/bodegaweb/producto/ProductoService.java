package com.bodegaweb.bodegaweb.producto;

import com.bodegaweb.bodegaweb.categoria.Categoria;
import com.bodegaweb.bodegaweb.categoria.CategoriaRepository;
import com.bodegaweb.bodegaweb.common.exception.DuplicateResourceException;
import com.bodegaweb.bodegaweb.common.exception.ResourceNotFoundException;
import com.bodegaweb.bodegaweb.producto.dto.InventarioRequest;
import com.bodegaweb.bodegaweb.producto.dto.ProductoRequest;
import com.bodegaweb.bodegaweb.producto.dto.ProductoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository repository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository repository, CategoriaRepository categoriaRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponse> listar(Long categoriaId, Boolean activo, String q, Pageable pageable) {
        String termino = StringUtils.hasText(q) ? q.trim() : null;
        return repository.buscar(categoriaId, activo, termino, pageable)
                .map(ProductoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return ProductoMapper.toResponse(buscar(id));
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorSku(String sku) {
        Producto producto = repository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "sku=" + sku));
        return ProductoMapper.toResponse(producto);
    }

    public ProductoResponse crear(ProductoRequest req) {
        String sku = req.sku().trim();
        if (repository.existsBySku(sku)) {
            throw new DuplicateResourceException("Producto", "sku", sku);
        }

        Producto producto = new Producto();
        producto.setSku(sku);
        aplicar(producto, req);

        Inventario inventario = new Inventario();
        InventarioRequest inicial = req.inventarioInicial();
        inventario.setStockDisponible(inicial != null ? inicial.stockDisponible() : 0);
        inventario.setStockReservado(inicial != null ? inicial.stockReservado() : 0);
        producto.asignarInventario(inventario);

        return ProductoMapper.toResponse(repository.save(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest req) {
        Producto producto = buscar(id);

        String sku = req.sku().trim();
        if (repository.existsBySkuAndIdNot(sku, id)) {
            throw new DuplicateResourceException("Producto", "sku", sku);
        }
        producto.setSku(sku);
        aplicar(producto, req);

        return ProductoMapper.toResponse(repository.save(producto));
    }

    public ProductoResponse cambiarEstado(Long id, boolean activo) {
        Producto producto = buscar(id);
        producto.setActivo(activo);
        return ProductoMapper.toResponse(repository.save(producto));
    }

    public void eliminar(Long id) {
        Producto producto = buscar(id);
        repository.delete(producto);
    }

    // ---------------------------------------------------------------------

    Producto buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }

    private void aplicar(Producto producto, ProductoRequest req) {
        producto.setNombre(req.nombre().trim());
        producto.setDescripcion(req.descripcion());
        producto.setPrecio(req.precio());
        producto.setImagenUrl(req.imagenUrl());
        producto.setActivo(req.activo() == null ? Boolean.TRUE : req.activo());
        producto.setCategoria(resolverCategoria(req.categoriaId()));
    }

    private Categoria resolverCategoria(Long categoriaId) {
        if (categoriaId == null) {
            return null;
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", categoriaId));
    }
}
