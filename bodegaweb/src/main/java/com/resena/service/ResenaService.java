package com.resena.service;

import com.resena.dto.ResenaRequest;
import com.resena.dto.ResenaResponse;
import java.util.List;

public interface ResenaService {

    ResenaResponse crear(ResenaRequest request);

    ResenaResponse obtener(Long id);

    List<ResenaResponse> listar();

    List<ResenaResponse> listarPorProducto(Long productoId);

    List<ResenaResponse> listarPorUsuario(Long usuarioId);

    ResenaResponse actualizar(Long id, ResenaRequest request);

    void eliminar(Long id);
}
