package com.example.festas.config;

import com.example.festas.entity.Role;
import com.example.festas.entity.TipoPapel;
import com.example.festas.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        System.out.println("=== INICIALIZANDO ROLES PADRÃO ===");

        // Criar ROLE_USER se não existir
        if (roleRepository.findByNome(TipoPapel.ROLE_USER).isEmpty()) {
            Role roleUser = new Role();
            roleUser.setNome(TipoPapel.ROLE_USER);
            roleRepository.save(roleUser);
            System.out.println("✅ ROLE_USER criada");
        } else {
            System.out.println("✅ ROLE_USER já existe");
        }

        // Criar ROLE_ADMIN se não existir
        if (roleRepository.findByNome(TipoPapel.ROLE_ADMIN).isEmpty()) {
            Role roleAdmin = new Role();
            roleAdmin.setNome(TipoPapel.ROLE_ADMIN);
            roleRepository.save(roleAdmin);
            System.out.println("✅ ROLE_ADMIN criada");
        } else {
            System.out.println("✅ ROLE_ADMIN já existe");
        }

        System.out.println("=== INICIALIZAÇÃO DE ROLES CONCLUÍDA ===");
    }
}
