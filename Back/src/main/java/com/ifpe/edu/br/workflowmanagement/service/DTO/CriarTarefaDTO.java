package com.ifpe.edu.br.workflowmanagement.service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CriarTarefaDTO {

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;

    @NotNull(message = "O ID do projeto é obrigatório")
    private Long projetoId;

    private Long responsavelId;

    @NotNull(message = "O ID do criador é obrigatório")
    private Long criadorId;

    // --- NOVOS CAMPOS ---
    private String prioridade;
    private String categoria;
    private Integer horasEstimadas;
    private LocalDate dataLimite;

    // Getters
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public Long getProjetoId() { return projetoId; }
    public Long getResponsavelId() { return responsavelId; }
    public Long getCriadorId() { return criadorId; }
    public String getPrioridade() { return prioridade; }
    public String getCategoria() { return categoria; }

    // Setters
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }
    public void setCriadorId(Long criadorId) { this.criadorId = criadorId; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(Integer horasEstimadas) { this.horasEstimadas = horasEstimadas; }
    public LocalDate getDataLimite() { return dataLimite; }
    public void setDataLimite(LocalDate dataLimite) { this.dataLimite = dataLimite; }
}