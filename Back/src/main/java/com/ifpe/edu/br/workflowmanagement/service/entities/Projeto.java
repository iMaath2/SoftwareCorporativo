package com.ifpe.edu.br.workflowmanagement.service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "projetos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do projeto é obrigatório")
    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    // Gerente é obrigatório
    @NotNull(message = "O projeto deve ter um gerente responsável")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_id", nullable = false)
    private Usuario gerente;

    // Fluxo pode ser nulo inicialmente, mas se associado, usamos LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fluxo_trabalho_id")
    private FluxoTrabalho fluxoTrabalho;

    // Cascade ALL: Se deletar o projeto, deleta as tarefas
    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarefa> tarefas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    protected Projeto() {}

    public Projeto(String nome, String descricao, Usuario gerente, FluxoTrabalho fluxoTrabalho) {
        this.nome = nome;
        this.descricao = descricao;
        this.gerente = gerente;
        this.fluxoTrabalho = fluxoTrabalho;
    }

    // Métodos auxiliares para gerenciar a lista de tarefas e manter consistência
    public void adicionarTarefa(Tarefa tarefa) {
        tarefas.add(tarefa);
        tarefa.setProjeto(this);
    }

    public void removerTarefa(Tarefa tarefa) {
        tarefas.remove(tarefa);
        tarefa.setProjeto(null);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Usuario getGerente() { return gerente; }
    public void setGerente(Usuario gerente) { this.gerente = gerente; }

    public FluxoTrabalho getFluxoTrabalho() { return fluxoTrabalho; }
    public void setFluxoTrabalho(FluxoTrabalho fluxoTrabalho) { this.fluxoTrabalho = fluxoTrabalho; }

    public List<Tarefa> getTarefas() { return tarefas; }
    public void setTarefas(List<Tarefa> tarefas) { this.tarefas = tarefas; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
}