package org.example.controller;

import org.example.model.Pedido;
import org.example.model.Produto;
import org.example.service.EstoqueService;
import org.example.service.FichaService;
import org.example.service.PedidoService;
import org.example.util.ResultadoOperacao;

import java.util.List;

public class PedidoController {

    private final PedidoService pedidoService;
    private final EstoqueService estoqueService;
    private final FichaService fichaService;

    private Pedido pedidoEmAndamento;

    public PedidoController(PedidoService pedidoService, EstoqueService estoqueService, FichaService fichaService) {
        this.pedidoService = pedidoService;
        this.estoqueService = estoqueService;
        this.fichaService = fichaService;
    }

    /** Inicia um novo pedido associado a uma mesa. Deve ser chamado antes de qualquer outra operação. */
    public void iniciarNovoPedido(int numeroMesa) {
        this.pedidoEmAndamento = pedidoService.criarPedido(numeroMesa);
    }

    public int getNumeroMesaAtual() {
        return pedidoEmAndamento.getNumeroMesa();
    }

    public List<Produto> listarCardapioDisponivel() {
        return estoqueService.listarComEstoqueDisponivel();
    }

    public ResultadoOperacao adicionarItem(int idProduto, int quantidade) {
        if (quantidade <= 0) {
            return ResultadoOperacao.erro("A quantidade deve ser maior que zero.");
        }
        Produto produto = estoqueService.buscarPorId(idProduto);
        if (produto == null) {
            return ResultadoOperacao.erro("Produto não encontrado.");
        }
        return pedidoService.adicionarItem(pedidoEmAndamento, produto, quantidade);
    }

    public double calcularTotalAtual() {
        return pedidoEmAndamento.calcularTotal();
    }

    public double calcularTotalComDescontoAtual() {
        return pedidoEmAndamento.calcularTotalComDesconto();
    }

    public boolean pedidoAtualTemDesconto() {
        return pedidoEmAndamento.possuiDesconto();
    }

    public Pedido getPedidoEmAndamento() {
        return pedidoEmAndamento;
    }

    /**
     * Confirma o pedido atual e, em caso de sucesso, já abre a ficha
     * correspondente — coordenação entre PedidoService e FichaService que
     * antes ficava espalhada/duplicada na View.
     */
    public ResultadoOperacao confirmarPedidoEAbrirFicha() {
        ResultadoOperacao resultadoConfirmacao = pedidoService.confirmarPedido(pedidoEmAndamento);
        if (!resultadoConfirmacao.isSucesso()) {
            return resultadoConfirmacao;
        }

        try {
            fichaService.abrirFicha(pedidoEmAndamento.getId());
            return ResultadoOperacao.sucesso(resultadoConfirmacao.getMensagem() + " Ficha aberta para a Mesa " + pedidoEmAndamento.getNumeroMesa() + ".");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResultadoOperacao.erro("Pedido confirmado, mas houve um erro ao abrir a ficha: " + e.getMessage());
        }
    }
}
