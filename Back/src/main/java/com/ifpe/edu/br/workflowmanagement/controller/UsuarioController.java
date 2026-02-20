package com.ifpe.edu.br.workflowmanagement.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ifpe.edu.br.workflowmanagement.service.DTO.CadastroUsuarioDTO;
import com.ifpe.edu.br.workflowmanagement.service.DTO.LoginDTO;
import com.ifpe.edu.br.workflowmanagement.service.DTO.UsuarioResponseDTO;
import com.ifpe.edu.br.workflowmanagement.service.UsuarioService;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid CadastroUsuarioDTO dto) {
        // O DTO já contém nome, sobrenome, cargo, etc.
        Usuario usuarioCriado = usuarioService.criarUsuario(dto);

        // Converte a Entidade para DTO de Resposta
        UsuarioResponseDTO response = new UsuarioResponseDTO(usuarioCriado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioService.login(dto.getEmail(), dto.getSenha());

        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(new UsuarioResponseDTO(usuarioOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        // Converte a lista de Entidades para lista de DTOs
        List<UsuarioResponseDTO> response = usuarios.stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirUsuario(@PathVariable Long id) {
        try {
            usuarioService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}