package com.example.festas.service;

import com.example.festas.entity.TipoEvento;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.TipoEventoRepository;
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
class TipoEventoServiceTest {

    @Mock
    private TipoEventoRepository tipoEventoRepository;

    @InjectMocks
    private TipoEventoService tipoEventoService;

    private TipoEvento tipoEvento;

    @BeforeEach
    void setUp() {
        tipoEvento = new TipoEvento();
        tipoEvento.setId(1L);
        tipoEvento.setNome("Aniversário");
        tipoEvento.setCapacidadeMinima(10);
        tipoEvento.setCapacidadeMaxima(100);
    }

    @Test
    void buscarTodos_deveRetornarLista() {
        when(tipoEventoRepository.findAll()).thenReturn(List.of(tipoEvento));

        List<TipoEvento> resultado = tipoEventoService.buscarTodos();

        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorId_deveRetornarTipoEvento() {
        when(tipoEventoRepository.findById(1L)).thenReturn(Optional.of(tipoEvento));

        Optional<TipoEvento> resultado = tipoEventoService.buscarPorId(1L);

        assertThat(resultado).isPresent();
    }

    @Test
    void salvar_devePersistir() {
        when(tipoEventoRepository.save(any(TipoEvento.class))).thenReturn(tipoEvento);

        TipoEvento salvo = tipoEventoService.salvar(tipoEvento);

        assertThat(salvo.getNome()).isEqualTo("Aniversário");
        verify(tipoEventoRepository).save(tipoEvento);
    }

    @Test
    void atualizar_existente_deveAtualizar() {
        when(tipoEventoRepository.findById(1L)).thenReturn(Optional.of(tipoEvento));
        when(tipoEventoRepository.save(any(TipoEvento.class))).thenReturn(tipoEvento);

        TipoEvento atualizado = tipoEventoService.atualizar(1L, tipoEvento);

        assertThat(atualizado.getId()).isEqualTo(1L);
    }

    @Test
    void atualizar_inexistente_deveLancarException() {
        when(tipoEventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoEventoService.atualizar(99L, tipoEvento))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deletar_existente_deveDeletar() {
        when(tipoEventoRepository.existsById(1L)).thenReturn(true);

        tipoEventoService.deletar(1L);

        verify(tipoEventoRepository).deleteById(1L);
    }

    @Test
    void deletar_inexistente_deveLancarException() {
        when(tipoEventoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> tipoEventoService.deletar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void buscarPorNome_deveRetornarResultados() {
        when(tipoEventoRepository.findByNomeIgnoreCaseContaining("Aniv")).thenReturn(List.of(tipoEvento));

        List<TipoEvento> resultado = tipoEventoService.buscarPorNome("Aniv");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorCapacidade_deveRetornarCompatíveis() {
        when(tipoEventoRepository.findByCapacidadeMinimaLessThanEqualAndCapacidadeMaximaGreaterThanEqual(50, 50))
                .thenReturn(List.of(tipoEvento));

        List<TipoEvento> resultado = tipoEventoService.buscarPorCapacidade(50);

        assertThat(resultado).hasSize(1);
    }
}
