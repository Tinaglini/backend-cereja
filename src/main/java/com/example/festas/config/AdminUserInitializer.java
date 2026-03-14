package com.example.festas.config;

import com.example.festas.entity.Role;
import com.example.festas.entity.TipoPapel;
import com.example.festas.entity.Usuario;
import com.example.festas.repository.RoleRepository;
import com.example.festas.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.default.email}")
    private String adminEmail;

    @Value("${admin.default.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            if (usuarioRepository.findByLogin(adminEmail) == null) {
                log.info("Criando usuário admin padrão: {}", adminEmail);

                Usuario admin = new Usuario();
                admin.setLogin(adminEmail);
                admin.setSenha(passwordEncoder.encode(adminPassword));

                Role roleAdmin = roleRepository.findByNome(TipoPapel.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Role ROLE_ADMIN não encontrada"));
                admin.getRoles().add(roleAdmin);

                usuarioRepository.save(admin);
                log.info("Usuário admin criado com sucesso.");
            } else {
                log.debug("Usuário admin já existe, ignorando criação.");
            }
        };
    }
}
