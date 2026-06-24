package org.example.controller;

import org.example.model.Usuario;
import org.example.service.AuthService;
import org.example.util.ResultadoOperacao;

import java.util.Optional;

/**
 * Recebe as ações da tela de Login e coordena a chamada ao AuthService.
 * A View nunca chama o AuthService diretamente.
 */
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    public ResultadoLogin autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            return ResultadoLogin.erro("Preencha o login e a senha.");
        }
        Optional<Usuario> usuario = authService.autenticar(login, senha);
        if (usuario.isEmpty()) {
            return ResultadoLogin.erro("Login ou senha inválidos.");
        }
        return ResultadoLogin.sucesso(usuario.get());
    }

    /** Conclui a troca obrigatória de senha (novo usuário ou senha redefinida pelo admin). */
    public ResultadoOperacao confirmarNovaSenha(int idUsuario, String novaSenha, String confirmacaoSenha) {
        if (novaSenha == null || novaSenha.length() < 4) {
            return ResultadoOperacao.erro("A nova senha deve ter ao menos 4 caracteres.");
        }
        if (!novaSenha.equals(confirmacaoSenha)) {
            return ResultadoOperacao.erro("As senhas digitadas não coincidem.");
        }
        try {
            authService.confirmarTrocaDeSenha(idUsuario, novaSenha);
            return ResultadoOperacao.sucesso("Senha definida com sucesso!");
        } catch (RuntimeException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }

    /** Resultado específico de login: ou devolve o usuário autenticado, ou uma mensagem de erro. */
    public static final class ResultadoLogin {
        private final boolean sucesso;
        private final String mensagemErro;
        private final Usuario usuario;

        private ResultadoLogin(boolean sucesso, String mensagemErro, Usuario usuario) {
            this.sucesso = sucesso;
            this.mensagemErro = mensagemErro;
            this.usuario = usuario;
        }

        static ResultadoLogin sucesso(Usuario usuario) {
            return new ResultadoLogin(true, null, usuario);
        }

        static ResultadoLogin erro(String mensagem) {
            return new ResultadoLogin(false, mensagem, null);
        }

        public boolean isSucesso() { return sucesso; }
        public String getMensagemErro() { return mensagemErro; }
        public Usuario getUsuario() { return usuario; }
    }
}
