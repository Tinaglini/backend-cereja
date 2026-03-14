package com.example.festas.service;

import com.example.festas.entity.Endereco;
import com.example.festas.exception.ResourceNotFoundException;
import com.example.festas.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    @Transactional
    public Endereco atualizar(Long id, Endereco endereco) {
        enderecoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com ID: " + id));
        endereco.setId(id);
        return salvar(endereco);
    }

    @Transactional
    public void deletar(Long id) {
        if (!enderecoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Endereço não encontrado com ID: " + id);
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
