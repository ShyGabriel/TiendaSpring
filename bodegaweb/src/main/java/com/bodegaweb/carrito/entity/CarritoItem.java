package com.bodegaweb.carrito.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "carrito_item")
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @Column(name = "producto_id", nullable = false)
    private Long productoId; 

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    public Long getId(){ 
        return id; 
    }

    public void setId(Long id){ 
        this.id = id; 
    }
    
    public Carrito getCarrito(){ 
        return carrito; 
    }
    
    public void setCarrito(Carrito carrito){ 
        this.carrito = carrito; 
    }
    
    public Long getProductoId(){ 
        return productoId; 
    }
    
    public void setProductoId(Long productoId){ 
        this.productoId = productoId; 
    }
    
    public Integer getCantidad(){ 
        return cantidad; 
    }
    
    public void setCantidad(Integer cantidad){ 
        this.cantidad = cantidad; 
    }
    
    public BigDecimal getPrecioUnitario(){ 
        return precioUnitario; 
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario){ 
        this.precioUnitario = precioUnitario; 
    }
}