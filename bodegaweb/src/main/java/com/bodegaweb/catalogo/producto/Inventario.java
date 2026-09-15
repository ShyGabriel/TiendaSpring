package com.bodegaweb.catalogo.producto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

/**
 * Inventario 1:1 con {@link Producto}. Tabla: {@code inventario}.
 * Sub-recurso: se gestiona bajo {@code /api/productos/{id}/inventario}.
 */
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false, unique = true)
    private Producto producto;

    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible = 0;

    @Column(name = "stock_reservado", nullable = false)
    private Integer stockReservado = 0;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    /**
     * Bloqueo optimista: evita que dos ajustes de stock concurrentes
     * (p. ej. dos compras al mismo tiempo) se pisen entre sí (lost update).
     * Ante conflicto, Hibernate lanza {@code ObjectOptimisticLockingFailureException},
     * mapeada a HTTP 409 en el manejador global.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @PrePersist
    @PreUpdate
    void touch() {
        actualizadoEn = LocalDateTime.now();
        if (stockDisponible == null) {
            stockDisponible = 0;
        }
        if (stockReservado == null) {
            stockReservado = 0;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public Integer getStockReservado() {
        return stockReservado;
    }

    public void setStockReservado(Integer stockReservado) {
        this.stockReservado = stockReservado;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
