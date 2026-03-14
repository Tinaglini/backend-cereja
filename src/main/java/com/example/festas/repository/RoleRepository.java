package com.example.festas.repository;

import com.example.festas.entity.Role;
import com.example.festas.entity.TipoPapel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNome(TipoPapel nome);
}
