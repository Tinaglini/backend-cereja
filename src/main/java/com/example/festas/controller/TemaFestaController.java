package com.example.festas.controller;

import com.example.festas.entity.TemaFesta;
import com.example.festas.service.TemaFestaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/temas")
public class TemaFestaController {

    @Autowired
    private TemaFestaService temaService;

    @GetMapping
    public ResponseEntity<List<TemaFesta>> buscarTodos() {
        List<TemaFesta> temas = temaService.buscarTodos();
        return ResponseEntity.ok(temas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemaFesta> buscarPorId(@PathVariable Long id) {
        Optional<TemaFesta> tema = temaService.buscarPorId(id);
        return tema.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TemaFesta> salvar(@Valid @RequestBody TemaFesta tema) {
        TemaFesta temaSalvo = temaService.salvar(tema);
        return ResponseEntity.status(HttpStatus.CREATED).body(temaSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemaFesta> atualizar(@PathVariable Long id, @Valid @RequestBody TemaFesta tema) {
        try {
            TemaFesta temaAtualizado = temaService.atualizar(id, tema);
            return ResponseEntity.ok(temaAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            temaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<TemaFesta>> buscarPorNome(@RequestParam String nome) {
        List<TemaFesta> temas = temaService.buscarPorNome(nome);
        return ResponseEntity.ok(temas);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<TemaFesta>> buscarAtivos() {
        List<TemaFesta> temas = temaService.buscarAtivos();
        return ResponseEntity.ok(temas);
    }
}