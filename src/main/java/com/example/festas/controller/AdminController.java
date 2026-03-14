package com.example.festas.controller;

import com.example.festas.entity.Role;
import com.example.festas.entity.TipoPapel;
import com.example.festas.entity.Usuario;
import com.example.festas.repository.RoleRepository;
import com.example.festas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Promove um usuário para ROLE_ADMIN
     * Apenas administradores podem usar este endpoint
     */
    @PostMapping("/{id}/promover-admin")
    public ResponseEntity<?> promoverParaAdmin(@PathVariable Long id) {
        System.out.println("=== PROMOVENDO USUÁRIO A ADMIN ===");
        System.out.println("ID do usuário: " + id);

        // Buscar usuário
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            System.out.println("ERRO: Usuário não encontrado");
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        // Verificar se já é admin
        boolean jaEAdmin = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (jaEAdmin) {
            System.out.println("AVISO: Usuário já é admin");
            return ResponseEntity.ok().body("Usuário já possui ROLE_ADMIN");
        }

        // Buscar role ROLE_ADMIN
        Role roleAdmin = roleRepository.findByNome(TipoPapel.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role ROLE_ADMIN não encontrada"));

        // Adicionar role ao usuário
        usuario.getRoles().add(roleAdmin);
        usuarioRepository.save(usuario);

        System.out.println("SUCCESS: Usuário " + usuario.getLogin() + " promovido a ADMIN!");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Usuário promovido a ADMIN com sucesso");
        response.put("email", usuario.getLogin());
        response.put("roles", usuario.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Remove ROLE_ADMIN de um usuário (rebaixa para USER apenas)
     * Apenas administradores podem usar este endpoint
     */
    @DeleteMapping("/{id}/remover-admin")
    public ResponseEntity<?> removerAdmin(@PathVariable Long id) {
        System.out.println("=== REMOVENDO ROLE_ADMIN DE USUÁRIO ===");
        System.out.println("ID do usuário: " + id);

        // Buscar usuário
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            System.out.println("ERRO: Usuário não encontrado");
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        // Buscar role ROLE_ADMIN
        Role roleAdmin = roleRepository.findByNome(TipoPapel.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role ROLE_ADMIN não encontrada"));

        // Remover role do usuário
        boolean removido = usuario.getRoles().remove(roleAdmin);

        if (!removido) {
            System.out.println("AVISO: Usuário não tinha ROLE_ADMIN");
            return ResponseEntity.ok().body("Usuário não possuía ROLE_ADMIN");
        }

        // Garantir que o usuário tenha pelo menos ROLE_USER
        Role roleUser = roleRepository.findByNome(TipoPapel.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role ROLE_USER não encontrada"));

        if (!usuario.getRoles().contains(roleUser)) {
            usuario.getRoles().add(roleUser);
        }

        usuarioRepository.save(usuario);

        System.out.println("SUCCESS: ROLE_ADMIN removida de " + usuario.getLogin());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "ROLE_ADMIN removida com sucesso");
        response.put("email", usuario.getLogin());
        response.put("roles", usuario.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os usuários e suas roles
     * Apenas administradores podem usar este endpoint
     */
    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        var usuarios = usuarioRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", u.getId());
                    userInfo.put("email", u.getLogin());
                    userInfo.put("roles", u.getAuthorities().stream()
                            .map(a -> a.getAuthority()).toList());
                    return userInfo;
                })
                .toList();

        return ResponseEntity.ok(usuarios);
    }
}
