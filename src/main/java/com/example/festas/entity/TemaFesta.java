package com.example.festas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class TemaFesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do tema é obrigatório")
    private String nome;

    private String descricao;

    private BigDecimal precoBase;

    private Boolean ativo;

    @ManyToMany(mappedBy = "temas")
    @JsonIgnoreProperties("temas")
    private List<SolicitacaoOrcamento> solicitacoes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public BigDecimal getPrecoBase() { return precoBase; }
    public void setPrecoBase(BigDecimal precoBase) { this.precoBase = precoBase; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    
    public List<SolicitacaoOrcamento> getSolicitacoes() { return solicitacoes; }
    public void setSolicitacoes(List<SolicitacaoOrcamento> solicitacoes) { this.solicitacoes = solicitacoes; }
}