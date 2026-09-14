package com.bodegaweb.direccion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bodegaweb.direccion.dto.DireccionRequest;
import com.bodegaweb.direccion.dto.DireccionResponse;
import com.bodegaweb.direccion.service.DireccionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @GetMapping
    public List<DireccionResponse> findAll() {
        return direccionService.findAll();
    }

    @GetMapping("/{id}")
    public DireccionResponse findById(@PathVariable Long id) {
        return direccionService.findById(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<DireccionResponse> findByUsuarioId(@PathVariable Long usuarioId) {
        return direccionService.findByUsuarioId(usuarioId);
    }

    @PostMapping
    public ResponseEntity<DireccionResponse> create(@Valid @RequestBody DireccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(direccionService.create(request));
    }

    @PutMapping("/{id}")
    public DireccionResponse update(@PathVariable Long id, @Valid @RequestBody DireccionRequest request) {
        return direccionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        direccionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}