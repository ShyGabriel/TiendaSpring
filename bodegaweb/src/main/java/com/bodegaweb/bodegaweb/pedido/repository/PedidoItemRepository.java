package com.bodegaweb.bodegaweb.pedido.repository;

import com.bodegaweb.bodegaweb.pedido.entity.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
}