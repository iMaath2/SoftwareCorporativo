package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.entities.Etapa;
import com.ifpe.edu.br.workflowmanagement.service.entities.FluxoTrabalho;
import com.ifpe.edu.br.workflowmanagement.service.repositories.EtapaRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.FluxoTrabalhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FluxoTrabalhoService {

    private final FluxoTrabalhoRepository fluxoTrabalhoRepository;
    private final EtapaRepository etapaRepository;

    @Autowired
    public FluxoTrabalhoService(FluxoTrabalhoRepository fluxoTrabalhoRepository,
                                EtapaRepository etapaRepository) {
        this.fluxoTrabalhoRepository = fluxoTrabalhoRepository;
        this.etapaRepository = etapaRepository;
    }

    /**
     * Cria um novo fluxo de trabalho vazio.
     */
    @Transactional
    public FluxoTrabalho criarFluxo(String nome) {
        FluxoTrabalho fluxo = new FluxoTrabalho(nome);
        return fluxoTrabalhoRepository.save(fluxo);
    }

    /**
     * Adiciona uma etapa a um fluxo existente.
     */
    @Transactional
    public Etapa adicionarEtapa(Long idFluxo, String nomeEtapa, int ordem) {
        FluxoTrabalho fluxo = fluxoTrabalhoRepository.findById(idFluxo)
                .orElseThrow(() -> new RuntimeException("Fluxo de trabalho não encontrado."));

        // Cria a etapa e associa ao fluxo
        Etapa novaEtapa = new Etapa(nomeEtapa, ordem, fluxo);
        
        // Usa o método auxiliar da entidade para manter a lista consistente
        fluxo.adicionarEtapa(novaEtapa);
        
        // Salvamos o fluxo, e o CascadeType.ALL cuidará de salvar a etapa
        fluxoTrabalhoRepository.save(fluxo);
        
        return novaEtapa;
    }

    /**
     * Helper: Cria um fluxo padrão (Kanban Simples) com 3 etapas.
     * Útil para inicializar o sistema ou para novos usuários.
     */
    @Transactional
    public FluxoTrabalho criarFluxoPadraoKanban() {
        FluxoTrabalho fluxo = new FluxoTrabalho("Kanban Padrão");
        
        fluxo.adicionarEtapa(new Etapa("A Fazer", 1, fluxo));
        fluxo.adicionarEtapa(new Etapa("Em Progresso", 2, fluxo));
        fluxo.adicionarEtapa(new Etapa("Concluído", 3, fluxo));

        return fluxoTrabalhoRepository.save(fluxo);
    }

    @Transactional(readOnly = true)
    public List<FluxoTrabalho> listarTodos() {
        return fluxoTrabalhoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public FluxoTrabalho buscarPorId(Long id) {
        return fluxoTrabalhoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fluxo não encontrado"));
    }
    
    @Transactional
    public void excluirFluxo(Long id) {
        // Regra: Só pode excluir se não estiver em uso por projetos (Validação de integridade)
        // O banco de dados provavelmente lançará exceção de ConstraintViolation se tentarmos deletar
        // um fluxo que está ligado a um Projeto. O ideal é tratar isso com try-catch no Controller.
        fluxoTrabalhoRepository.deleteById(id);
    }
}