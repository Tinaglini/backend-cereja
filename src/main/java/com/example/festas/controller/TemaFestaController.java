package com.example.festas.controller;

import com.example.festas.entity.TemaFesta;
import com.example.festas.entity.Usuario;
import com.example.festas.service.TemaFestaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/ativos")
    public ResponseEntity<List<TemaFesta>> buscarAtivos() {
        List<TemaFesta> temas = temaService.buscarAtivos();
        return ResponseEntity.ok(temas);
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<TemaFesta>> buscarPendentes() {
        List<TemaFesta> temas = temaService.buscarPendentes();
        return ResponseEntity.ok(temas);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<TemaFesta>> buscarPorNome(@RequestParam String nome) {
        List<TemaFesta> temas = temaService.buscarPorNome(nome);
        return ResponseEntity.ok(temas);
    }

    /**
     * Cria um tema.
     * - ADMIN: tema entra com ativo = true (catálogo oficial)
     * - USER: tema entra com ativo = false (sugestão aguardando aprovação)
     */
    @PostMapping
    public ResponseEntity<TemaFesta> salvar(@Valid @RequestBody TemaFesta tema, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        boolean isAdmin = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            tema.setAtivo(false);
        }

        TemaFesta temaSalvo = temaService.salvar(tema);
        return ResponseEntity.status(HttpStatus.CREATED).body(temaSalvo);
    }

    /**
     * Admin aprova uma sugestão de tema (ativa = true).
     */
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<TemaFesta> ativar(@PathVariable Long id) {
        try {
            TemaFesta tema = temaService.ativar(id);
            return ResponseEntity.ok(tema);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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
}
