package com.bodegaweb.direccion.service;

import java.util.List;

import com.bodegaweb.direccion.dto.DireccionRequest;
import com.bodegaweb.direccion.dto.DireccionResponse;

public interface DireccionService {

    List<DireccionResponse> findAll();

    DireccionResponse findById(Long id);

    List<DireccionResponse> findByUsuarioId(Long usuarioId);

    DireccionResponse create(DireccionRequest request);

    DireccionResponse update(Long id, DireccionRequest request);

    void delete(Long id);
}