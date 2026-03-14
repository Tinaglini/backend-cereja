package com.example.festas.service;

import com.example.festas.entity.Cliente;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setTelefone("11999999999");
    }

    @Test
    void buscarTodos_deveRetornarListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<Cliente> resultado = clienteService.buscarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
    }

    @Test
    void buscarPorId_deveRetornarClienteExistente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("João Silva");
    }

    @Test
    void buscarPorId_deveRetornarVazioParaIdInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.buscarPorId(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void salvar_comTelefone_deveDefinirStatusCompleto() {
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        Cliente salvo = clienteService.salvar(cliente);

        assertThat(salvo.getStatusCadastro()).isEqualTo("COMPLETO");
        verify(clienteRepository).save(cliente);
    }

    @Test
    void salvar_semTelefone_deveDefinirStatusIncompleto() {
        cliente.setTelefone(null);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        Cliente salvo = clienteService.salvar(cliente);

        assertThat(salvo.getStatusCadastro()).isEqualTo("INCOMPLETO");
    }

    @Test
    void salvar_comTelefoneVazio_deveDefinirStatusIncompleto() {
        cliente.setTelefone("   ");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        Cliente salvo = clienteService.salvar(cliente);

        assertThat(salvo.getStatusCadastro()).isEqualTo("INCOMPLETO");
    }

    @Test
    void atualizar_clienteExistente_deveAtualizarERetornar() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        Cliente atualizado = clienteService.atualizar(1L, cliente);

        assertThat(atualizado.getId()).isEqualTo(1L);
    }

    @Test
    void atualizar_clienteInexistente_deveLancarResourceNotFoundException() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.atualizar(99L, cliente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deletar_clienteExistente_deveDeletar() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        clienteService.deletar(1L);

        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void deletar_clienteInexistente_deveLancarResourceNotFoundException() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> clienteService.deletar(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void buscarPorNome_deveRetornarClientes() {
        when(clienteRepository.findByNomeIgnoreCaseContaining("João")).thenReturn(List.of(cliente));

        List<Cliente> resultado = clienteService.buscarPorNome("João");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorTelefone_deveRetornarCliente() {
        when(clienteRepository.findByTelefone("11999999999")).thenReturn(cliente);

        Cliente resultado = clienteService.buscarPorTelefone("11999999999");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTelefone()).isEqualTo("11999999999");
    }

    @Test
    void buscarPorStatus_deveRetornarClientes() {
        when(clienteRepository.findByStatusCadastro("COMPLETO")).thenReturn(List.of(cliente));

        List<Cliente> resultado = clienteService.buscarPorStatus("COMPLETO");

        assertThat(resultado).hasSize(1);
    }
}
