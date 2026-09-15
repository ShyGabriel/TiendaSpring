package com.bodegaweb.pedido.service;

import com.bodegaweb.carrito.entity.Carrito;
import com.bodegaweb.carrito.entity.CarritoItem;
import com.bodegaweb.carrito.repository.CarritoRepository;
import com.bodegaweb.pedido.dto.PedidoItemResponse;
import com.bodegaweb.pedido.dto.PedidoRequest;
import com.bodegaweb.pedido.dto.PedidoResponse;
import com.bodegaweb.pedido.entity.EstadoPedido;
import com.bodegaweb.pedido.entity.Pedido;
import com.bodegaweb.pedido.entity.PedidoItem;
import com.bodegaweb.pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                              CarritoRepository carritoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carritoRepository = carritoRepository;
    }

    @Override
    @Transactional
    public PedidoResponse crearDesdeCarrito(PedidoRequest request) {
        Carrito carrito = carritoRepository.findByUsuarioId(request.usuarioId())
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado para usuario: " + request.usuarioId()));

        if (carrito.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío, no se puede generar el pedido");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(request.usuarioId());
        pedido.setDireccionId(request.direccionId());
        pedido.setEstado(EstadoPedido.PENDIENTE);

        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem ci : carrito.getItems()) {
            PedidoItem pi = new PedidoItem();
            pi.setPedido(pedido);
            pi.setProductoId(ci.getProductoId());
            pi.setCantidad(ci.getCantidad());
            pi.setPrecioUnitario(ci.getPrecioUnitario());
            pedido.getItems().add(pi);
            total = total.add(ci.getPrecioUnitario().multiply(BigDecimal.valueOf(ci.getCantidad())));
        }
        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);

        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return toResponse(guardado);
    }

    @Override
    public PedidoResponse obtener(Long id) {
        return toResponse(pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + id)));
    }

    @Override
    public List<PedidoResponse> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PedidoResponse actualizarEstado(Long id, EstadoPedido estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + id));
        pedido.setEstado(estado);
        return toResponse(pedidoRepository.save(pedido));
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<PedidoItemResponse> items = pedido.getItems().stream()
                .map(i -> new PedidoItemResponse(
                        i.getId(),
                        i.getProductoId(),
                        i.getCantidad(),
                        i.getPrecioUnitario(),
                        i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad()))
                ))
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getUsuarioId(),
                pedido.getDireccionId(),
                pedido.getEstado(),
                pedido.getTotal(),
                pedido.getFechaPedido(),
                items
        );
    }
}