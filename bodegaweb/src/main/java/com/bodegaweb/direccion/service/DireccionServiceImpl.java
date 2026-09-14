package com.bodegaweb.direccion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodegaweb.direccion.dto.DireccionRequest;
import com.bodegaweb.direccion.dto.DireccionResponse;
import com.bodegaweb.direccion.entity.Direccion;
import com.bodegaweb.direccion.repository.DireccionRepository;
import com.bodegaweb.exceptions.ResourceNotFoundException;
import com.bodegaweb.usuario.entity.Usuario;
import com.bodegaweb.usuario.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class DireccionServiceImpl implements DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    public DireccionServiceImpl(DireccionRepository direccionRepository, UsuarioRepository usuarioRepository) {
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<DireccionResponse> findAll() {
        return direccionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public DireccionResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    public List<DireccionResponse> findByUsuarioId(Long usuarioId) {
        return direccionRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DireccionResponse create(DireccionRequest request) {
        Direccion direccion = new Direccion();
        applyRequest(direccion, request);
        return toResponse(direccionRepository.save(direccion));
    }

    @Override
    @Transactional
    public DireccionResponse update(Long id, DireccionRequest request) {
        Direccion direccion = findEntityById(id);
        applyRequest(direccion, request);
        return toResponse(direccionRepository.save(direccion));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Direccion direccion = findEntityById(id);
        direccionRepository.delete(direccion);
    }

    private Direccion findEntityById(Long id) {
        return direccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion no encontrada con id " + id));
    }

    private void applyRequest(Direccion direccion, DireccionRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + request.usuarioId()));
        direccion.setCalle(request.calle());
        direccion.setNumero(request.numero());
        direccion.setCiudad(request.ciudad());
        direccion.setCodigoPostal(request.codigoPostal());
        direccion.setPais(request.pais());
        direccion.setUsuario(usuario);
    }

    private DireccionResponse toResponse(Direccion direccion) {
        return new DireccionResponse(
                direccion.getId(),
                direccion.getCalle(),
                direccion.getNumero(),
                direccion.getCiudad(),
                direccion.getCodigoPostal(),
                direccion.getPais(),
                direccion.getUsuario().getId());
    }
}