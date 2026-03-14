package com.example.festas.service;

import com.example.festas.entity.Endereco;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.EnderecoRepository;
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
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private EnderecoService enderecoService;

    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01310-100");
    }

    @Test
    void buscarTodos_deveRetornarLista() {
        when(enderecoRepository.findAll()).thenReturn(List.of(endereco));

        List<Endereco> resultado = enderecoService.buscarTodos();

        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorId_deveRetornarEndereco() {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        Optional<Endereco> resultado = enderecoService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCidade()).isEqualTo("São Paulo");
    }

    @Test
    void salvar_devePersistir() {
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        Endereco salvo = enderecoService.salvar(endereco);

        assertThat(salvo).isNotNull();
        verify(enderecoRepository).save(endereco);
    }

    @Test
    void atualizar_existente_deveAtualizar() {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        Endereco atualizado = enderecoService.atualizar(1L, endereco);

        assertThat(atualizado.getId()).isEqualTo(1L);
    }

    @Test
    void atualizar_inexistente_deveLancarException() {
        when(enderecoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enderecoService.atualizar(99L, endereco))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deletar_existente_deveDeletar() {
        when(enderecoRepository.existsById(1L)).thenReturn(true);

        enderecoService.deletar(1L);

        verify(enderecoRepository).deleteById(1L);
    }

    @Test
    void deletar_inexistente_deveLancarException() {
        when(enderecoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> enderecoService.deletar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void buscarPorCidade_deveRetornarEnderecos() {
        when(enderecoRepository.findByCidadeIgnoreCaseContaining("Paulo")).thenReturn(List.of(endereco));

        List<Endereco> resultado = enderecoService.buscarPorCidade("Paulo");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorEstado_deveRetornarEnderecos() {
        when(enderecoRepository.findByEstado("SP")).thenReturn(List.of(endereco));

        List<Endereco> resultado = enderecoService.buscarPorEstado("SP");

        assertThat(resultado).hasSize(1);
    }
}
