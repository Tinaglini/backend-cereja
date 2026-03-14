package com.example.festas.service;

import com.example.festas.entity.Endereco;
import com.example.festas.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    public List<Endereco> buscarTodos() {
        return enderecoRepository.findAll();
    }

    public Optional<Endereco> buscarPorId(Long id) {
        return enderecoRepository.findById(id);
    }

    public Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public Endereco atualizar(Long id, Endereco endereco) {
        Optional<Endereco> enderecoExistente = enderecoRepository.findById(id);
        if (enderecoExistente.isPresent()) {
            endereco.setId(id);
            return salvar(endereco);
        }
        throw new RuntimeException("Endereço não encontrado com ID: " + id);
    }

    public void deletar(Long id) {
        if (!enderecoRepository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar endereço inexistente");
        }
        enderecoRepository.deleteById(id);
    }

    public List<Endereco> buscarPorCidade(String cidade) {
        return enderecoRepository.findByCidadeIgnoreCaseContaining(cidade);
    }

    public List<Endereco> buscarPorEstado(String estado) {
        return enderecoRepository.findByEstado(estado);
    }
}