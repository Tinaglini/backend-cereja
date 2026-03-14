package com.example.festas.repository;

import com.example.festas.entity.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoEventoRepository extends JpaRepository<TipoEvento, Long> {

    // Métodos automáticos (mínimo 2 por repository)
    List<TipoEvento> findByNomeIgnoreCaseContaining(String nome);
    List<TipoEvento> findByCapacidadeMinimaLessThanEqualAndCapacidadeMaximaGreaterThanEqual(Integer min, Integer max);
}