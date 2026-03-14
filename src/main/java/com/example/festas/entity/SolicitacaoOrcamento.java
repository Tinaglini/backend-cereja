package com.example.festas.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class SolicitacaoOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O cliente é obrigatório")
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonIgnoreProperties("solicitacoes")
    private Cliente cliente;

    @NotNull(message = "A data do evento é obrigatória")
    private LocalDate dataEvento;

    @NotNull(message = "O endereço é obrigatório")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    @JsonIgnoreProperties("solicitacoes")
    private Endereco endereco;

    @NotNull(message = "A quantidade de convidados é obrigatória")
    private Integer quantidadeConvidados;

    private Boolean precisaMesasCadeiras;

    @NotNull(message = "O tipo de evento é obrigatório")
    @ManyToOne
    @JoinColumn(name = "tipo_evento_id")
    @JsonIgnoreProperties("solicitacoes")
    private TipoEvento tipoEvento;

    private BigDecimal valorPretendido;

    private String statusOrcamento; // PENDENTE, APROVADO, REJEITADO

    private LocalDateTime dataCriacao;

    @ManyToMany
    @JoinTable(
            name = "solicitacao_tema",
            joinColumns = @JoinColumn(name = "solicitacao_id"),
            inverseJoinColumns = @JoinColumn(name = "tema_id")
    )
    @JsonIgnoreProperties("solicitacoes")
    private List<TemaFesta> temas;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public LocalDate getDataEvento() { return dataEvento; }
    public void setDataEvento(LocalDate dataEvento) { this.dataEvento = dataEvento; }
    
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    
    public Integer getQuantidadeConvidados() { return quantidadeConvidados; }
    public void setQuantidadeConvidados(Integer quantidadeConvidados) { this.quantidadeConvidados = quantidadeConvidados; }
    
    public Boolean getPrecisaMesasCadeiras() { return precisaMesasCadeiras; }
    public void setPrecisaMesasCadeiras(Boolean precisaMesasCadeiras) { this.precisaMesasCadeiras = precisaMesasCadeiras; }
    
    public TipoEvento getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; }
    
    public BigDecimal getValorPretendido() { return valorPretendido; }
    public void setValorPretendido(BigDecimal valorPretendido) { this.valorPretendido = valorPretendido; }
    
    public String getStatusOrcamento() { return statusOrcamento; }
    public void setStatusOrcamento(String statusOrcamento) { this.statusOrcamento = statusOrcamento; }
    
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public List<TemaFesta> getTemas() { return temas; }
    public void setTemas(List<TemaFesta> temas) { this.temas = temas; }
}