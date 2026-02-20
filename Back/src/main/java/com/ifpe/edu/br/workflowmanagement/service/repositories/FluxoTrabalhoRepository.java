package com.ifpe.edu.br.workflowmanagement.service.repositories;

import com.ifpe.edu.br.workflowmanagement.service.entities.FluxoTrabalho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FluxoTrabalhoRepository extends JpaRepository<FluxoTrabalho, Long> {
}