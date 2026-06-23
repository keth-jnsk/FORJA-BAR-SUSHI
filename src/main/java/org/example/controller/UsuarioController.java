package org.example.controller;

import org.example.model.Perfil;
import org.example.service.AuthService;
import org.example.util.ResultadoOperacao;

public class UsuarioController {

    private final AuthService authService;

    public UsuarioController(AuthService authService) {
        this.authService = authService;
    }

    public ResultadoOperacao cadastrarUsuario(String login, String senha, String perfilTexto) {
        if (login == null || login.isBlank()) {
            return ResultadoOperacao.erro("O login é obrigatório.");
        }
        if (senha == null || senha.isBlank()) {
            return ResultadoOperacao.erro("A senha é obrigatória.");
        }

        Perfil perfil;
        try {
            perfil = Perfil.valueOf(perfilTexto);
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResultadoOperacao.erro("Selecione um perfil válido.");
        }

        if (authService.loginJaExiste(login)) {
            return ResultadoOperacao.erro("Este login já está em uso.");
        }

        try {
            authService.cadastrarUsuario(login, senha, perfil);
            return ResultadoOperacao.sucesso("Usuário '" + login + "' cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }
}
