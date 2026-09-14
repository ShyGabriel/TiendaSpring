package com.bodegaweb.bodegaweb.carrito.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId(){ 
        return id; 
    }
    
    public void setId(Long id){ 
        this.id = id; 
    }
    
    public Long getUsuarioId(){ 
        return usuarioId; 
    }
    
    public void setUsuarioId(Long usuarioId){ 
        this.usuarioId = usuarioId; 
    }
    
    public LocalDateTime getCreatedAt(){ 
        return createdAt; 
    }

    public List<CarritoItem> getItems(){ 
        return items; 
    }
    
    public void setItems(List<CarritoItem> items){ 
        this.items = items; 
    }
}