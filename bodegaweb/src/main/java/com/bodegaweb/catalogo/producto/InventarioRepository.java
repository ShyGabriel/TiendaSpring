package com.bodegaweb.catalogo.producto;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoId(Long productoId);

    boolean existsByProductoId(Long productoId);
}
