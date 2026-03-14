package com.example.festas.controller;

import com.example.festas.entity.SolicitacaoOrcamento;
import com.example.festas.entity.Usuario;
import com.example.festas.service.SolicitacaoOrcamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoOrcamentoController {

    @Autowired
    private SolicitacaoOrcamentoService solicitacaoService;

    @GetMapping
    public ResponseEntity<List<SolicitacaoOrcamento>> buscarTodos(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        boolean isAdmin = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<SolicitacaoOrcamento> solicitacoes;
        if (isAdmin) {
            // Admin vê todas as solicitações
            solicitacoes = solicitacaoService.buscarTodos();
        } else {
            // Usuário comum vê apenas suas solicitações
            solicitacoes = solicitacaoService.buscarPorUsuario(usuario.getUsername());
        }

        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        boolean isAdmin = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Optional<SolicitacaoOrcamento> solicitacao = solicitacaoService.buscarPorId(id);
        if (solicitacao.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Verificar propriedade se não for admin
        if (!isAdmin && !solicitacaoService.pertenceAoUsuario(id, usuario.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Você só pode visualizar suas próprias solicitações");
        }

        return ResponseEntity.ok(solicitacao.get());
    }

    @PostMapping
    public ResponseEntity<SolicitacaoOrcamento> salvar(@Valid @RequestBody SolicitacaoOrcamento solicitacao) {
        try {
            SolicitacaoOrcamento solicitacaoSalva = solicitacaoService.salvar(solicitacao);
            return ResponseEntity.status(HttpStatus.CREATED).body(solicitacaoSalva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody SolicitacaoOrcamento solicitacao,
            Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        boolean isAdmin = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Verificar propriedade se não for admin
        if (!isAdmin && !solicitacaoService.pertenceAoUsuario(id, usuario.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Você só pode atualizar suas próprias solicitações");
        }

        try {
            SolicitacaoOrcamento solicitacaoAtualizada = solicitacaoService.atualizar(id, solicitacao);
            return ResponseEntity.ok(solicitacaoAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            solicitacaoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status")
    public ResponseEntity<List<SolicitacaoOrcamento>> buscarPorStatus(@RequestParam String status) {
        List<SolicitacaoOrcamento> solicitacoes = solicitacaoService.buscarPorStatus(status);
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> buscarPorCliente(@PathVariable Long clienteId, Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        boolean isAdmin = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<SolicitacaoOrcamento> solicitacoes = solicitacaoService.buscarPorCliente(clienteId);

        // Se não for admin, verificar se está buscando suas próprias solicitações
        if (!isAdmin) {
            // Verificar se pelo menos uma solicitação pertence ao usuário
            boolean pertenceAoUsuario = solicitacoes.stream()
                    .anyMatch(s -> solicitacaoService.pertenceAoUsuario(s.getId(), usuario.getUsername()));

            if (!pertenceAoUsuario && !solicitacoes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Você só pode visualizar suas próprias solicitações");
            }
        }

        return ResponseEntity.ok(solicitacoes);
    }
}