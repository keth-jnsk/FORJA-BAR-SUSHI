package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false, length = 100)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

    @Column(nullable = false, name = "deve_redefinir_senha")
    private boolean deveRedefinirSenha;

    protected Usuario() {
        // exigido pelo JPA
    }

    public Usuario(String login, String senha, Perfil perfil) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("O login não pode ser vazio.");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        if (perfil == null) {
            throw new IllegalArgumentException("O perfil é obrigatório.");
        }
        this.login = login.trim();
        this.senha = senha;
        this.perfil = perfil;
        // Toda conta nova precisa trocar a senha provisória no primeiro login.
        this.deveRedefinirSenha = true;
    }

    public boolean senhaEstaCorreta(String senhaDigitada) {
        return this.senha.equals(senhaDigitada);
    }

    public boolean isAdmin() {
        return perfil == Perfil.ADMIN;
    }

    public boolean isDeveRedefinirSenha() {
        return deveRedefinirSenha;
    }

    public void atualizarLogin(String novoLogin) {
        if (novoLogin == null || novoLogin.isBlank()) {
            throw new IllegalArgumentException("O login não pode ser vazio.");
        }
        this.login = novoLogin.trim();
    }

    public void atualizarPerfil(Perfil novoPerfil) {
        if (novoPerfil == null) {
            throw new IllegalArgumentException("O perfil é obrigatório.");
        }
        this.perfil = novoPerfil;
    }

    /** Usado pelo administrador: define uma senha provisória e força a troca no próximo login. */
    public void redefinirSenhaPeloAdmin(String novaSenhaProvisoria) {
        if (novaSenhaProvisoria == null || novaSenhaProvisoria.isBlank()) {
            throw new IllegalArgumentException("A nova senha não pode ser vazia.");
        }
        this.senha = novaSenhaProvisoria;
        this.deveRedefinirSenha = true;
    }

    /** Usado pelo próprio usuário ao concluir a troca obrigatória de senha. */
    public void confirmarTrocaDeSenha(String novaSenha) {
        if (novaSenha == null || novaSenha.isBlank()) {
            throw new IllegalArgumentException("A nova senha não pode ser vazia.");
        }
        this.senha = novaSenha;
        this.deveRedefinirSenha = false;
    }

    public int getId() { return id; }
    public String getLogin() { return login; }
    public Perfil getPerfil() { return perfil; }

    // getSenha não é exposto publicamente: comparação deve ocorrer
    // via senhaEstaCorreta(), nunca lendo a senha diretamente de fora.
    String getSenha() { return senha; }
}
