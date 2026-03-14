package com.example.festas.service;

import com.example.festas.entity.TemaFesta;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.TemaFestaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemaFestaServiceTest {

    @Mock
    private TemaFestaRepository temaRepository;

    @InjectMocks
    private TemaFestaService temaFestaService;

    private TemaFesta tema;

    @BeforeEach
    void setUp() {
        tema = new TemaFesta();
        tema.setId(1L);
        tema.setNome("Jardim Encantado");
        tema.setPrecoBase(new BigDecimal("1500.00"));
    }

    @Test
    void buscarTodos_deveRetornarLista() {
        when(temaRepository.findAll()).thenReturn(List.of(tema));

        List<TemaFesta> resultado = temaFestaService.buscarTodos();

        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorId_deveRetornarTema() {
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));

        Optional<TemaFesta> resultado = temaFestaService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Jardim Encantado");
    }

    @Test
    void salvar_semAtivo_deveDefinirAtivoComoTrue() {
        when(temaRepository.save(any(TemaFesta.class))).thenAnswer(i -> i.getArgument(0));

        TemaFesta salvo = temaFestaService.salvar(tema);

        assertThat(salvo.getAtivo()).isTrue();
    }

    @Test
    void salvar_comAtivoDefinido_deveManterValor() {
        tema.setAtivo(false);
        when(temaRepository.save(any(TemaFesta.class))).thenAnswer(i -> i.getArgument(0));

        TemaFesta salvo = temaFestaService.salvar(tema);

        assertThat(salvo.getAtivo()).isFalse();
    }

    @Test
    void atualizar_temaExistente_deveAtualizar() {
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
        when(temaRepository.save(any(TemaFesta.class))).thenAnswer(i -> i.getArgument(0));

        TemaFesta atualizado = temaFestaService.atualizar(1L, tema);

        assertThat(atualizado.getId()).isEqualTo(1L);
    }

    @Test
    void atualizar_temaInexistente_deveLancarException() {
        when(temaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> temaFestaService.atualizar(99L, tema))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deletar_temaExistente_deveDeletar() {
        when(temaRepository.existsById(1L)).thenReturn(true);

        temaFestaService.deletar(1L);

        verify(temaRepository).deleteById(1L);
    }

    @Test
    void deletar_temaInexistente_deveLancarException() {
        when(temaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> temaFestaService.deletar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void buscarPorNome_deveRetornarTemas() {
        when(temaRepository.findByNomeIgnoreCaseContaining("Jardim")).thenReturn(List.of(tema));

        List<TemaFesta> resultado = temaFestaService.buscarPorNome("Jardim");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarAtivos_deveRetornarApenasAtivos() {
        tema.setAtivo(true);
        when(temaRepository.findByAtivo(true)).thenReturn(List.of(tema));

        List<TemaFesta> resultado = temaFestaService.buscarAtivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getAtivo()).isTrue();
    }
}
