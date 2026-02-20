package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.Etapa;
import com.ifpe.edu.br.workflowmanagement.service.entities.FluxoTrabalho;
import com.ifpe.edu.br.workflowmanagement.service.entities.Projeto;
import com.ifpe.edu.br.workflowmanagement.service.entities.Tarefa;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import com.ifpe.edu.br.workflowmanagement.service.repositories.EtapaRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.FluxoTrabalhoRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.ProjetoRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FluxoTrabalhoRepository fluxoTrabalhoRepository;
    private final EtapaRepository etapaRepository; // <-- ADICIONADO AQUI

    @Autowired
    public ProjetoService(ProjetoRepository projetoRepository,
                          UsuarioRepository usuarioRepository,
                          FluxoTrabalhoRepository fluxoTrabalhoRepository,
                          EtapaRepository etapaRepository) { // <-- INJETADO NO CONSTRUTOR
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fluxoTrabalhoRepository = fluxoTrabalhoRepository;
        this.etapaRepository = etapaRepository;
    }

    // --- Métodos de Escrita (Criação e Edição) ---

    @Transactional
    public Projeto criarProjeto(String nome, String descricao, Long idUsuarioGerente) {
        Usuario gerente = usuarioRepository.findById(idUsuarioGerente)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado."));

        // Permissão: Apenas Gerentes ou Admins
        if (!isGerenteOuAdmin(gerente)) {
            throw new SecurityException("Apenas Gerentes ou Admins podem criar projetos.");
        }

        // 1. Cria e salva o projeto inicial sem fluxo
        Projeto novoProjeto = new Projeto(nome, descricao, gerente, null);
        Projeto projetoSalvo = projetoRepository.save(novoProjeto);

        // 2. Cria o Fluxo de Trabalho padrão para este novo projeto
        FluxoTrabalho fluxoPadrao = new FluxoTrabalho();
        fluxoPadrao.setNome("Kanban Padrão");
        FluxoTrabalho fluxoSalvo = fluxoTrabalhoRepository.save(fluxoPadrao);

// 3. Cria as 4 etapas passando o nome, a ordem e o fluxo no próprio construtor
        Etapa etapa1 = new Etapa("A Fazer", 1, fluxoSalvo);
        etapaRepository.save(etapa1);

        Etapa etapa2 = new Etapa("Em Andamento", 2, fluxoSalvo);
        etapaRepository.save(etapa2);

        Etapa etapa3 = new Etapa("Em Revisão (QA)", 3, fluxoSalvo);
        etapaRepository.save(etapa3);

        Etapa etapa4 = new Etapa("Concluído", 4, fluxoSalvo);
        etapaRepository.save(etapa4);
        // 4. Associa o fluxo criado ao projeto e salva novamente
        projetoSalvo.setFluxoTrabalho(fluxoSalvo);
        return projetoRepository.save(projetoSalvo);
    }

    @Transactional
    public Projeto associarFluxoTrabalho(Long idProjeto, Long idFluxo, Long idUsuarioExecutor) {
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
                .orElseThrow(() -> new RuntimeException("Usuário executor não encontrado."));
        FluxoTrabalho fluxo = fluxoTrabalhoRepository.findById(idFluxo)
                .orElseThrow(() -> new RuntimeException("Fluxo não encontrado."));

        // Permissão: Dono do projeto ou Admin
        if (!projeto.getGerente().getId().equals(executor.getId()) && !isAdmin(executor)) {
            throw new SecurityException("Sem permissão para associar fluxo.");
        }

        projeto.setFluxoTrabalho(fluxo);
        return projetoRepository.save(projeto);
    }

    // --- Métodos de Leitura (GET) ---

    @Transactional(readOnly = true)
    public List<Projeto> listarTodos() {
        return projetoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Projeto buscarPorId(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado com ID: " + id));
    }

    // --- Relatórios ---

    @Transactional(readOnly = true)
    public String gerarRelatorio(Long idProjeto, Long idUsuarioExecutor) {
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!projeto.getGerente().getId().equals(executor.getId()) && !isAdmin(executor)) {
            throw new SecurityException("Usuário não tem permissão para gerar relatórios deste projeto.");
        }

        List<Tarefa> tarefas = projeto.getTarefas();
        long totalTarefas = tarefas.size();

        long tarefasConcluidas = tarefas.stream()
                .filter(t -> t.getEtapaAtual() != null &&
                        (t.getEtapaAtual().getNome().toLowerCase().contains("concluído") ||
                                t.getEtapaAtual().getNome().toLowerCase().contains("done")))
                .count();

        StringBuilder sb = new StringBuilder();
        sb.append("=== Relatório do Projeto: ").append(projeto.getNome()).append(" ===\n");
        sb.append("Gerente: ").append(projeto.getGerente().getNome()).append("\n");
        sb.append("Total de Tarefas: ").append(totalTarefas).append("\n");
        sb.append("Tarefas Concluídas: ").append(tarefasConcluidas).append("\n");

        if (totalTarefas > 0) {
            double progresso = (double) tarefasConcluidas / totalTarefas * 100;
            sb.append(String.format("Progresso: %.2f%%\n", progresso));
        } else {
            sb.append("Progresso: 0.00% (Sem tarefas)\n");
        }

        return sb.toString();
    }

    // --- Métodos Auxiliares de Permissão ---

    private boolean isAdmin(Usuario u) {
        return u.getPapel() != null && "ADMIN".equalsIgnoreCase(u.getPapel().getNome());
    }

    private boolean isGerenteOuAdmin(Usuario u) {
        if (u.getPapel() == null) return false;
        String papel = u.getPapel().getNome().toUpperCase();
        return "ADMIN".equals(papel) || "GERENTE".equals(papel) || papel.contains("GERENTE");
    }

    @Transactional
    public void excluirProjeto(Long idProjeto, Long idUsuarioExecutor) {
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));

        Usuario executor = usuarioRepository.findById(idUsuarioExecutor)
                .orElseThrow(() -> new RuntimeException("Usuário executor não encontrado."));

        boolean isGerenteDoProjeto = projeto.getGerente().getId().equals(idUsuarioExecutor);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(executor.getPapel().getNome());

        if (!isGerenteDoProjeto && !isAdmin) {
            throw new SecurityException("Permissão negada. Apenas o Gerente do projeto ou Admin podem excluí-lo.");
        }

        projetoRepository.delete(projeto);
    }
}