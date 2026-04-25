package org.example.ui;

import org.example.model.*;
import org.example.repository.FichaRepository;
import org.example.service.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private EstoqueService estoqueService;
    private PedidoService pedidoService;
    private FichaService fichaService;
    private RelatorioService relatorioService;
    private Scanner scanner;

    public Menu(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
        this.pedidoService = new PedidoService(estoqueService);
        this.fichaService = new FichaService(new FichaRepository(), pedidoService);
        this.relatorioService = new RelatorioService(pedidoService);
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n=== SISTEMA BAR/RESTAURANTE ===");
            System.out.println("1. Cadastrar produto");
            System.out.println("2. Listar produtos");
            System.out.println("3. Buscar produto");
            System.out.println("4. Remover produto");
            System.out.println("5. Atualizar estoque");
            System.out.println("6. Atualizar preço");
            System.out.println("7. Alerta estoque baixo");
            System.out.println("\n--- PEDIDOS ---");
            System.out.println("8. Iniciar atendimento (Novo Pedido)");
            System.out.println("9. Ver fichas abertas");
            System.out.println("10. Fechar ficha");
            System.out.println("11. Relatório do dia");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");

            opcao = lerInt();

            switch (opcao) {
                case 1  -> cadastrarProduto();
                case 2  -> listarProdutos();
                case 3  -> buscarProduto();
                case 4  -> removerProduto();
                case 5  -> atualizarEstoque();
                case 6  -> atualizarPreco();
                case 7  -> alertarEstoqueBaixo();
                case 8  -> fluxoPedidoCompleto();
                case 9  -> verFichasAbertas();
                case 10 -> fecharFicha();
                case 11 -> relatorio();
                case 0  -> System.out.println("Encerrando...");
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void fluxoPedidoCompleto() {
        Pedido pedido = pedidoService.criarPedido();
        boolean continuar = true;
        while (continuar) {
            mostrarCardapio();
            System.out.print("\nDigite o ID do produto: ");
            int idProduto = lerInt();
            Produto produto = estoqueService.buscarPorId(idProduto);
            if (produto == null) { System.out.println("Produto não encontrado."); continue; }
            if (produto.getQuantidade() == 0) {
                System.out.println("Produto sem estoque: " + produto.getNome());
                continue;
            }
            System.out.print("Quantidade: ");
            int qtd = lerInt();
            boolean ok = pedidoService.adicionarItem(pedido, produto, qtd);
            if (!ok) continue;
            System.out.print("Deseja adicionar mais itens? (s/n): ");
            String resp = scanner.nextLine();
            continuar = resp.equalsIgnoreCase("s");
        }
        boolean confirmado = pedidoService.confirmarPedido(pedido);
        if (confirmado) {
            System.out.println("Pedido confirmado!");
            fichaService.abrirFicha(pedido.getId());
        }
    }

    private void mostrarCardapio() {
        List<Produto> produtos = estoqueService.listarTodos();
        System.out.println("\n========== CARDÁPIO ==========");
        System.out.printf("%-40s | %-40s%n", "COMIDAS", "BEBIDAS");
        System.out.println("--------------------------------------------------------------------------");
        ArrayList<Produto> comidas = new ArrayList<>();
        ArrayList<Produto> bebidas = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getQuantidade() == 0) continue; // bloqueio produto sem estoque
            if (p.getTipo() == TipoProduto.COMIDAS) comidas.add(p);
            else bebidas.add(p);
        }
        int max = Math.max(comidas.size(), bebidas.size());
        for (int i = 0; i < max; i++) {
            String comidaStr = "";
            String bebidaStr = "";
            if (i < comidas.size()) {
                Produto c = comidas.get(i);
                comidaStr = String.format("[%d] %s (R$ %.2f | %d)", c.getId(), c.getNome(), c.getPreco(), c.getQuantidade());
            }
            if (i < bebidas.size()) {
                Produto b = bebidas.get(i);
                bebidaStr = String.format("[%d] %s (R$ %.2f | %d)", b.getId(), b.getNome(), b.getPreco(), b.getQuantidade());
            }
            System.out.printf("%-40s | %-40s%n", comidaStr, bebidaStr);
        }
        System.out.println("--------------------------------------------------------------------------");
    }

    private void cadastrarProduto() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Preço: ");
        double preco = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Quantidade: ");
        int quantidade = scanner.nextInt();
        scanner.nextLine();
        int tipoOpcao = 0;
        while (tipoOpcao != 1 && tipoOpcao != 2) {
            System.out.println("Tipo: 1-BEBIDA  2-COMIDA");
            System.out.print("Tipo: ");
            tipoOpcao = lerInt();
            if (tipoOpcao != 1 && tipoOpcao != 2)
                System.out.println("Opção inválida, digite 1 ou 2.");
        }
        TipoProduto tipo = (tipoOpcao == 1) ? TipoProduto.BEBIDA : TipoProduto.COMIDAS;
        estoqueService.adicionarProduto(new Produto(nome, preco, quantidade, tipo));
    }

    private void listarProdutos() {
        List<Produto> lista = estoqueService.listarTodos();
        if (lista.isEmpty()) { System.out.println("Nenhum produto cadastrado."); return; }
        System.out.println("\n--- Produtos ---");
        for (Produto p : lista) System.out.println(p);
    }

    private void buscarProduto() {
        System.out.print("Buscar por (1-ID | 2-Nome): ");
        int opcao = lerInt();
        if (opcao == 1) {
            int id = lerInt("ID: ");
            Produto p = estoqueService.buscarPorId(id);
            if (p != null) System.out.println(p);
            else System.out.println("Produto não encontrado.");
        } else if (opcao == 2) {
            System.out.print("Nome: ");
            String nome = scanner.nextLine();
            Produto p = estoqueService.buscarPorNome(nome);
            if (p != null) System.out.println(p);
        } else {
            System.out.println("Opção inválida.");
        }
    }

    private void removerProduto() {
        listarProdutos();
        int id = lerInt("ID do produto a remover: ");
        Produto produto = estoqueService.buscarPorId(id);
        if (produto == null) { System.out.println("Produto não encontrado."); return; }
        System.out.println("Produto: " + produto);
        System.out.print("Tem certeza? (s/n): ");
        String confirmacao = scanner.nextLine();
        if (confirmacao.equalsIgnoreCase("s")) {
            estoqueService.removerProduto(id);
            System.out.println("Produto removido com sucesso!");
        } else {
            System.out.println("Remoção cancelada.");
        }
    }

    private void atualizarEstoque() {
        listarProdutos();
        int id = lerInt("ID do produto: ");
        Produto p = estoqueService.buscarPorId(id);
        if (p == null) { System.out.println("Produto não encontrado."); return; }
        System.out.println("Estoque atual de " + p.getNome() + ": " + p.getQuantidade());
        int novaQtd = lerInt("Nova quantidade: ");
        estoqueService.atualizarEstoque(id, novaQtd);
    }

    private void atualizarPreco() {
        listarProdutos();
        int id = lerInt("ID do produto: ");
        Produto p = estoqueService.buscarPorId(id);
        if (p == null) { System.out.println("Produto não encontrado."); return; }
        System.out.println("Preço atual de " + p.getNome() + ": R$ " + p.getPreco());
        System.out.print("Novo preço: R$ ");
        double novoPreco = scanner.nextDouble();
        scanner.nextLine();
        estoqueService.atualizarPreco(id, novoPreco);
    }

    private void alertarEstoqueBaixo() {
        int minimo = lerInt("Quantidade mínima para alerta: ");
        estoqueService.alertarEstoqueBaixo(minimo);
    }

    private void verFichasAbertas() {
        fichaService.exibirFichasAbertas();
    }

    private void fecharFicha() {
        List<Ficha> abertas = fichaService.listarAbertas();
        if (abertas.isEmpty()) {
            System.out.println("Nenhuma ficha aberta.");
            return;
        }
        System.out.println("\n=== FICHAS ABERTAS ===");
        for (Ficha f : abertas) System.out.println(f);
        System.out.print("\nDigite os primeiros 8 caracteres da ficha: ");
        String idParcial = scanner.nextLine();
        Ficha ficha = abertas.stream()
                .filter(f -> f.getId().toString().startsWith(idParcial))
                .findFirst()
                .orElse(null);
        if (ficha == null) { System.out.println("Ficha não encontrada."); return; }
        fichaService.fecharFicha(ficha.getId());
    }

    private void relatorio() {
        relatorioService.gerarRelatorioDoDia();
    }

    private int lerInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Digite um número: ");
            scanner.next();
        }
        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }

    private int lerInt(String mensagem) {
        System.out.print(mensagem);
        return lerInt();
    }
}