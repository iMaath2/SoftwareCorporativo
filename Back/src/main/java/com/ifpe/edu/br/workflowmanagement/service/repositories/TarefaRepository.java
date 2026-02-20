package com.ifpe.edu.br.workflowmanagement.service.repositories;

import com.ifpe.edu.br.workflowmanagement.service.entities.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByProjetoId(Long projetoId);

    List<Tarefa> findByResponsavelId(Long responsavelId);
    
    // Opcional: Busca tarefas por coluna/etapa (útil para carregar o Kanban)
    List<Tarefa> findByProjetoIdAndEtapaAtualId(Long projetoId, Long etapaId);
}