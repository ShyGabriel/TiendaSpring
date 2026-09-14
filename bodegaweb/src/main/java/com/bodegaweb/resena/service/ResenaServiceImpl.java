package com.bodegaweb.resena.service;

import com.bodegaweb.common.exception.DuplicateResourceException;
import com.bodegaweb.common.exception.ResourceNotFoundException;
import com.bodegaweb.resena.dto.ResenaRequest;
import com.bodegaweb.resena.dto.ResenaResponse;
import com.bodegaweb.resena.entity.Resena;
import com.bodegaweb.resena.repository.ResenaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository repository;

    public ResenaServiceImpl(ResenaRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResenaResponse crear(ResenaRequest request) {
        if (repository.existsByProductoIdAndUsuarioId(request.productoId(), request.usuarioId())) {
            throw new DuplicateResourceException(
                    "Reseña", "productoId+usuarioId", request.productoId() + "/" + request.usuarioId());
        }

        Resena resena = new Resena();
        aplicar(resena, request);
        return toResponse(repository.save(resena));
    }

    @Override
    @Transactional(readOnly = true)
    public ResenaResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaResponse> listar() {
        return repository.findAll().stream().map(ResenaServiceImpl::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaResponse> listarPorProducto(Long productoId) {
        return repository.findByProductoIdOrderByCreatedAtDesc(productoId).stream()
                .map(ResenaServiceImpl::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId).stream()
                .map(ResenaServiceImpl::toResponse)
                .toList();
    }

    @Override
    public ResenaResponse actualizar(Long id, ResenaRequest request) {
        Resena resena = buscar(id);
        if (repository.existsByProductoIdAndUsuarioIdAndIdNot(
                request.productoId(), request.usuarioId(), id)) {
            throw new DuplicateResourceException(
                    "Reseña", "productoId+usuarioId", request.productoId() + "/" + request.usuarioId());
        }
        aplicar(resena, request);
        return toResponse(repository.save(resena));
    }

    @Override
    public void eliminar(Long id) {
        Resena resena = buscar(id);
        repository.delete(resena);
    }

    private Resena buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña", id));
    }

    private void aplicar(Resena resena, ResenaRequest request) {
        resena.setProductoId(request.productoId());
        resena.setUsuarioId(request.usuarioId());
        resena.setCalificacion(request.calificacion());
        resena.setComentario(request.comentario());
    }

    static ResenaResponse toResponse(Resena resena) {
        return new ResenaResponse(
                resena.getId(),
                resena.getProductoId(),
                resena.getUsuarioId(),
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getCreatedAt(),
                resena.getUpdatedAt()
        );
    }
}
