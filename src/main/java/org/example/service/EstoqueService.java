package org.example.service;

import org.example.model.EstoqueInsuficienteException;
import org.example.model.Produto;
import org.example.model.TipoProduto;
import org.example.repository.ProdutoRepo;
import org.example.util.ResultadoOperacao;

import java.util.List;

public class EstoqueService {

    private final ProdutoRepo produtoRepo;

    public EstoqueService(ProdutoRepo produtoRepo) {
        this.produtoRepo = produtoRepo;
    }

    public ResultadoOperacao cadastrarProduto(String nome, double preco, int quantidade, TipoProduto tipo) {
        if (nome == null || nome.isBlank()) {
            return ResultadoOperacao.erro("O nome do produto é obrigatório.");
        }
        if (preco <= 0) {
            return ResultadoOperacao.erro("O preço deve ser maior que zero.");
        }
        if (quantidade < 0) {
            return ResultadoOperacao.erro("A quantidade não pode ser negativa.");
        }
        if (tipo == null) {
            return ResultadoOperacao.erro("Selecione o tipo do produto.");
        }
        if (produtoRepo.buscarPorNome(nome) != null) {
            return ResultadoOperacao.erro("Já existe um produto cadastrado com este nome.");
        }

        try {
            Produto produto = new Produto(nome, preco, quantidade, tipo);
            produtoRepo.salvar(produto);

            if (produto.estoqueEstaBaixo()) {
                return ResultadoOperacao.sucesso(
                        "Produto '" + nome + "' cadastrado, porém já com estoque baixo (" + quantidade + " unidades).");
            }
            return ResultadoOperacao.sucesso("Produto '" + nome + "' cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }

    public ResultadoOperacao removerProduto(int id) {
        Produto produto = produtoRepo.buscarPorId(id);
        if (produto == null) {
            return ResultadoOperacao.erro("Produto não encontrado.");
        }
        boolean removido = produtoRepo.deletar(id);
        return removido
                ? ResultadoOperacao.sucesso("Produto '" + produto.getNome() + "' removido com sucesso.")
                : ResultadoOperacao.erro("Não foi possível remover o produto.");
    }

    public ResultadoOperacao atualizarNome(int id, String novoNome) {
        Produto produto = produtoRepo.buscarPorId(id);
        if (produto == null) {
            return ResultadoOperacao.erro("Produto não encontrado.");
        }
        if (novoNome == null || novoNome.isBlank()) {
            return ResultadoOperacao.erro("O nome do produto é obrigatório.");
        }
        Produto existente = produtoRepo.buscarPorNome(novoNome);
        if (existente != null && existente.getId() != id) {
            return ResultadoOperacao.erro("Já existe outro produto com este nome.");
        }
        try {
            produto.atualizarNome(novoNome);
            produtoRepo.atualizar(produto);
            return ResultadoOperacao.sucesso("Nome atualizado para '" + novoNome + "'.");
        } catch (IllegalArgumentException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }

    public ResultadoOperacao atualizarPreco(int id, double novoPreco) {
        Produto produto = produtoRepo.buscarPorId(id);
        if (produto == null) {
            return ResultadoOperacao.erro("Produto não encontrado.");
        }
        try {
            produto.atualizarPreco(novoPreco);
            produtoRepo.atualizar(produto);
            return ResultadoOperacao.sucesso("Preço de '" + produto.getNome() + "' atualizado.");
        } catch (IllegalArgumentException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }

    public ResultadoOperacao atualizarEstoque(int id, int novaQuantidade) {
        Produto produto = produtoRepo.buscarPorId(id);
        if (produto == null) {
            return ResultadoOperacao.erro("Produto não encontrado.");
        }
        if (novaQuantidade < 0) {
            return ResultadoOperacao.erro("A quantidade não pode ser negativa.");
        }
        int diferenca = novaQuantidade - produto.getQuantidade();
        if (diferenca > 0) {
            produto.estornarEstoque(diferenca);
        } else if (diferenca < 0) {
            produto.baixarEstoque(-diferenca);
        }
        produtoRepo.atualizar(produto);
        return ResultadoOperacao.sucesso(
                "Estoque de '" + produto.getNome() + "' atualizado para " + novaQuantidade + " unidades.");
    }

    /**
     * Confirma a baixa de estoque para um produto (ex.: ao confirmar pedido).
     * Lança EstoqueInsuficienteException se não houver unidades suficientes,
     * o que impede qualquer cenário de estoque negativo.
     */
    public void baixarEstoque(int idProduto, int quantidade) {
        Produto produto = produtoRepo.buscarPorId(idProduto);
        if (produto == null) {
            throw new IllegalStateException("Produto id " + idProduto + " não encontrado.");
        }
        produto.baixarEstoque(quantidade); // lança EstoqueInsuficienteException se necessário
        produtoRepo.atualizar(produto);
    }

    public void estornarEstoque(int idProduto, int quantidade) {
        Produto produto = produtoRepo.buscarPorId(idProduto);
        if (produto == null) {
            throw new IllegalStateException("Produto id " + idProduto + " não encontrado.");
        }
        produto.estornarEstoque(quantidade);
        produtoRepo.atualizar(produto);
    }

    public boolean possuiEstoqueSuficiente(int idProduto, int quantidadeDesejada) {
        Produto produto = produtoRepo.buscarPorId(idProduto);
        return produto != null && produto.possuiEstoqueSuficiente(quantidadeDesejada);
    }

    public List<Produto> listarTodos() {
        return produtoRepo.buscarTodos();
    }

    public List<Produto> listarPorTipo(TipoProduto tipo) {
        return produtoRepo.buscarPorTipo(tipo);
    }

    public List<Produto> listarComEstoqueDisponivel() {
        return produtoRepo.buscarTodos().stream()
                .filter(p -> p.getQuantidade() > 0)
                .toList();
    }

    public List<Produto> listarComEstoqueBaixo() {
        return produtoRepo.buscarTodos().stream()
                .filter(Produto::estoqueEstaBaixo)
                .toList();
    }

    public Produto buscarPorId(int id) {
        return produtoRepo.buscarPorId(id);
    }

    public Produto buscarPorNome(String nome) {
        return produtoRepo.buscarPorNome(nome);
    }
}
