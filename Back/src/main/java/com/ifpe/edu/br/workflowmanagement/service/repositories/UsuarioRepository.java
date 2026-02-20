package com.ifpe.edu.br.workflowmanagement.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    
    // Novo método auxiliar para validações rápidas
    boolean existsByEmail(String email);
}