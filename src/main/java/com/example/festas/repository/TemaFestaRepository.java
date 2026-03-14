package com.example.festas.repository;

import com.example.festas.entity.TemaFesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemaFestaRepository extends JpaRepository<TemaFesta, Long> {

    // Métodos automáticos (mínimo 2 por repository)
    List<TemaFesta> findByNomeIgnoreCaseContaining(String nome);
    List<TemaFesta> findByAtivo(Boolean ativo);
}