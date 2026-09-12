package com.resena.repository;

import com.resena.entity.Resena;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByProductoIdOrderByCreatedAtDesc(Long productoId);

    List<Resena> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    boolean existsByProductoIdAndUsuarioId(Long productoId, Long usuarioId);

    boolean existsByProductoIdAndUsuarioIdAndIdNot(Long productoId, Long usuarioId, Long id);
}
