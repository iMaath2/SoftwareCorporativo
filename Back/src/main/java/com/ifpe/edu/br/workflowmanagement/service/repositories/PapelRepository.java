package com.ifpe.edu.br.workflowmanagement.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ifpe.edu.br.workflowmanagement.service.entities.Papel;
import java.util.Optional;

@Repository
public interface PapelRepository extends JpaRepository<Papel, Long> {
    
    // Útil para buscar papéis pelo nome (ex: "ADMIN", "GERENTE") durante o cadastro
    Optional<Papel> findByNome(String nome);
}