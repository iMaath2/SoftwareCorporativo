package com.ifpe.edu.br.workflowmanagement.controller;

import com.ifpe.edu.br.workflowmanagement.service.entities.Etapa;
import com.ifpe.edu.br.workflowmanagement.service.entities.FluxoTrabalho;
import com.ifpe.edu.br.workflowmanagement.service.FluxoTrabalhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fluxos")
public class FluxoTrabalhoController {

    private final FluxoTrabalhoService fluxoService;

    @Autowired
    public FluxoTrabalhoController(FluxoTrabalhoService fluxoService) {
        this.fluxoService = fluxoService;
    }

    // 1. Criar um fluxo padrão (Kanban Simples)
    @PostMapping("/padrao")
    public ResponseEntity<FluxoTrabalho> criarFluxoPadrao() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fluxoService.criarFluxoPadraoKanban());
    }

    // 2. Criar um fluxo customizado (vazio)
    @PostMapping
    public ResponseEntity<FluxoTrabalho> criarFluxoCustomizado(@RequestBody Map<String, String> payload) {
        String nome = payload.get("nome");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fluxoService.criarFluxo(nome));
    }

    // 3. Adicionar etapa a um fluxo existente
    @PostMapping("/{idFluxo}/etapas")
    public ResponseEntity<Etapa> adicionarEtapa(
            @PathVariable Long idFluxo,
            @RequestBody Map<String, Object> payload) {
        
        String nome = (String) payload.get("nome");
        int ordem = (int) payload.get("ordem");
        
        Etapa etapa = fluxoService.adicionarEtapa(idFluxo, nome, ordem);
        return ResponseEntity.ok(etapa);
    }

    @GetMapping
    public ResponseEntity<List<FluxoTrabalho>> listarTodos() {
        return ResponseEntity.ok(fluxoService.listarTodos());
    }
}