package com.example.festas.service;

import com.example.festas.entity.Endereco;
import com.example.festas.entity.SolicitacaoOrcamento;
import com.example.festas.exception.BadRequestException;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.EnderecoRepository;
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

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Transactional(readOnly = true)
    public List<SolicitacaoOrcamento> buscarTodos() {
        return solicitacaoRepository.findAllWithTemas();
    }

    @Transactional(readOnly = true)
    public Optional<SolicitacaoOrcamento> buscarPorId(Long id) {
        return solicitacaoRepository.findByIdWithTemas(id);
    }

    @Transactional
    public SolicitacaoOrcamento salvar(SolicitacaoOrcamento solicitacao) {
        if (solicitacao.getCliente() == null || solicitacao.getCliente().getId() == null) {
            throw new BadRequestException("Não é possível criar solicitação sem associar a um cliente");
        }

        solicitacao.setStatusOrcamento("PENDENTE");
        solicitacao.setId(null);
        solicitacao.setDataCriacao(LocalDateTime.now());

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public SolicitacaoOrcamento atualizar(Long id, SolicitacaoOrcamento solicitacao) {
        SolicitacaoOrcamento existente = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada com ID: " + id));

        existente.setDataEvento(solicitacao.getDataEvento());
        existente.setQuantidadeConvidados(solicitacao.getQuantidadeConvidados());
        existente.setPrecisaMesasCadeiras(solicitacao.getPrecisaMesasCadeiras());
        existente.setValorPretendido(solicitacao.getValorPretendido());
        existente.setStatusOrcamento(solicitacao.getStatusOrcamento());
        existente.setTemas(solicitacao.getTemas());
        existente.setTipoEvento(solicitacao.getTipoEvento());

        // endereco tem CascadeType.ALL — carregar do banco pelo ID para evitar
        // sobrescrever o registro existente com campos nulos (objeto raso do frontend)
        if (solicitacao.getEndereco() != null && solicitacao.getEndereco().getId() != null) {
            Endereco endereco = enderecoRepository.findById(solicitacao.getEndereco().getId())
                    .orElse(existente.getEndereco());
            existente.setEndereco(endereco);
        }

        return solicitacaoRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!solicitacaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Solicitação não encontrada com ID: " + id);
        }
        solicitacaoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoOrcamento> buscarPorStatus(String status) {
        return solicitacaoRepository.findByStatusOrcamento(status);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoOrcamento> buscarPorCliente(Long clienteId) {
        return solicitacaoRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId);
    }

    @Transactional(readOnly = true)
    public boolean pertenceAoUsuario(Long solicitacaoId, String emailUsuario) {
        return solicitacaoRepository.findById(solicitacaoId)
                .map(s -> s.getCliente() != null
                        && s.getCliente().getUsuario() != null
                        && s.getCliente().getUsuario().getLogin().equals(emailUsuario))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoOrcamento> buscarPorStatusEUsuario(String status, String emailUsuario) {
        return solicitacaoRepository.findByStatusOrcamentoAndClienteUsuarioLogin(status, emailUsuario);
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoOrcamento> buscarPorUsuario(String emailUsuario) {
        return solicitacaoRepository.findByClienteUsuarioLogin(emailUsuario);
    }
}
