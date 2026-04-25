package org.example.service;

import org.example.model.Produto;
import org.example.model.TipoProduto;
import org.example.repository.ProdutoRepo;
import java.util.List;


public class EstoqueService {

    private ProdutoRepo repo;

    public EstoqueService(ProdutoRepo repo) {
        this.repo = repo;
    }

    public void adicionarProduto(Produto p) {
        if (p.getNome() == null || p.getNome().isBlank()) {
            System.out.println("Erro: nome do produto não pode ser vazio.");
            return;
        }
        if (p.getPreco() <= 0) {
            System.out.println("Erro: preço deve ser maior que zero.");
            return;
        }
        if (p.getQuantidade() < 0) {
            System.out.println("Erro: quantidade não pode ser negativa.");
            return;
        }
        repo.salvar(p);
        System.out.println("Produto '" + p.getNome() + "' cadastrado com sucesso!");
    }

    public void removerProduto(int id) {
        if (repo.deletar(id)) {
            System.out.println("Produto desativado com sucesso!");
        } else {
            System.out.println("Produto id " + id + " não encontrado.");
        }
    }

    public void atualizarPreco(int id, double novoPreco) {
        Produto p = repo.buscarPorId(id);
        if (p == null) { System.out.println("Produto não encontrado."); return; }
        p.setPreco(novoPreco);
        repo.atualizar(p);
        System.out.println("Preço atualizado.");
    }

    public List<Produto> listarTodos() { return repo.buscarTodos(); }
    public List<Produto> listarPorTipo(TipoProduto tipo) { return repo.buscarPorTipo(tipo); }
    public Produto buscarPorId(int id)                         { return repo.buscarPorId(id); }

    public Produto buscarPorNome(String nome) {
        Produto p = repo.buscarPorNome(nome);
        if (p == null) System.out.println("Produto '" + nome + "' não encontrado.");
        return p;
    }

    public boolean temEstoque(Produto produto, int quantidade) {
        Produto atual = repo.buscarPorId(produto.getId());
        return atual != null && atual.getQuantidade() >= quantidade;
    }

    public boolean baixarEstoque(Produto produto, int quantidade) {
        return baixarEstoque(produto.getId(), quantidade);
    }

    public boolean baixarEstoque(int idProduto, int quantidade) {
        Produto p = repo.buscarPorId(idProduto);
        if (p == null) {
            System.out.println("Produto id " + idProduto + " não encontrado.");
            return false;
        }
        if (p.getQuantidade() < quantidade) {
            System.out.println("Estoque insuficiente para: " + p.getNome()
                    + " (disponível: " + p.getQuantidade() + ", solicitado: " + quantidade + ")");
            return false;
        }
        p.setQuantidade(p.getQuantidade() - quantidade);
        repo.atualizar(p);
        System.out.printf("Estoque baixado: %s | -%d | Restante: %d%n",
                p.getNome(), quantidade, p.getQuantidade());
        return true;
    }

    public boolean estornarEstoque(Produto produto, int quantidade) {
        Produto p = repo.buscarPorId(produto.getId());
        if (p == null) {
            System.out.println("Produto não encontrado para estorno: " + produto.getNome());
            return false;
        }
        p.setQuantidade(p.getQuantidade() + quantidade);
        repo.atualizar(p);
        System.out.printf("Estorno: %s | +%d | Novo total: %d%n",
                p.getNome(), quantidade, p.getQuantidade());
        return true;
    }

    public void alertarEstoqueBaixo(int minimo) {
        System.out.println("Estoque abaixo de " + minimo + " unidades:");
        boolean encontrou = false;
        for (Produto p : repo.buscarTodos()) {
            if (p.getQuantidade() < minimo) {
                System.out.printf("   • %-20s | Estoque: %d%n", p.getNome(), p.getQuantidade());
                encontrou = true;
            }
        }
        if (!encontrou) System.out.println("   Nenhum produto crítico.");
    }

    public void atualizarEstoque(int id, int novaQuantidade) {
        Produto p = repo.buscarPorId(id);
        if (p == null) {
            System.out.println("Produto não encontrado.");
            return;
        }
        p.setQuantidade(novaQuantidade);
        repo.atualizar(p);
        System.out.println("Estoque atualizado! " + p.getNome() + " agora tem " + novaQuantidade + " unidades.");
    }
}