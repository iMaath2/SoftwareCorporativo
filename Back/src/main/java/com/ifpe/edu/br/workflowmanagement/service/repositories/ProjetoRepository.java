package com.ifpe.edu.br.workflowmanagement.service.repositories;

import com.ifpe.edu.br.workflowmanagement.service.entities.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    List<Projeto> findByGerenteId(Long idGerente);
}