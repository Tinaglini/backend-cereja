package com.example.festas.service;

import com.example.festas.entity.Cliente;
import com.example.festas.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        // REGRA DE NEGÓCIO COMPLEXA: Verificar status do cadastro antes de persistir
        if (cliente.getTelefone() == null || cliente.getTelefone().trim().isEmpty()) {
            cliente.setStatusCadastro("INCOMPLETO");
        } else {
            cliente.setStatusCadastro("COMPLETO");
        }

        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente cliente) {
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);
        if (clienteExistente.isPresent()) {
            cliente.setId(id);
            return salvar(cliente);
        }
        throw new RuntimeException("Cliente não encontrado com ID: " + id);
    }

    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar cliente inexistente");
        }
        clienteRepository.deleteById(id);
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeIgnoreCaseContaining(nome);
    }

    public Cliente buscarPorTelefone(String telefone) {
        return clienteRepository.findByTelefone(telefone);
    }

    public List<Cliente> buscarPorStatus(String status) {
        return clienteRepository.findByStatusCadastro(status);
    }
}