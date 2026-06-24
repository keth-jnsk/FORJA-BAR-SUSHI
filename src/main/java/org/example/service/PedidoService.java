package org.example.service;

import org.example.model.EstoqueInsuficienteException;
import org.example.model.ItemPedido;
import org.example.model.Pedido;
import org.example.model.Produto;
import org.example.repository.PedidoRepo;
import org.example.util.ResultadoOperacao;

import java.util.List;

public class PedidoService {

    private final PedidoRepo pedidoRepo;
    private final EstoqueService estoqueService;

    public PedidoService(PedidoRepo pedidoRepo, EstoqueService estoqueService) {
        this.pedidoRepo = pedidoRepo;
        this.estoqueService = estoqueService;
    }

    public Pedido criarPedido(int numeroMesa) {
        return new Pedido(numeroMesa);
    }

    public ResultadoOperacao adicionarItem(Pedido pedido, Produto produto, int quantidade) {
        if (quantidade <= 0) {
            return ResultadoOperacao.erro("A quantidade deve ser maior que zero.");
        }
        if (!produto.possuiEstoqueSuficiente(quantidade)) {
            return ResultadoOperacao.erro(
                    "Estoque insuficiente para '" + produto.getNome() + "'. Disponível: " + produto.getQuantidade() + ".");
        }
        try {
            pedido.adicionarItem(produto, quantidade);
            return ResultadoOperacao.sucesso("Item adicionado ao pedido.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }

    /**
     * Confirma o pedido: valida que não está vazio, confere estoque de todos
     * os itens, persiste o pedido e só então dá baixa no estoque de cada item.
     */
    public ResultadoOperacao confirmarPedido(Pedido pedido) {
        if (pedido.getItens().isEmpty()) {
            return ResultadoOperacao.erro("Não é possível confirmar um pedido sem itens.");
        }

        for (ItemPedido item : pedido.getItens()) {
            if (!estoqueService.possuiEstoqueSuficiente(item.getProduto().getId(), item.getQuantidadePedida())) {
                return ResultadoOperacao.erro(
                        "Estoque insuficiente para '" + item.getProduto().getNome() + "'.");
            }
        }

        try {
            pedido.confirmar();
            pedidoRepo.salvar(pedido);

            for (ItemPedido item : pedido.getItens()) {
                estoqueService.baixarEstoque(item.getProduto().getId(), item.getQuantidadePedida());
            }

            String mensagemDesconto = pedido.possuiDesconto()
                    ? String.format(" Desconto de 10%% aplicado — total final: R$ %.2f.", pedido.calcularTotalComDesconto())
                    : "";
            return ResultadoOperacao.sucesso("Pedido confirmado com sucesso!" + mensagemDesconto);

        } catch (EstoqueInsuficienteException e) {
            return ResultadoOperacao.erro(e.getMessage());
        } catch (IllegalStateException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }

    public Pedido buscarPorId(int id) {
        return pedidoRepo.buscarPorId(id);
    }

    public void atualizar(Pedido pedido) {
        pedidoRepo.atualizar(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepo.buscarTodos();
    }
}
