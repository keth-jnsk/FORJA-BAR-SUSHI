package org.example;

import org.example.database.FlywayConfig;
import org.example.model.Usuario;
import org.example.repository.ProdutoRepo;
import org.example.service.AuthService;
import org.example.service.EstoqueService;
import org.example.ui.Menu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        FlywayConfig.rodarMigrations();

        AuthService authService = new AuthService();
        Scanner scanner = new Scanner(System.in);
        Usuario usuario = null;

        int tentativas = 0;
        while (usuario == null && tentativas < 3) {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();
            usuario = authService.login(login, senha);
            if (usuario == null) {
                tentativas++;
                System.out.println("Tentativa " + tentativas + " de 3.");
            }
        }

        if (usuario == null) {
            System.out.println("Acesso bloqueado.");
            return;
        }

        ProdutoRepo produtoRepo = new ProdutoRepo();
        EstoqueService estoqueService = new EstoqueService(produtoRepo);
        Menu menu = new Menu(estoqueService);
        menu.iniciar();
    }
}