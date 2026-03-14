package com.example.festas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/user/test")
    public ResponseEntity<?> userTest() {
        return ResponseEntity.ok(Map.of(
                "message", "✅ Endpoint /api/user/test acessado com sucesso!",
                "requiredRole", "ROLE_USER ou ROLE_ADMIN",
                "description", "Este endpoint pode ser acessado por usuários com ROLE_USER ou ROLE_ADMIN"));
    }

    @GetMapping("/admin/test")
    public ResponseEntity<?> adminTest() {
        return ResponseEntity.ok(Map.of(
                "message", "✅ Endpoint /api/admin/test acessado com sucesso!",
                "requiredRole", "ROLE_ADMIN",
                "description", "Este endpoint só pode ser acessado por usuários com ROLE_ADMIN"));
    }

    @GetMapping("/public/test")
    public ResponseEntity<?> publicTest() {
        return ResponseEntity.ok(Map.of(
                "message", "✅ Endpoint /api/public/test acessado com sucesso!",
                "requiredRole", "Nenhuma (público)",
                "description", "Este endpoint pode ser acessado sem autenticação"));
    }
}
