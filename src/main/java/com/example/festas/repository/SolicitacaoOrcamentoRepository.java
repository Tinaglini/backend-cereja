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

    List<SolicitacaoOrcamento> findByStatusOrcamento(String statusOrcamento);

    List<SolicitacaoOrcamento> findByDataEventoAfter(LocalDate data);

    @Query("SELECT DISTINCT s FROM SolicitacaoOrcamento s LEFT JOIN FETCH s.temas ORDER BY s.dataCriacao DESC")
    List<SolicitacaoOrcamento> findAllWithTemas();

    @Query("SELECT DISTINCT s FROM SolicitacaoOrcamento s LEFT JOIN FETCH s.temas WHERE s.cliente.id = :clienteId ORDER BY s.dataCriacao DESC")
    List<SolicitacaoOrcamento> findByClienteIdOrderByDataCriacaoDesc(@Param("clienteId") Long clienteId);

    @Query("SELECT DISTINCT s FROM SolicitacaoOrcamento s LEFT JOIN FETCH s.temas JOIN s.cliente c JOIN c.usuario u WHERE u.login = :login ORDER BY s.dataCriacao DESC")
    List<SolicitacaoOrcamento> findByClienteUsuarioLogin(@Param("login") String login);
}
