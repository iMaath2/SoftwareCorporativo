package com.ifpe.edu.br.workflowmanagement.service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A notificação deve ter um destinatário")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsavel_id", nullable = false)
    private Usuario usuarioResponsavel;

    @NotBlank(message = "A mensagem da notificação é obrigatória")
    @Column(nullable = false, length = 1000)
    private String mensagem;

    @Column(nullable = false)
    private boolean lida = false;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    protected Notificacao() {}

    public Notificacao(Usuario usuarioResponsavel, String mensagem) {
        this.usuarioResponsavel = usuarioResponsavel;
        this.mensagem = mensagem;
    }

    // Método de negócio para alterar status
    public void marcarComoLida() {
        this.lida = true;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuarioResponsavel() { return usuarioResponsavel; }
    public void setUsuarioResponsavel(Usuario usuarioResponsavel) { this.usuarioResponsavel = usuarioResponsavel; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
}