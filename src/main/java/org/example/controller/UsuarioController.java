package org.example.controller;

import org.example.model.Perfil;
import org.example.model.Usuario;
import org.example.service.AuthService;
import org.example.util.ResultadoOperacao;

import java.util.List;

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
            return ResultadoOperacao.sucesso("Usuário '" + login + "' cadastrado com sucesso! Ele(a) precisará trocar a senha no primeiro login.");
        } catch (IllegalArgumentException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }

    public List<Usuario> listarTodos() {
        return authService.listarTodos();
    }

    public ResultadoOperacao editarUsuario(Perfil perfilDoSolicitante, int id, String novoLogin, String perfilTexto) {
        if (perfilDoSolicitante != Perfil.ADMIN) {
            return ResultadoOperacao.erro("Apenas administradores podem editar usuários.");
        }
        if (novoLogin == null || novoLogin.isBlank()) {
            return ResultadoOperacao.erro("O login é obrigatório.");
        }
        Perfil perfil;
        try {
            perfil = Perfil.valueOf(perfilTexto);
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResultadoOperacao.erro("Selecione um perfil válido.");
        }
        try {
            authService.editarUsuario(id, novoLogin, perfil);
            return ResultadoOperacao.sucesso("Usuário atualizado com sucesso.");
        } catch (RuntimeException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }

    public ResultadoOperacao excluirUsuario(Perfil perfilDoSolicitante, int id) {
        if (perfilDoSolicitante != Perfil.ADMIN) {
            return ResultadoOperacao.erro("Apenas administradores podem excluir usuários.");
        }
        try {
            authService.excluirUsuario(id);
            return ResultadoOperacao.sucesso("Usuário excluído com sucesso.");
        } catch (RuntimeException e) {
            return ResultadoOperacao.erro("Não foi possível excluir: " + e.getMessage());
        }
    }

    public ResultadoOperacao redefinirSenha(Perfil perfilDoSolicitante, int id, String novaSenhaProvisoria) {
        if (perfilDoSolicitante != Perfil.ADMIN) {
            return ResultadoOperacao.erro("Apenas administradores podem redefinir senhas.");
        }
        if (novaSenhaProvisoria == null || novaSenhaProvisoria.isBlank()) {
            return ResultadoOperacao.erro("Digite a nova senha provisória.");
        }
        try {
            authService.redefinirSenha(id, novaSenhaProvisoria);
            return ResultadoOperacao.sucesso("Senha redefinida. O usuário precisará trocá-la no próximo login.");
        } catch (RuntimeException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }
}
