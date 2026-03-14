package com.example.festas.repository;

import com.example.festas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método que o Spring Security usará para buscar um usuário pelo login (email)
    UserDetails findByLogin(String login);
}