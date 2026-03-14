package com.example.festas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Entity
public class TipoEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do tipo de evento é obrigatório")
    private String nome;

    private String descricao;

    private Integer capacidadeMinima;

    private Integer capacidadeMaxima;

    @OneToMany(mappedBy = "tipoEvento")
    @JsonIgnoreProperties("tipoEvento")
    private List<SolicitacaoOrcamento> solicitacoes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public Integer getCapacidadeMinima() { return capacidadeMinima; }
    public void setCapacidadeMinima(Integer capacidadeMinima) { this.capacidadeMinima = capacidadeMinima; }
    
    public Integer getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(Integer capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }
    
    public List<SolicitacaoOrcamento> getSolicitacoes() { return solicitacoes; }
    public void setSolicitacoes(List<SolicitacaoOrcamento> solicitacoes) { this.solicitacoes = solicitacoes; }
}