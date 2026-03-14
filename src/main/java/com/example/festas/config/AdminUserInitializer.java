package com.example.festas.config;

import com.example.festas.entity.Role;
import com.example.festas.entity.TipoPapel;
import com.example.festas.entity.Usuario;
import com.example.festas.repository.RoleRepository;
import com.example.festas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserInitializer {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            // Verificar se já existe um usuário admin
            if (usuarioRepository.findByLogin("admin@festas.com") == null) {
                System.out.println("=== CRIANDO USUÁRIO ADMIN PADRÃO ===");

                Usuario admin = new Usuario();
                admin.setLogin("admin@festas.com");
                admin.setSenha(passwordEncoder.encode("admin123"));

                // Adicionar role ADMIN
                Role roleAdmin = roleRepository.findByNome(TipoPapel.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Role ROLE_ADMIN não encontrada"));
                admin.getRoles().add(roleAdmin);

                usuarioRepository.save(admin);

                System.out.println("✅ Usuário admin criado:");
                System.out.println("   Email: admin@festas.com");
                System.out.println("   Senha: admin123");
                System.out.println("   Role: ROLE_ADMIN");
            } else {
                System.out.println("✅ Usuário admin já existe");
            }
        };
    }
}
