package com.bodegaweb.catalogo.categoria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bodegaweb.catalogo.categoria.dto.CategoriaRequest;
import com.bodegaweb.catalogo.categoria.dto.CategoriaResponse;
import com.bodegaweb.catalogo.producto.Producto;
import com.bodegaweb.catalogo.producto.ProductoRepository;
import com.bodegaweb.common.exception.BusinessException;
import com.bodegaweb.common.exception.DuplicateResourceException;
import com.bodegaweb.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests unitarios de {@link CategoriaService} con Mockito (no levantan contexto de
 * Spring, corren en milisegundos). Complementan a CatalogoFlowTests, que solo
 * ejercita el camino feliz vía HTTP.
 */
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CategoriaService service;

    @Test
    void listarSinPadreIdDevuelveSoloRaicesOrdenadas() {
        Categoria raiz = categoria(1L, "Ropa", null);
        when(repository.findByCategoriaPadreIsNullOrderByNombreAsc()).thenReturn(List.of(raiz));

        List<CategoriaResponse> resp = service.listar(null);

        assertThat(resp).extracting(CategoriaResponse::id).containsExactly(1L);
        verify(repository, never()).findAll();
    }

    @Test
    void listarConPadreIdDevuelveSusHijasDirectas() {
        Categoria padre = categoria(1L, "Ropa", null);
        Categoria hijo = categoria(2L, "Camisas", padre);
        when(repository.findByCategoriaPadreIdOrderByNombreAsc(1L)).thenReturn(List.of(hijo));

        List<CategoriaResponse> resp = service.listar(1L);

        assertThat(resp).extracting(CategoriaResponse::id).containsExactly(2L);
    }

    @Test
    void crearGeneraSlugDesdeElNombreCuandoNoSeEnvia() {
        when(repository.existsBySlug("television-4k")).thenReturn(false);
        when(repository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoriaResponse resp = service.crear(new CategoriaRequest("Televisión 4K", null, null));

        assertThat(resp.slug()).isEqualTo("television-4k");
    }

    @Test
    void crearRechazaSlugYaExistente() {
        when(repository.existsBySlug("electronica")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(new CategoriaRequest("Electrónica", null, null)))
                .isInstanceOf(DuplicateResourceException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void crearConCategoriaPadreInexistenteLanzaNotFound() {
        when(repository.existsBySlug("laptops")).thenReturn(false);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.crear(new CategoriaRequest("Laptops", null, 99L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void actualizarRechazaQueLaCategoriaSeaSuPropioPadre() {
        Categoria actual = categoria(5L, "Ropa", null);
        when(repository.findById(5L)).thenReturn(Optional.of(actual));

        assertThatThrownBy(() -> service.actualizar(5L, new CategoriaRequest("Ropa", null, 5L)))
                .isInstanceOf(BusinessException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void actualizarDetectaCicloCuandoElNuevoPadreEsDescendiente() {
        // raiz(1) -> hijo(2) -> nieto(3); mover raiz bajo nieto formaria un ciclo.
        Categoria raiz = categoria(1L, "Raíz", null);
        Categoria hijo = categoria(2L, "Hijo", raiz);
        Categoria nieto = categoria(3L, "Nieto", hijo);

        when(repository.findById(1L)).thenReturn(Optional.of(raiz));
        when(repository.findById(3L)).thenReturn(Optional.of(nieto));

        assertThatThrownBy(() -> service.actualizar(1L, new CategoriaRequest("Raíz", null, 3L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("descendiente");
    }

    @Test
    void obtenerLanzaNotFoundSiNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void eliminarDesvinculaSubcategoriasAntesDeBorrar() {
        Categoria padre = categoria(1L, "Padre", null);
        Categoria hijo = categoria(2L, "Hijo", padre);
        when(repository.findById(1L)).thenReturn(Optional.of(padre));
        when(repository.findByCategoriaPadreIdOrderByNombreAsc(1L)).thenReturn(List.of(hijo));
        when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of());

        service.eliminar(1L);

        assertThat(hijo.getCategoriaPadre()).isNull();
        verify(repository).saveAll(List.of(hijo));
        verify(repository).delete(padre);
    }

    @Test
    void eliminarDesvinculaProductosAntesDeBorrar() {
        Categoria categoria = categoria(1L, "Accesorios", null);
        Producto producto = new Producto();
        producto.setId(10L);
        producto.setCategoria(categoria);

        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        when(repository.findByCategoriaPadreIdOrderByNombreAsc(1L)).thenReturn(List.of());
        when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(producto));

        service.eliminar(1L);

        assertThat(producto.getCategoria()).isNull();
        verify(productoRepository).saveAll(List.of(producto));
        verify(repository).delete(categoria);
    }

    // ---------------------------------------------------------------------

    private static Categoria categoria(Long id, String nombre, Categoria padre) {
        Categoria c = new Categoria();
        c.setId(id);
        c.setNombre(nombre);
        c.setSlug(nombre.toLowerCase(Locale.ROOT));
        c.setCategoriaPadre(padre);
        return c;
    }
}
