package com.ifpe.edu.br.workflowmanagement.service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "fluxos_trabalho")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FluxoTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do fluxo é obrigatório")
    @Column(nullable = false, length = 255)
    private String nome;

    // @OrderBy garante que o JPA traga as etapas ordenadas do banco automaticamente
    @OneToMany(mappedBy = "fluxoTrabalho", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC") 
    private List<Etapa> etapas = new ArrayList<>();

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    public FluxoTrabalho() {}

    public FluxoTrabalho(String nome) {
        this.nome = nome;
    }

    // Método auxiliar para manter a consistência da relação bidirecional
    public void adicionarEtapa(Etapa etapa) {
        etapas.add(etapa);
        etapa.setFluxoTrabalho(this);
    }

    public void removerEtapa(Etapa etapa) {
        etapas.remove(etapa);
        etapa.setFluxoTrabalho(null);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Etapa> getEtapas() { return etapas; }
    public void setEtapas(List<Etapa> etapas) { this.etapas = etapas; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
}