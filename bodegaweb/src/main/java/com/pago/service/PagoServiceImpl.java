package com.pago.service;

import com.bodegaweb.bodegaweb.common.exception.BusinessException;
import com.bodegaweb.bodegaweb.common.exception.DuplicateResourceException;
import com.bodegaweb.bodegaweb.common.exception.ResourceNotFoundException;
import com.pago.dto.PagoRequest;
import com.pago.dto.PagoResponse;
import com.pago.entity.Pago;
import com.pago.enums.EstadoPago;
import com.pago.repository.PagoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository repository;

    public PagoServiceImpl(PagoRepository repository) {
        this.repository = repository;
    }

    @Override
    public PagoResponse crear(PagoRequest request) {
        if (repository.existsByPedidoId(request.pedidoId())) {
            throw new DuplicateResourceException("Pago", "pedidoId", request.pedidoId());
        }

        Pago pago = new Pago();
        pago.setPedidoId(request.pedidoId());
        pago.setMonto(request.monto());
        pago.setMetodoPago(request.metodoPago());
        pago.setReferenciaExterna(request.referenciaExterna());
        pago.setEstado(EstadoPago.PENDIENTE);

        return toResponse(repository.save(pago));
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponse obtenerPorPedido(Long pedidoId) {
        Pago pago = repository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "pedidoId=" + pedidoId));
        return toResponse(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listar() {
        return repository.findAll().stream().map(PagoServiceImpl::toResponse).toList();
    }

    @Override
    public PagoResponse actualizarEstado(Long id, EstadoPago nuevoEstado) {
        Pago pago = buscar(id);
        validarTransicion(pago.getEstado(), nuevoEstado);
        pago.setEstado(nuevoEstado);
        return toResponse(repository.save(pago));
    }

    private Pago buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
    }

    private void validarTransicion(EstadoPago actual, EstadoPago nuevo) {
        boolean permitida = switch (actual) {
            case PENDIENTE -> nuevo == EstadoPago.APROBADO || nuevo == EstadoPago.RECHAZADO;
            case APROBADO -> nuevo == EstadoPago.REEMBOLSADO;
            case RECHAZADO, REEMBOLSADO -> false;
        };
        if (!permitida) {
            throw new BusinessException(
                    "No se puede cambiar el pago de %s a %s".formatted(actual, nuevo));
        }
    }

    static PagoResponse toResponse(Pago pago) {
        return new PagoResponse(
                pago.getId(),
                pago.getPedidoId(),
                pago.getMonto(),
                pago.getMetodoPago(),
                pago.getEstado(),
                pago.getReferenciaExterna(),
                pago.getCreatedAt(),
                pago.getUpdatedAt()
        );
    }
}
