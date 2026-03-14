package com.example.festas.repository;

import com.example.festas.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Métodos automáticos (mínimo 2 por repository)
    List<Cliente> findByNomeIgnoreCaseContaining(String nome);
    List<Cliente> findByStatusCadastro(String statusCadastro);

    // Consulta JPQL personalizada (pelo menos 1 na aplicação toda)
    @Query("SELECT c FROM Cliente c WHERE c.telefone = :telefone")
    Cliente findByTelefone(@Param("telefone") String telefone);
}