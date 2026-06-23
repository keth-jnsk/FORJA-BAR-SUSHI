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
    }

    public boolean senhaEstaCorreta(String senhaDigitada) {
        return this.senha.equals(senhaDigitada);
    }

    public boolean isAdmin() {
        return perfil == Perfil.ADMIN;
    }

    public int getId() { return id; }
    public String getLogin() { return login; }
    public Perfil getPerfil() { return perfil; }

    // getSenha não é exposto publicamente: comparação deve ocorrer
    // via senhaEstaCorreta(), nunca lendo a senha diretamente de fora.
    String getSenha() { return senha; }
}
