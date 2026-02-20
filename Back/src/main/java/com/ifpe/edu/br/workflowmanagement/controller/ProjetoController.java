package com.ifpe.edu.br.workflowmanagement.controller;

import com.ifpe.edu.br.workflowmanagement.service.DTO.CriarProjetoDTO;
import com.ifpe.edu.br.workflowmanagement.service.entities.Projeto;
import com.ifpe.edu.br.workflowmanagement.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    @Autowired
    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @PostMapping
    public ResponseEntity<Projeto> criarProjeto(@RequestBody @Valid CriarProjetoDTO dto) {
        try {
            Projeto projeto = projetoService.criarProjeto(
                    dto.getNome(),
                    dto.getDescricao(),
                    dto.getGerenteId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(projeto);
        } catch (SecurityException e) {
            // Retorna 403 Forbidden se o usuário não tiver permissão
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Projeto>> listarProjetos() {
        return ResponseEntity.ok(projetoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscarPorId(@PathVariable Long id) {
        try {
            Projeto projeto = projetoService.buscarPorId(id);
            return ResponseEntity.ok(projeto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para associar um fluxo ao projeto
    // Exemplo URL: POST /api/projetos/1/fluxo/5?idExecutor=2
    @PostMapping("/{idProjeto}/fluxo/{idFluxo}")
    public ResponseEntity<?> associarFluxo(
            @PathVariable Long idProjeto,
            @PathVariable Long idFluxo,
            @RequestParam Long idExecutor) { // Quem está fazendo a ação
        try {
            Projeto projetoAtualizado = projetoService.associarFluxoTrabalho(idProjeto, idFluxo, idExecutor);
            return ResponseEntity.ok(projetoAtualizado);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Endpoint para gerar relatório simples (String)
    @GetMapping("/{idProjeto}/relatorio")
    public ResponseEntity<String> gerarRelatorio(@PathVariable Long idProjeto, @RequestParam Long idExecutor) {
         try {
            String relatorio = projetoService.gerarRelatorio(idProjeto, idExecutor);
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirProjeto(@PathVariable Long id, @RequestParam Long idExecutor) {
        try {
            projetoService.excluirProjeto(id, idExecutor);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir projeto: " + e.getMessage());
        }
    }
}