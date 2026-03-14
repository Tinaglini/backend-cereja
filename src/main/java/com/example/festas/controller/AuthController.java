package com.example.festas.controller;

import com.example.festas.entity.Usuario;
import com.example.festas.entity.TipoPapel;
import com.example.festas.exception.BadRequestException;
import com.example.festas.repository.RoleRepository;
import com.example.festas.repository.UsuarioRepository;
import com.example.festas.security.DadosAutenticacao;
import com.example.festas.security.DadosRegistro;
import com.example.festas.security.DadosTokenJWT;
import com.example.festas.security.ITokenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/login")
    public ResponseEntity<?> efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        try {
            var authentication = manager.authenticate(authenticationToken);
            var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
            log.info("Login bem-sucedido para: {}", dados.login());
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
        } catch (AuthenticationException e) {
            log.warn("Falha de autenticação para: {}", dados.login());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Falha na autenticação: Login ou senha incorretos.");
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody @Valid DadosRegistro dados) {
        if (usuarioRepository.findByLogin(dados.email()) != null) {
            throw new BadRequestException("Usuário já existente!");
        }

        String senhaCriptografada = passwordEncoder.encode(dados.senha());
        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(dados.email());
        novoUsuario.setSenha(senhaCriptografada);

        var roleUser = roleRepository.findByNome(TipoPapel.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role ROLE_USER não encontrada no banco de dados"));
        novoUsuario.getRoles().add(roleUser);

        usuarioRepository.save(novoUsuario);
        log.info("Novo usuário registrado: {}", dados.email());
        return ResponseEntity.ok().build();
    }
}
