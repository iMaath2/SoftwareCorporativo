package com.ifpe.edu.br.workflowmanagement.service.repositories;

import com.ifpe.edu.br.workflowmanagement.service.entities.RegistroHoras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroHorasRepository extends JpaRepository<RegistroHoras, Long> {

    List<RegistroHoras> findByUsuarioIdAndDataRegistroBetween(Long usuarioId, LocalDate dataInicio, LocalDate dataFim);

    List<RegistroHoras> findByTarefaId(Long tarefaId);

    @Query("SELECT r FROM RegistroHoras r WHERE r.tarefa.projeto.id = :projetoId")
    List<RegistroHoras> findByProjetoId(@Param("projetoId") Long projetoId);

    // CORREÇÃO: Retorno alterado de Float para BigDecimal
    @Query("SELECT SUM(r.horas) FROM RegistroHoras r WHERE r.tarefa.id = :tarefaId")
    BigDecimal sumHorasByTarefaId(@Param("tarefaId") Long tarefaId);

    // CORREÇÃO: Retorno alterado de Float para BigDecimal
    @Query("SELECT SUM(r.horas) FROM RegistroHoras r WHERE r.tarefa.projeto.id = :projetoId")
    BigDecimal sumHorasByProjetoId(@Param("projetoId") Long projetoId);
}