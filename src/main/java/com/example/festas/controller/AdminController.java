package com.example.festas.controller;

import com.example.festas.entity.Role;
import com.example.festas.entity.TipoPapel;
import com.example.festas.entity.Usuario;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.RoleRepository;
import com.example.festas.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/{id}/promover-admin")
    public ResponseEntity<?> promoverParaAdmin(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        boolean jaEAdmin = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (jaEAdmin) {
            return ResponseEntity.ok().body("Usuário já possui ROLE_ADMIN");
        }

        Role roleAdmin = roleRepository.findByNome(TipoPapel.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_ADMIN não encontrada"));

        usuario.getRoles().add(roleAdmin);
        usuarioRepository.save(usuario);
        log.info("Usuário {} promovido a ADMIN", usuario.getLogin());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Usuário promovido a ADMIN com sucesso");
        response.put("email", usuario.getLogin());
        response.put("roles", usuario.getAuthorities().stream().map(a -> a.getAuthority()).toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/remover-admin")
    public ResponseEntity<?> removerAdmin(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        Role roleAdmin = roleRepository.findByNome(TipoPapel.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_ADMIN não encontrada"));

        boolean removido = usuario.getRoles().remove(roleAdmin);

        if (!removido) {
            return ResponseEntity.ok().body("Usuário não possuía ROLE_ADMIN");
        }

        Role roleUser = roleRepository.findByNome(TipoPapel.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role ROLE_USER não encontrada"));

        if (!usuario.getRoles().contains(roleUser)) {
            usuario.getRoles().add(roleUser);
        }

        usuarioRepository.save(usuario);
        log.info("ROLE_ADMIN removida de {}", usuario.getLogin());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "ROLE_ADMIN removida com sucesso");
        response.put("email", usuario.getLogin());
        response.put("roles", usuario.getAuthorities().stream().map(a -> a.getAuthority()).toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        var usuarios = usuarioRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", u.getId());
                    userInfo.put("email", u.getLogin());
                    userInfo.put("roles", u.getAuthorities().stream().map(a -> a.getAuthority()).toList());
                    return userInfo;
                })
                .toList();

        return ResponseEntity.ok(usuarios);
    }
}
