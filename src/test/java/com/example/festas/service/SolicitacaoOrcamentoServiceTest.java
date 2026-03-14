package com.example.festas.service;

import com.example.festas.entity.Cliente;
import com.example.festas.entity.SolicitacaoOrcamento;
import com.example.festas.entity.Usuario;
import com.example.festas.exception.BadRequestException;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.SolicitacaoOrcamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoOrcamentoServiceTest {

    @Mock
    private SolicitacaoOrcamentoRepository solicitacaoRepository;

    @InjectMocks
    private SolicitacaoOrcamentoService solicitacaoService;

    private SolicitacaoOrcamento solicitacao;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("user@test.com");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria Silva");
        cliente.setUsuario(usuario);

        solicitacao = new SolicitacaoOrcamento();
        solicitacao.setId(1L);
        solicitacao.setCliente(cliente);
        solicitacao.setDataEvento(LocalDate.now().plusMonths(1));
        solicitacao.setQuantidadeConvidados(50);
    }

    @Test
    void buscarTodos_deveRetornarLista() {
        when(solicitacaoRepository.findAll()).thenReturn(List.of(solicitacao));

        List<SolicitacaoOrcamento> resultado = solicitacaoService.buscarTodos();

        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorId_deveRetornarSolicitacao() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));

        Optional<SolicitacaoOrcamento> resultado = solicitacaoService.buscarPorId(1L);

        assertThat(resultado).isPresent();
    }

    @Test
    void salvar_comClienteValido_devePersistirComStatusPendente() {
        when(solicitacaoRepository.save(any(SolicitacaoOrcamento.class))).thenAnswer(i -> i.getArgument(0));

        SolicitacaoOrcamento salva = solicitacaoService.salvar(solicitacao);

        assertThat(salva.getStatusOrcamento()).isEqualTo("PENDENTE");
        assertThat(salva.getDataCriacao()).isNotNull();
    }

    @Test
    void salvar_comStatusJaDefinido_deveManterStatus() {
        solicitacao.setStatusOrcamento("APROVADO");
        when(solicitacaoRepository.save(any(SolicitacaoOrcamento.class))).thenAnswer(i -> i.getArgument(0));

        SolicitacaoOrcamento salva = solicitacaoService.salvar(solicitacao);

        assertThat(salva.getStatusOrcamento()).isEqualTo("APROVADO");
    }

    @Test
    void salvar_semCliente_deveLancarBadRequestException() {
        solicitacao.setCliente(null);

        assertThatThrownBy(() -> solicitacaoService.salvar(solicitacao))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void salvar_clienteSemId_deveLancarBadRequestException() {
        cliente.setId(null);

        assertThatThrownBy(() -> solicitacaoService.salvar(solicitacao))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void atualizar_existente_deveAtualizar() {
        LocalDateTime dataOriginal = LocalDateTime.now().minusDays(1);
        solicitacao.setDataCriacao(dataOriginal);
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.save(any(SolicitacaoOrcamento.class))).thenAnswer(i -> i.getArgument(0));

        SolicitacaoOrcamento atualizada = solicitacaoService.atualizar(1L, solicitacao);

        assertThat(atualizada.getId()).isEqualTo(1L);
        assertThat(atualizada.getDataCriacao()).isEqualTo(dataOriginal);
    }

    @Test
    void atualizar_inexistente_deveLancarException() {
        when(solicitacaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> solicitacaoService.atualizar(99L, solicitacao))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deletar_existente_deveDeletar() {
        when(solicitacaoRepository.existsById(1L)).thenReturn(true);

        solicitacaoService.deletar(1L);

        verify(solicitacaoRepository).deleteById(1L);
    }

    @Test
    void deletar_inexistente_deveLancarException() {
        when(solicitacaoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> solicitacaoService.deletar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void buscarPorStatus_deveRetornarFiltrados() {
        when(solicitacaoRepository.findByStatusOrcamento("PENDENTE")).thenReturn(List.of(solicitacao));

        List<SolicitacaoOrcamento> resultado = solicitacaoService.buscarPorStatus("PENDENTE");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorCliente_deveRetornarSolicitacoes() {
        when(solicitacaoRepository.findByClienteIdOrderByDataCriacaoDesc(1L)).thenReturn(List.of(solicitacao));

        List<SolicitacaoOrcamento> resultado = solicitacaoService.buscarPorCliente(1L);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void pertenceAoUsuario_solicitacaoDoUsuario_deveRetornarTrue() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));

        boolean resultado = solicitacaoService.pertenceAoUsuario(1L, "user@test.com");

        assertThat(resultado).isTrue();
    }

    @Test
    void pertenceAoUsuario_solicitacaoDeOutroUsuario_deveRetornarFalse() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacao));

        boolean resultado = solicitacaoService.pertenceAoUsuario(1L, "outro@test.com");

        assertThat(resultado).isFalse();
    }

    @Test
    void pertenceAoUsuario_solicitacaoInexistente_deveRetornarFalse() {
        when(solicitacaoRepository.findById(99L)).thenReturn(Optional.empty());

        boolean resultado = solicitacaoService.pertenceAoUsuario(99L, "user@test.com");

        assertThat(resultado).isFalse();
    }

    @Test
    void buscarPorUsuario_deveUsarQueryDoRepository() {
        when(solicitacaoRepository.findByClienteUsuarioLogin("user@test.com")).thenReturn(List.of(solicitacao));

        List<SolicitacaoOrcamento> resultado = solicitacaoService.buscarPorUsuario("user@test.com");

        assertThat(resultado).hasSize(1);
        verify(solicitacaoRepository).findByClienteUsuarioLogin("user@test.com");
        verify(solicitacaoRepository, never()).findAll();
    }
}
