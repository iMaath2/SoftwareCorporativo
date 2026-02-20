package com.ifpe.edu.br.workflowmanagement.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ifpe.edu.br.workflowmanagement.service.DTO.CriarTarefaDTO;
import com.ifpe.edu.br.workflowmanagement.service.TarefaService;
import com.ifpe.edu.br.workflowmanagement.service.entities.Comentario;
import com.ifpe.edu.br.workflowmanagement.service.entities.RegistroHoras;
import com.ifpe.edu.br.workflowmanagement.service.entities.Tarefa;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    @Autowired
    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> buscarPorId(@PathVariable Long id) {
        return tarefaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- ATUALIZADO: Agora recebe Prioridade, Categoria, Horas Estimadas e Prazo ---
    @PostMapping
    public ResponseEntity<?> criarTarefa(@RequestBody @Valid CriarTarefaDTO dto) {
        try {
            Tarefa tarefa = tarefaService.criarTarefa(
                    dto.getTitulo(),
                    dto.getDescricao(),
                    dto.getProjetoId(),
                    dto.getResponsavelId(),
                    dto.getCriadorId(),
                    dto.getPrioridade(),
                    dto.getCategoria(),
                    dto.getHorasEstimadas(), // <--- Repassando Horas Estimadas
                    dto.getDataLimite()      // <--- Repassando Prazo (Data Limite)
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(tarefa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // -------------------------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<?> editarTarefa(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload,
            @RequestParam Long idExecutor) {
        try {
            String titulo = (String) payload.get("titulo");
            String descricao = (String) payload.get("descricao");

            // Tratamento da Data (String -> LocalDate)
            String dataStr = (String) payload.get("dataLimite");
            LocalDate dataLimite = null;
            if (dataStr != null && !dataStr.isBlank()) {
                dataLimite = LocalDate.parse(dataStr);
            }

            Tarefa t = tarefaService.editarTarefa(id, titulo, descricao, dataLimite, idExecutor);
            return ResponseEntity.ok(t);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{idTarefa}/mover/{idNovaEtapa}")
    public ResponseEntity<?> moverTarefa(
            @PathVariable Long idTarefa,
            @PathVariable Long idNovaEtapa,
            @RequestParam Long idExecutor) {
        try {
            Tarefa tarefa = tarefaService.moverTarefaParaEtapa(idTarefa, idNovaEtapa, idExecutor);
            return ResponseEntity.ok(tarefa);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/responsavel/{idNovoResponsavel}")
    public ResponseEntity<?> alterarResponsavel(
            @PathVariable Long id,
            @PathVariable Long idNovoResponsavel,
            @RequestParam Long idExecutor) {
        try {
            Tarefa t = tarefaService.definirResponsavel(id, idNovoResponsavel, idExecutor);
            return ResponseEntity.ok(t);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{idTarefa}/horas/total")
    public ResponseEntity<BigDecimal> consultarTotalHoras(@PathVariable Long idTarefa) {
        BigDecimal total = tarefaService.consultarHorasGastas(idTarefa);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/{idTarefa}/horas")
    public ResponseEntity<?> registrarHoras(
            @PathVariable Long idTarefa,
            @RequestBody Map<String, Object> payload) {
        try {
            BigDecimal horas = new BigDecimal(payload.get("horas").toString());
            LocalDate data = LocalDate.parse((String) payload.get("data"));
            Long idUsuario = Long.valueOf(payload.get("usuarioId").toString());

            RegistroHoras registro = tarefaService.registrarHoras(idTarefa, horas, data, idUsuario);
            return ResponseEntity.ok(registro);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{idTarefa}/comentarios")
    public ResponseEntity<?> comentar(
            @PathVariable Long idTarefa,
            @RequestBody Map<String, Object> payload) {
        try {
            String texto = (String) payload.get("texto");
            Long autorId = Long.valueOf(payload.get("autorId").toString());

            Comentario comentario = tarefaService.adicionarComentario(idTarefa, texto, autorId);
            return ResponseEntity.ok(comentario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirTarefa(
            @PathVariable Long id,
            @RequestParam Long idExecutor) {
        try {
            tarefaService.excluirTarefa(id, idExecutor);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}