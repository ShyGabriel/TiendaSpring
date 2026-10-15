package com.bodegaweb.catalogo.producto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bodegaweb.catalogo.producto.dto.AjusteStockRequest;
import com.bodegaweb.catalogo.producto.dto.AjusteStockRequest.TipoStock;
import com.bodegaweb.catalogo.producto.dto.InventarioRequest;
import com.bodegaweb.catalogo.producto.dto.InventarioResponse;
import com.bodegaweb.common.exception.BusinessException;
import com.bodegaweb.common.exception.ResourceNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests unitarios de {@link InventarioService}: ajustes de stock y alta perezosa. */
@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService service;

    @Test
    void ajustarDisponibleDentroDeRangoDescuentaCorrectamente() {
        Inventario inv = inventario(20, 0);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inv));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(a -> a.getArgument(0));

        InventarioResponse resp = service.ajustar(1L, new AjusteStockRequest(TipoStock.DISPONIBLE, -5));

        assertThat(resp.stockDisponible()).isEqualTo(15);
    }

    @Test
    void ajustarDisponibleQueDejariaStockNegativoLanzaBusinessException() {
        Inventario inv = inventario(3, 0);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inv));

        assertThatThrownBy(() -> service.ajustar(1L, new AjusteStockRequest(TipoStock.DISPONIBLE, -10)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void ajustarReservadoQueDejariaStockNegativoLanzaBusinessException() {
        Inventario inv = inventario(10, 2);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inv));

        assertThatThrownBy(() -> service.ajustar(1L, new AjusteStockRequest(TipoStock.RESERVADO, -3)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void ajustarSinInventarioExistenteLanzaNotFound() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ajustar(1L, new AjusteStockRequest(TipoStock.DISPONIBLE, 1)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void reemplazarConProductoSinInventarioLoCreaDesdeCero() {
        Producto producto = new Producto();
        producto.setId(9L);
        when(inventarioRepository.findByProductoId(9L)).thenReturn(Optional.empty());
        when(productoRepository.findById(9L)).thenReturn(Optional.of(producto));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(a -> a.getArgument(0));

        InventarioResponse resp = service.reemplazar(9L, new InventarioRequest(12, 4));

        assertThat(resp.stockDisponible()).isEqualTo(12);
        assertThat(resp.stockReservado()).isEqualTo(4);
    }

    @Test
    void reemplazarConProductoInexistenteLanzaNotFound() {
        when(inventarioRepository.findByProductoId(9L)).thenReturn(Optional.empty());
        when(productoRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reemplazar(9L, new InventarioRequest(1, 0)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void reemplazarSobreescribeValoresAbsolutosDeUnInventarioExistente() {
        Inventario inv = inventario(5, 1);
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inv));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(a -> a.getArgument(0));

        InventarioResponse resp = service.reemplazar(1L, new InventarioRequest(100, 50));

        assertThat(resp.stockDisponible()).isEqualTo(100);
        assertThat(resp.stockReservado()).isEqualTo(50);
    }

    private static Inventario inventario(int disponible, int reservado) {
        Producto producto = new Producto();
        producto.setId(1L);
        Inventario inv = new Inventario();
        inv.setProducto(producto);
        inv.setStockDisponible(disponible);
        inv.setStockReservado(reservado);
        return inv;
    }
}
