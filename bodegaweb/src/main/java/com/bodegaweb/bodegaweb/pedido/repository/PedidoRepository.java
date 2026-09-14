package com.bodegaweb.bodegaweb.pedido.repository;

import com.bodegaweb.bodegaweb.pedido.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
}