package com.losa.api_reservas.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.client.RestClient.ResponseSpec;

import com.losa.api_reservas.model.Court;
import com.losa.api_reservas.service.CourtService;

@RestController
@RequestMapping("/api/courts")
public class CourtController {

    @Autowired
    private CourtService courtService;

    @PostMapping
    public ResponseEntity<?> criarQuadra(@RequestBody Court quadra) {
        try {
            Court novaQuadra = courtService.criarQuadra(quadra);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaQuadra);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Court>> listarTodas() {
        return ResponseEntity.ok(courtService.listarTodasAtivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Court> buscarPorId(@PathVariable UUID id) {
        return courtService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarQuadra(@PathVariable UUID id, @RequestBody Court court) {
        try {
            Court quadraAtualizada = courtService.atualizarQuadra(id, court);
            return ResponseEntity.ok(quadraAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarQuadra(@PathVariable UUID id) {
        try {
            courtService.deletarQuadra(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
