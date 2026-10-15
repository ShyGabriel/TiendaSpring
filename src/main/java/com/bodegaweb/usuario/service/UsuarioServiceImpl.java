package com.bodegaweb.usuario.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodegaweb.common.exception.DuplicateResourceException;
import com.bodegaweb.common.exception.ResourceNotFoundException;
import com.bodegaweb.usuario.dto.UsuarioRequest;
import com.bodegaweb.usuario.dto.UsuarioResponse;
import com.bodegaweb.usuario.entity.Usuario;
import com.bodegaweb.usuario.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UsuarioResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Ya existe un usuario con el email " + request.email());
        }
        Usuario usuario = new Usuario();
        applyRequest(usuario, request);
        // El registro publico no puede auto-asignarse un rol; siempre nace como USUARIO.
        // El rol se ajusta luego por un ADMIN via update.
        usuario.setRol(Usuario.Rol.USUARIO);
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario usuario = findEntityById(id);
        if (!usuario.getEmail().equalsIgnoreCase(request.email())
                && usuarioRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Ya existe un usuario con el email " + request.email());
        }
        applyRequest(usuario, request);
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Usuario usuario = findEntityById(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario findEntityById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    private void applyRequest(Usuario usuario, UsuarioRequest request) {
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setTelefono(request.telefono());
        if (request.rol() != null) {
            usuario.setRol(request.rol());
        }
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.getCreatedAt());
    }
}