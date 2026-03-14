package com.example.festas.service;

import com.example.festas.entity.TipoEvento;
import com.example.festas.repository.TipoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public TipoEvento salvar(TipoEvento tipoEvento) {
        return tipoEventoRepository.save(tipoEvento);
    }

    public TipoEvento atualizar(Long id, TipoEvento tipoEvento) {
        Optional<TipoEvento> tipoExistente = tipoEventoRepository.findById(id);
        if (tipoExistente.isPresent()) {
            tipoEvento.setId(id);
            return salvar(tipoEvento);
        }
        throw new RuntimeException("Tipo de evento não encontrado com ID: " + id);
    }

    public void deletar(Long id) {
        if (!tipoEventoRepository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar tipo de evento inexistente");
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