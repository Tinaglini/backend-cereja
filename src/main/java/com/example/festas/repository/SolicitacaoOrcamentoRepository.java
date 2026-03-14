package com.example.festas.repository;

import com.example.festas.entity.SolicitacaoOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitacaoOrcamentoRepository extends JpaRepository<SolicitacaoOrcamento, Long> {

    // Métodos automáticos (mínimo 2 por repository)
    List<SolicitacaoOrcamento> findByStatusOrcamento(String statusOrcamento);
    List<SolicitacaoOrcamento> findByDataEventoAfter(LocalDate data);

    // Consulta JPQL personalizada adicional
    @Query("SELECT s FROM SolicitacaoOrcamento s WHERE s.cliente.id = :clienteId ORDER BY s.dataCriacao DESC")
    List<SolicitacaoOrcamento> findByClienteIdOrderByDataCriacaoDesc(@Param("clienteId") Long clienteId);
}