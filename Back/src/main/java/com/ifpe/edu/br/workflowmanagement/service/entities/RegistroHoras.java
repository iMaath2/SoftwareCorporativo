package com.ifpe.edu.br.workflowmanagement.service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "registros_horas")
public class RegistroHoras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @JsonIgnore
    @NotNull(message = "A tarefa é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;


    @NotNull(message = "A quantidade de horas é obrigatória")
    @Positive(message = "A quantidade de horas deve ser positiva")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal horas;

    @NotNull(message = "A data do registro é obrigatória")
    @PastOrPresent(message = "A data não pode ser no futuro")
    @Column(nullable = false)
    private LocalDate dataRegistro;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    protected RegistroHoras() {}

    public RegistroHoras(Usuario usuario, Tarefa tarefa, BigDecimal horas, LocalDate dataRegistro) {
        this.usuario = usuario;
        this.tarefa = tarefa;
        this.horas = horas;
        this.dataRegistro = dataRegistro;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }

    public BigDecimal getHoras() { return horas; }
    public void setHoras(BigDecimal horas) { this.horas = horas; }

    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
}