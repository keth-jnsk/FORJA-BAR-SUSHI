package org.example.service;

import org.example.model.Usuario;
import org.example.model.Perfil;
import org.example.repository.UsuarioRepository;

import java.util.Optional;

public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Autentica o usuário. Retorna Optional vazio se login/senha inválidos
     * — quem decide a mensagem exibida é o Controller, não este Service.
     */
    public Optional<Usuario> autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            return Optional.empty();
        }
        Usuario usuario = usuarioRepository.buscarPorLogin(login);
        if (usuario == null || !usuario.senhaEstaCorreta(senha)) {
            return Optional.empty();
        }
        return Optional.of(usuario);
    }

    public boolean loginJaExiste(String login) {
        return usuarioRepository.buscarPorLogin(login) != null;
    }

    public void cadastrarUsuario(String login, String senha, Perfil perfil) {
        Usuario usuario = new Usuario(login, senha, perfil);
        usuarioRepository.salvar(usuario);
    }
}
