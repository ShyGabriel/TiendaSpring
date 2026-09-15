package com.bodegaweb.catalogo.producto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bodegaweb.catalogo.categoria.Categoria;
import com.bodegaweb.catalogo.categoria.CategoriaRepository;
import com.bodegaweb.catalogo.producto.dto.InventarioRequest;
import com.bodegaweb.catalogo.producto.dto.ProductoRequest;
import com.bodegaweb.catalogo.producto.dto.ProductoResponse;
import com.bodegaweb.common.exception.DuplicateResourceException;
import com.bodegaweb.common.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests unitarios de {@link ProductoService} con Mockito. */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    private static final BigDecimal PRECIO = new BigDecimal("29.90");

    @Mock
    private ProductoRepository repository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService service;

    @Test
    void crearRechazaSkuDuplicado() {
        when(repository.existsBySku("SKU-1")).thenReturn(true);

        ProductoRequest req = new ProductoRequest("SKU-1", "Mouse", null, PRECIO, null, null, null, null);

        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(DuplicateResourceException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void crearConCategoriaInexistenteLanzaNotFound() {
        when(repository.existsBySku("SKU-1")).thenReturn(false);
        when(categoriaRepository.findById(50L)).thenReturn(Optional.empty());

        ProductoRequest req = new ProductoRequest("SKU-1", "Mouse", null, PRECIO, 50L, null, null, null);

        assertThatThrownBy(() -> service.crear(req))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void crearSinInventarioInicialQuedaEnCero() {
        when(repository.existsBySku("SKU-1")).thenReturn(false);
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoRequest req = new ProductoRequest("SKU-1", "Mouse", null, PRECIO, null, null, null, null);
        ProductoResponse resp = service.crear(req);

        assertThat(resp.inventario().stockDisponible()).isZero();
        assertThat(resp.inventario().stockReservado()).isZero();
    }

    @Test
    void crearConInventarioInicialExplicitoLoRespeta() {
        when(repository.existsBySku("SKU-1")).thenReturn(false);
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoRequest req = new ProductoRequest(
                "SKU-1", "Mouse", null, PRECIO, null, null, null, new InventarioRequest(8, 2));
        ProductoResponse resp = service.crear(req);

        assertThat(resp.inventario().stockDisponible()).isEqualTo(8);
        assertThat(resp.inventario().stockReservado()).isEqualTo(2);
    }

    @Test
    void crearAsignaLaCategoriaCuandoExiste() {
        Categoria cat = new Categoria();
        cat.setId(3L);
        cat.setNombre("Perifericos");
        when(repository.existsBySku("SKU-1")).thenReturn(false);
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(cat));
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoRequest req = new ProductoRequest("SKU-1", "Mouse", null, PRECIO, 3L, null, null, null);
        ProductoResponse resp = service.crear(req);

        assertThat(resp.categoriaId()).isEqualTo(3L);
    }

    @Test
    void actualizarRechazaSkuDuplicadoDeOtroProducto() {
        Producto existente = new Producto();
        existente.setId(7L);
        existente.setSku("SKU-OLD");
        when(repository.findById(7L)).thenReturn(Optional.of(existente));
        when(repository.existsBySkuAndIdNot("SKU-NEW", 7L)).thenReturn(true);

        ProductoRequest req = new ProductoRequest("SKU-NEW", "Mouse", null, PRECIO, null, null, null, null);

        assertThatThrownBy(() -> service.actualizar(7L, req))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void actualizarProductoInexistenteLanzaNotFound() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        ProductoRequest req = new ProductoRequest("SKU-NEW", "Mouse", null, PRECIO, null, null, null, null);

        assertThatThrownBy(() -> service.actualizar(7L, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void obtenerPorSkuLanzaNotFoundSiNoExiste() {
        when(repository.findBySku("NOPE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorSku("NOPE"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void cambiarEstadoActualizaElFlagActivo() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setActivo(true);
        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse resp = service.cambiarEstado(1L, false);

        assertThat(resp.activo()).isFalse();
    }

    @Test
    void noConsultaCategoriaRepositorySiCategoriaIdEsNulo() {
        when(repository.existsBySku("SKU-1")).thenReturn(false);
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoRequest req = new ProductoRequest("SKU-1", "Mouse", null, PRECIO, null, null, null, null);
        service.crear(req);

        verify(categoriaRepository, never()).findById(any());
    }
}
