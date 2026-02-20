package com.ifpe.edu.br.workflowmanagement.service;

import com.ifpe.edu.br.workflowmanagement.service.DTO.CadastroUsuarioDTO;
import com.ifpe.edu.br.workflowmanagement.service.entities.Papel;
import com.ifpe.edu.br.workflowmanagement.service.entities.Usuario;
import com.ifpe.edu.br.workflowmanagement.service.repositories.PapelRepository;
import com.ifpe.edu.br.workflowmanagement.service.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PapelRepository papelRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.papelRepository = papelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario criarUsuario(CadastroUsuarioDTO dto) {
        // Validação de e-mail existente
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("O e-mail informado já está em uso.");
        }

        // Busca o Papel pelo ID que veio do DTO
        Papel papel = papelRepository.findById(dto.getPapelId())
                .orElseThrow(() -> new RuntimeException("Papel (Role) não encontrado."));

        String senhaCriptografada = passwordEncoder.encode(dto.getSenha());

        // Cria o usuário passando o SOBRENOME e o CARGO
        Usuario novoUsuario = new Usuario(
                dto.getNome(),
                dto.getSobrenome(), // <--- Campo Novo
                dto.getEmail(),
                senhaCriptografada,
                dto.getCargo(),
                papel
        );

        return usuarioRepository.save(novoUsuario);
    }

    public Optional<Usuario> login(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(senha, usuario.getSenha())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    // Métodos de leitura e exclusão
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) throw new RuntimeException("Usuário não encontrado.");
        usuarioRepository.deleteById(id);
    }
}