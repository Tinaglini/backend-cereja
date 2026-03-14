package com.example.festas.service;

import com.example.festas.entity.TemaFesta;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.TemaFestaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TemaFestaService {

    @Autowired
    private TemaFestaRepository temaRepository;

    public List<TemaFesta> buscarTodos() {
        return temaRepository.findAll();
    }

    public Optional<TemaFesta> buscarPorId(Long id) {
        return temaRepository.findById(id);
    }

    @Transactional
    public TemaFesta salvar(TemaFesta tema) {
        if (tema.getAtivo() == null) {
            tema.setAtivo(true);
        }
        return temaRepository.save(tema);
    }

    @Transactional
    public TemaFesta atualizar(Long id, TemaFesta tema) {
        temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema não encontrado com ID: " + id));
        tema.setId(id);
        return salvar(tema);
    }

    @Transactional
    public void deletar(Long id) {
        if (!temaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tema não encontrado com ID: " + id);
        }
        temaRepository.deleteById(id);
    }

    public List<TemaFesta> buscarPorNome(String nome) {
        return temaRepository.findByNomeIgnoreCaseContaining(nome);
    }

    public List<TemaFesta> buscarAtivos() {
        return temaRepository.findByAtivo(true);
    }
}
