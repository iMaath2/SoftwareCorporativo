package com.ifpe.edu.br.workflowmanagement.service.repositories;

import com.ifpe.edu.br.workflowmanagement.service.entities.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EtapaRepository extends JpaRepository<Etapa, Long> {

    // Encontra a primeira etapa de um fluxo (muito útil para quando criar uma nova tarefa)
    Optional<Etapa> findFirstByFluxoTrabalhoIdOrderByOrdemAsc(Long fluxoTrabalhoId);
}