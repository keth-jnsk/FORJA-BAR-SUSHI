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

    @Column(nullable = false, length = 20)
    private String perfil;

    public Usuario() {}

    public Usuario(String login, String senha, String perfil) {
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    public int getId()        { return id; }
    public String getLogin()  { return login; }
    public String getSenha()  { return senha; }
    public String getPerfil() { return perfil; }
}
