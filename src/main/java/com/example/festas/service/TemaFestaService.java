package com.example.festas.service;

import com.example.festas.entity.TemaFesta;
import com.example.festas.repository.TemaFestaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public TemaFesta salvar(TemaFesta tema) {
        // REGRA DE NEGÓCIO: Definir como ativo por padrão antes de persistir
        if (tema.getAtivo() == null) {
            tema.setAtivo(true);
        }
        return temaRepository.save(tema);
    }

    public TemaFesta atualizar(Long id, TemaFesta tema) {
        Optional<TemaFesta> temaExistente = temaRepository.findById(id);
        if (temaExistente.isPresent()) {
            tema.setId(id);
            return salvar(tema);
        }
        throw new RuntimeException("Tema não encontrado com ID: " + id);
    }

    public void deletar(Long id) {
        if (!temaRepository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar tema inexistente");
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