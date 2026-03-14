package com.example.festas.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DadosRegistro(
        @NotBlank(message = "Nome é obrigatório") String nome,

        @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ser válido") String email,

        @NotBlank(message = "Senha é obrigatória") String senha) {
}
