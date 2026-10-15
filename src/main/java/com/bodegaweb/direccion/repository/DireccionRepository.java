package com.bodegaweb.direccion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodegaweb.direccion.entity.Direccion;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {

    List<Direccion> findByUsuarioId(Long usuarioId);
}