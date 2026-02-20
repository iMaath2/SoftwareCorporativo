package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.Comentario;
import com.ifpe.edu.br.workflowmanagement.service.entities.Tarefa;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import com.ifpe.edu.br.workflowmanagement.service.repositories.ComentarioRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.TarefaRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ComentarioService(ComentarioRepository comentarioRepository,
                             TarefaRepository tarefaRepository,
                             UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Comentario criarComentario(Long idTarefa, String texto, Long idAutor) {
        Tarefa tarefa = tarefaRepository.findById(idTarefa)
            .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));
        Usuario autor = usuarioRepository.findById(idAutor)
            .orElseThrow(() -> new RuntimeException("Usuário autor não encontrado."));

        Comentario novoComentario = new Comentario(autor, tarefa, texto);
        return comentarioRepository.save(novoComentario);
    }

    @Transactional
    public Comentario editarComentario(Long idComentario, String novoTexto, Long idUsuarioExecutor) {
        Comentario comentario = comentarioRepository.findById(idComentario)
            .orElseThrow(() -> new RuntimeException("Comentário não encontrado."));

        // Regra: Apenas o autor original pode editar o texto
        if (!comentario.getAutor().getId().equals(idUsuarioExecutor)) {
            throw new SecurityException("Apenas o autor pode editar seu próprio comentário.");
        }

        comentario.setTexto(novoTexto); // Ajustado para setTexto (conforme Entidade)
        return comentarioRepository.save(comentario);
    }

    @Transactional
    public void excluirComentario(Long idComentario, Long idUsuarioExecutor) {
        Comentario comentario = comentarioRepository.findById(idComentario)
            .orElseThrow(() -> new RuntimeException("Comentário não encontrado."));
        
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
                .orElseThrow(() -> new RuntimeException("Usuário executor não encontrado."));

        // Regra: Autor OU Gerente do Projeto OU Admin podem excluir
        boolean isAutor = comentario.getAutor().getId().equals(idUsuarioExecutor);
        boolean isGerente = comentario.getTarefa().getProjeto().getGerente().getId().equals(idUsuarioExecutor);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(executor.getPapel().getNome());

        if (!isAutor && !isGerente && !isAdmin) {
             throw new SecurityException("Sem permissão para excluir este comentário.");
        }

        comentarioRepository.deleteById(idComentario);
    }

    @Transactional(readOnly = true)
    public List<Comentario> buscarComentariosPorTarefa(Long idTarefa) {
        return comentarioRepository.findByTarefaIdOrderByDataCriacaoAsc(idTarefa);
    }
}