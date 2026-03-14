package com.example.festas.security;

import com.example.festas.entity.Usuario;

public interface ITokenService {
    String gerarToken(Usuario usuario);
    String getSubject(String tokenJWT);
}


