package com.example.festas.service;

import com.example.festas.entity.SolicitacaoOrcamento;
import com.example.festas.exception.BadRequestException;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.SolicitacaoOrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitacaoOrcamentoService {

    @Autowired
    private SolicitacaoOrcamentoRepository solicitacaoRepository;

    public List<SolicitacaoOrcamento> buscarTodos() {
        return solicitacaoRepository.findAll();
    }

    public Optional<SolicitacaoOrcamento> buscarPorId(Long id) {
        return solicitacaoRepository.findById(id);
    }

    @Transactional
    public SolicitacaoOrcamento salvar(SolicitacaoOrcamento solicitacao) {
        if (solicitacao.getCliente() == null || solicitacao.getCliente().getId() == null) {
            throw new BadRequestException("Não é possível criar solicitação sem associar a um cliente");
        }

        if (solicitacao.getStatusOrcamento() == null) {
            solicitacao.setStatusOrcamento("PENDENTE");
        }
        solicitacao.setDataCriacao(LocalDateTime.now());

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public SolicitacaoOrcamento atualizar(Long id, SolicitacaoOrcamento solicitacao) {
        SolicitacaoOrcamento existente = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada com ID: " + id));
        solicitacao.setId(id);
        solicitacao.setDataCriacao(existente.getDataCriacao());
        return salvar(solicitacao);
    }

    @Transactional
    public void deletar(Long id) {
        if (!solicitacaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Solicitação não encontrada com ID: " + id);
        }
        solicitacaoRepository.deleteById(id);
    }

    public List<SolicitacaoOrcamento> buscarPorStatus(String status) {
        return solicitacaoRepository.findByStatusOrcamento(status);
    }

    public List<SolicitacaoOrcamento> buscarPorCliente(Long clienteId) {
        return solicitacaoRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId);
    }

    public boolean pertenceAoUsuario(Long solicitacaoId, String emailUsuario) {
        return solicitacaoRepository.findById(solicitacaoId)
                .map(s -> s.getCliente() != null
                        && s.getCliente().getUsuario() != null
                        && s.getCliente().getUsuario().getLogin().equals(emailUsuario))
                .orElse(false);
    }

    public List<SolicitacaoOrcamento> buscarPorUsuario(String emailUsuario) {
        return solicitacaoRepository.findByClienteUsuarioLogin(emailUsuario);
    }
}
