package com.example.festas.service;

import com.example.festas.entity.SolicitacaoOrcamento;
import com.example.festas.repository.SolicitacaoOrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public SolicitacaoOrcamento salvar(SolicitacaoOrcamento solicitacao) {
        // REGRA DE NEGÓCIO COMPLEXA COM EXCEPTION: Validar cliente
        if (solicitacao.getCliente() == null || solicitacao.getCliente().getId() == null) {
            throw new RuntimeException("Não é possível criar solicitação sem associar a um cliente");
        }

        // REGRA DE NEGÓCIO COMPLEXA: Definir status e data de criação antes de
        // persistir
        if (solicitacao.getStatusOrcamento() == null) {
            solicitacao.setStatusOrcamento("PENDENTE");
        }
        solicitacao.setDataCriacao(LocalDateTime.now());

        return solicitacaoRepository.save(solicitacao);
    }

    public SolicitacaoOrcamento atualizar(Long id, SolicitacaoOrcamento solicitacao) {
        Optional<SolicitacaoOrcamento> solicitacaoExistente = solicitacaoRepository.findById(id);
        if (solicitacaoExistente.isPresent()) {
            solicitacao.setId(id);
            solicitacao.setDataCriacao(solicitacaoExistente.get().getDataCriacao());
            return salvar(solicitacao);
        }
        throw new RuntimeException("Solicitação não encontrada com ID: " + id);
    }

    public void deletar(Long id) {
        if (!solicitacaoRepository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar solicitação inexistente");
        }
        solicitacaoRepository.deleteById(id);
    }

    public List<SolicitacaoOrcamento> buscarPorStatus(String status) {
        return solicitacaoRepository.findByStatusOrcamento(status);
    }

    public List<SolicitacaoOrcamento> buscarPorCliente(Long clienteId) {
        return solicitacaoRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId);
    }

    /**
     * Verifica se uma solicitação pertence ao usuário logado
     */
    public boolean pertenceAoUsuario(Long solicitacaoId, String emailUsuario) {
        Optional<SolicitacaoOrcamento> solicitacao = solicitacaoRepository.findById(solicitacaoId);
        if (solicitacao.isEmpty()) {
            return false;
        }

        // Verificar se o cliente da solicitação está vinculado ao usuário
        if (solicitacao.get().getCliente() != null &&
                solicitacao.get().getCliente().getUsuario() != null) {
            return solicitacao.get().getCliente().getUsuario().getLogin().equals(emailUsuario);
        }

        return false;
    }

    /**
     * Busca solicitações do usuário logado (baseado no cliente vinculado)
     */
    public List<SolicitacaoOrcamento> buscarPorUsuario(String emailUsuario) {
        // Buscar todas as solicitações onde o cliente está vinculado ao usuário
        return solicitacaoRepository.findAll().stream()
                .filter(s -> s.getCliente() != null &&
                        s.getCliente().getUsuario() != null &&
                        s.getCliente().getUsuario().getLogin().equals(emailUsuario))
                .toList();
    }
}