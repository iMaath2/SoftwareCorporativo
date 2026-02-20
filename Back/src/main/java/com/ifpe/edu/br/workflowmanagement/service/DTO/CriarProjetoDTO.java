package com.ifpe.edu.br.workflowmanagement.service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CriarProjetoDTO {

    @NotBlank(message = "O nome do projeto é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "O ID do gerente é obrigatório")
    private Long gerenteId;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Long getGerenteId() { return gerenteId; }
    public void setGerenteId(Long gerenteId) { this.gerenteId = gerenteId; }
}