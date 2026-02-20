package com.ifpe.edu.br.workflowmanagement.service.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CadastroUsuarioDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    // --- NOVO CAMPO ---
    @NotBlank(message = "O sobrenome é obrigatório")
    private String sobrenome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    // Opcional, mas útil já que a entidade tem
    private String cargo;

    @NotNull(message = "O ID do papel é obrigatório")
    private Long papelId;

    // Construtor padrão
    public CadastroUsuarioDTO() {}

    // Construtor completo
    public CadastroUsuarioDTO(String nome, String sobrenome, String email, String senha, Long papelId, String cargo) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.senha = senha;
        this.papelId = papelId;
        this.cargo = cargo;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSobrenome() { return sobrenome; } // <--- Novo Getter
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; } // <--- Novo Setter

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Long getPapelId() { return papelId; }
    public void setPapelId(Long papelId) { this.papelId = papelId; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}