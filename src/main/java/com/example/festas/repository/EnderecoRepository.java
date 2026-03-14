package com.example.festas.repository;

import com.example.festas.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Métodos automáticos (mínimo 2 por repository)
    List<Endereco> findByCidadeIgnoreCaseContaining(String cidade);
    List<Endereco> findByEstado(String estado);
}