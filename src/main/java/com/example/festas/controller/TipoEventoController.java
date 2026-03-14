package com.example.festas.controller;

import com.example.festas.entity.TipoEvento;
import com.example.festas.service.TipoEventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tipos-evento")
public class TipoEventoController {

    @Autowired
    private TipoEventoService tipoEventoService;

    @GetMapping
    public ResponseEntity<List<TipoEvento>> buscarTodos() {
        List<TipoEvento> tiposEvento = tipoEventoService.buscarTodos();
        return ResponseEntity.ok(tiposEvento);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoEvento> buscarPorId(@PathVariable Long id) {
        Optional<TipoEvento> tipoEvento = tipoEventoService.buscarPorId(id);
        return tipoEvento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoEvento> salvar(@Valid @RequestBody TipoEvento tipoEvento) {
        TipoEvento tipoEventoSalvo = tipoEventoService.salvar(tipoEvento);
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoEventoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoEvento> atualizar(@PathVariable Long id, @Valid @RequestBody TipoEvento tipoEvento) {
        try {
            TipoEvento tipoEventoAtualizado = tipoEventoService.atualizar(id, tipoEvento);
            return ResponseEntity.ok(tipoEventoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            tipoEventoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<TipoEvento>> buscarPorNome(@RequestParam String nome) {
        List<TipoEvento> tiposEvento = tipoEventoService.buscarPorNome(nome);
        return ResponseEntity.ok(tiposEvento);
    }

    @GetMapping("/capacidade/{capacidade}")
    public ResponseEntity<List<TipoEvento>> buscarPorCapacidade(@PathVariable Integer capacidade) {
        List<TipoEvento> tiposEvento = tipoEventoService.buscarPorCapacidade(capacidade);
        return ResponseEntity.ok(tiposEvento);
    }
}