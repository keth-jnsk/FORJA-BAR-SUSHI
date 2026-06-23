package org.example.controller;

import org.example.model.Produto;
import org.example.model.TipoProduto;
import org.example.service.EstoqueService;
import org.example.util.ResultadoOperacao;

import java.util.List;

public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    public ResultadoOperacao cadastrarProduto(String nome, String precoTexto, String quantidadeTexto, String tipoTexto) {
        double preco;
        int quantidade;

        try {
            preco = Double.parseDouble(precoTexto.replace(",", "."));
        } catch (NumberFormatException | NullPointerException e) {
            return ResultadoOperacao.erro("Digite um preço válido.");
        }

        try {
            quantidade = Integer.parseInt(quantidadeTexto);
        } catch (NumberFormatException | NullPointerException e) {
            return ResultadoOperacao.erro("Digite uma quantidade válida.");
        }

        TipoProduto tipo;
        try {
            tipo = TipoProduto.valueOf(tipoTexto);
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResultadoOperacao.erro("Selecione um tipo de produto válido.");
        }

        return estoqueService.cadastrarProduto(nome, preco, quantidade, tipo);
    }

    public ResultadoOperacao removerProduto(int id) {
        return estoqueService.removerProduto(id);
    }

    public ResultadoOperacao atualizarNome(int id, String novoNome) {
        return estoqueService.atualizarNome(id, novoNome);
    }

    public ResultadoOperacao atualizarEstoque(int id, int novaQuantidade) {
        return estoqueService.atualizarEstoque(id, novaQuantidade);
    }

    public ResultadoOperacao atualizarPreco(int id, double novoPreco) {
        return estoqueService.atualizarPreco(id, novoPreco);
    }

    public List<Produto> listarTodos() {
        return estoqueService.listarTodos();
    }

    public List<Produto> listarComEstoqueDisponivel() {
        return estoqueService.listarComEstoqueDisponivel();
    }

    public List<Produto> listarComEstoqueBaixo() {
        return estoqueService.listarComEstoqueBaixo();
    }

    public Produto buscarPorId(int id) {
        return estoqueService.buscarPorId(id);
    }
}
