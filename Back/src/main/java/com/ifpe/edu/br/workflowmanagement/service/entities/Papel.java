package com.ifpe.edu.br.workflowmanagement.service.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "papeis")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Papel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do papel é obrigatório") // Validação
    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    // Construtor protegido para uso do JPA
    protected Papel() {}

    public Papel(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}