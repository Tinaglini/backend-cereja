package com.example.festas.service;

import com.example.festas.entity.TipoEvento;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.TipoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TipoEventoService {

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    public List<TipoEvento> buscarTodos() {
        return tipoEventoRepository.findAll();
    }

    public Optional<TipoEvento> buscarPorId(Long id) {
        return tipoEventoRepository.findById(id);
    }

    @Transactional
    public TipoEvento salvar(TipoEvento tipoEvento) {
        return tipoEventoRepository.save(tipoEvento);
    }

    @Transactional
    public TipoEvento atualizar(Long id, TipoEvento tipoEvento) {
        tipoEventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evento não encontrado com ID: " + id));
        tipoEvento.setId(id);
        return salvar(tipoEvento);
    }

    @Transactional
    public void deletar(Long id) {
        if (!tipoEventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de evento não encontrado com ID: " + id);
        }
        tipoEventoRepository.deleteById(id);
    }

    public List<TipoEvento> buscarPorNome(String nome) {
        return tipoEventoRepository.findByNomeIgnoreCaseContaining(nome);
    }

    public List<TipoEvento> buscarPorCapacidade(Integer capacidade) {
        return tipoEventoRepository.findByCapacidadeMinimaLessThanEqualAndCapacidadeMaximaGreaterThanEqual(capacidade, capacidade);
    }
}
