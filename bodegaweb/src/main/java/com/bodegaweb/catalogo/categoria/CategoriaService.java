package com.bodegaweb.catalogo.categoria;

import com.bodegaweb.catalogo.categoria.dto.CategoriaRequest;
import com.bodegaweb.catalogo.categoria.dto.CategoriaResponse;
import com.bodegaweb.catalogo.categoria.dto.CategoriaTreeResponse;
import com.bodegaweb.catalogo.producto.Producto;
import com.bodegaweb.catalogo.producto.ProductoRepository;
import com.bodegaweb.common.exception.BusinessException;
import com.bodegaweb.common.exception.DuplicateResourceException;
import com.bodegaweb.common.exception.ResourceNotFoundException;
import com.bodegaweb.common.util.SlugUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository repository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository repository, ProductoRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar(Long padreId) {
        List<Categoria> categorias = (padreId == null)
                ? repository.findAll()
                : repository.findByCategoriaPadreIdOrderByNombreAsc(padreId);
        return categorias.stream().map(CategoriaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoriaTreeResponse> arbol() {
        List<Categoria> todas = repository.findAll();
        Map<Long, List<Categoria>> hijosPorPadre = todas.stream()
                .filter(c -> c.getCategoriaPadre() != null)
                .collect(Collectors.groupingBy(c -> c.getCategoriaPadre().getId()));
        return todas.stream()
                .filter(c -> c.getCategoriaPadre() == null)
                .sorted((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()))
                .map(raiz -> CategoriaMapper.toTree(raiz, hijosPorPadre))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return CategoriaMapper.toResponse(buscar(id));
    }

    public CategoriaResponse crear(CategoriaRequest req) {
        String slug = resolverSlug(req.slug(), req.nombre());
        if (repository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Categoría", "slug", slug);
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(req.nombre().trim());
        categoria.setSlug(slug);
        categoria.setCategoriaPadre(resolverPadre(req.categoriaPadreId(), null));

        return CategoriaMapper.toResponse(repository.save(categoria));
    }

    public CategoriaResponse actualizar(Long id, CategoriaRequest req) {
        Categoria categoria = buscar(id);

        String slug = resolverSlug(req.slug(), req.nombre());
        if (repository.existsBySlugAndIdNot(slug, id)) {
            throw new DuplicateResourceException("Categoría", "slug", slug);
        }

        categoria.setNombre(req.nombre().trim());
        categoria.setSlug(slug);
        categoria.setCategoriaPadre(resolverPadre(req.categoriaPadreId(), id));

        return CategoriaMapper.toResponse(repository.save(categoria));
    }

    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        // Réplica en aplicación del comportamiento previsto por el esquema para las FK:
        //   categorias.categoria_padre_id  -> ON DELETE SET NULL
        //   productos.categoria_id         -> ON DELETE SET NULL
        // No se asume que la FK generada por Hibernate (ddl-auto=update) tenga esa regla,
        // así que se desvincula explícitamente antes de borrar.
        List<Categoria> hijos = repository.findByCategoriaPadreIdOrderByNombreAsc(id);
        hijos.forEach(h -> h.setCategoriaPadre(null));
        repository.saveAll(hijos);

        List<Producto> productos = productoRepository.findByCategoriaId(id);
        productos.forEach(p -> p.setCategoria(null));
        productoRepository.saveAll(productos);

        repository.flush();
        productoRepository.flush();

        repository.delete(categoria);
    }

    // ---------------------------------------------------------------------

    private Categoria buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
    }

    private String resolverSlug(String slugEntrada, String nombre) {
        String base = (slugEntrada != null && !slugEntrada.isBlank()) ? slugEntrada : nombre;
        String slug = SlugUtils.slugify(base);
        if (slug.isBlank()) {
            throw new BusinessException("No se pudo generar un slug válido a partir de '" + base + "'");
        }
        return slug;
    }

    /**
     * Resuelve la categoría padre y valida que no se forme un ciclo
     * (una categoría no puede ser su propio ancestro).
     */
    private Categoria resolverPadre(Long padreId, Long idActual) {
        if (padreId == null) {
            return null;
        }
        if (padreId.equals(idActual)) {
            throw new BusinessException("Una categoría no puede ser su propia padre");
        }
        Categoria padre = repository.findById(padreId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría padre", padreId));

        if (idActual != null) {
            Categoria cursor = padre;
            while (cursor != null) {
                if (idActual.equals(cursor.getId())) {
                    throw new BusinessException(
                            "Movimiento inválido: la categoría padre es descendiente de la categoría actual");
                }
                cursor = cursor.getCategoriaPadre();
            }
        }
        return padre;
    }
}
