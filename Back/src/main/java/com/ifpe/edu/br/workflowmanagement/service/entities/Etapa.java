package com.ifpe.edu.br.workflowmanagement.service.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType; // <--- Importe isso
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "etapas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // <--- Adicione essa linha
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da etapa é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private int ordem;

    @JsonIgnore
    @NotNull(message = "A etapa deve pertencer a um fluxo de trabalho")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fluxo_trabalho_id", nullable = false)
    private FluxoTrabalho fluxoTrabalho;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    protected Etapa() {}

    public Etapa(String nome, int ordem, FluxoTrabalho fluxoTrabalho) {
        this.nome = nome;
        this.ordem = ordem;
        this.fluxoTrabalho = fluxoTrabalho;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }

    public FluxoTrabalho getFluxoTrabalho() { return fluxoTrabalho; }
    public void setFluxoTrabalho(FluxoTrabalho fluxoTrabalho) { this.fluxoTrabalho = fluxoTrabalho; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
}