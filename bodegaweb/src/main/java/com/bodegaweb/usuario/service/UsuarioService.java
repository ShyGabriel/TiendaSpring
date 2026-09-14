package com.bodegaweb.usuario.service;

import java.util.List;

import com.bodegaweb.usuario.dto.UsuarioLoginRequest;
import com.bodegaweb.usuario.dto.UsuarioRequest;
import com.bodegaweb.usuario.dto.UsuarioResponse;

public interface UsuarioService {

    List<UsuarioResponse> findAll();

    UsuarioResponse findById(Long id);

    UsuarioResponse create(UsuarioRequest request);

    UsuarioResponse update(Long id, UsuarioRequest request);

    void delete(Long id);

    UsuarioResponse login(UsuarioLoginRequest request);
}