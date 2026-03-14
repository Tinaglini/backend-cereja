package com.example.festas.controller;

import com.example.festas.entity.Endereco;
import com.example.festas.service.EnderecoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @GetMapping
    public ResponseEntity<List<Endereco>> buscarTodos() {
        List<Endereco> enderecos = enderecoService.buscarTodos();
        return ResponseEntity.ok(enderecos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Endereco> buscarPorId(@PathVariable Long id) {
        Optional<Endereco> endereco = enderecoService.buscarPorId(id);
        return endereco.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Endereco> salvar(@Valid @RequestBody Endereco endereco) {
        Endereco enderecoSalvo = enderecoService.salvar(endereco);
        return ResponseEntity.status(HttpStatus.CREATED).body(enderecoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Endereco> atualizar(@PathVariable Long id, @Valid @RequestBody Endereco endereco) {
        try {
            Endereco enderecoAtualizado = enderecoService.atualizar(id, endereco);
            return ResponseEntity.ok(enderecoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            enderecoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cidade")
    public ResponseEntity<List<Endereco>> buscarPorCidade(@RequestParam String cidade) {
        List<Endereco> enderecos = enderecoService.buscarPorCidade(cidade);
        return ResponseEntity.ok(enderecos);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Endereco>> buscarPorEstado(@PathVariable String estado) {
        List<Endereco> enderecos = enderecoService.buscarPorEstado(estado);
        return ResponseEntity.ok(enderecos);
    }
}