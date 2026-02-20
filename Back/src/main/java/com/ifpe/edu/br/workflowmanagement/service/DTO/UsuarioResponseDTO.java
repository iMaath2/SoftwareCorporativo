package com.ifpe.edu.br.workflowmanagement.service.DTO;

import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;

public class UsuarioResponseDTO {
    
    private Long id;
    private String nome;
    private String email;
    private String papel; // Apenas o nome do papel (ex: "ADMIN")

    // Construtor padrão
    public UsuarioResponseDTO() {}

    // Construtor completo
    public UsuarioResponseDTO(Long id, String nome, String email, String papel) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.papel = papel;
    }

    // Construtor auxiliar que converte Entidade -> DTO (Facilita muito no Service)
    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        // Verifica se o papel existe para evitar NullPointerException
        this.papel = (usuario.getPapel() != null) ? usuario.getPapel().getNome() : null;
    }

    // Getters e Setters (Necessários para o JSON de resposta)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }
}