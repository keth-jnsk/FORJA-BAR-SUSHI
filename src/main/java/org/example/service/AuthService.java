package org.example.service;

import org.example.model.Usuario;
import org.example.repository.UsuarioRepository;

public class AuthService {

    private UsuarioRepository repo;

    public AuthService() {
        this.repo = new UsuarioRepository();
    }

    public Usuario login(String login, String senha) {
        Usuario u = repo.buscarPorLogin(login);
        if (u == null) {
            System.out.println("Usuário não encontrado.");
            return null;
        }
        if (!u.getSenha().equals(senha)) {
            System.out.println("Senha incorreta.");
            return null;
        }
        System.out.println("Bem-vindo, " + u.getLogin() + "! Perfil: " + u.getPerfil());
        return u;
    }
}
