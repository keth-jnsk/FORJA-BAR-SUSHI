package org.example.service;

import org.example.model.Ficha;
import org.example.model.Pedido;
import org.example.repository.FichaRepository;
import org.example.util.ResultadoOperacao;

import java.util.List;
import java.util.UUID;

public class FichaService {

    private final FichaRepository fichaRepository;
    private final PedidoService pedidoService;

    public FichaService(FichaRepository fichaRepository, PedidoService pedidoService) {
        this.fichaRepository = fichaRepository;
        this.pedidoService = pedidoService;
    }

    public Ficha abrirFicha(int pedidoId) {
        Pedido pedido = pedidoService.buscarPorId(pedidoId);

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido #" + pedidoId + " não encontrado.");
        }
        if (pedido.getStatus() == Pedido.StatusPedido.CANCELADO
                || pedido.getStatus() == Pedido.StatusPedido.ENTREGUE) {
            throw new IllegalStateException(
                    "Não é possível abrir ficha para um pedido " + pedido.getStatus() + ".");
        }

        Ficha ficha = new Ficha(pedido);
        fichaRepository.salvar(ficha);
        return ficha;
    }

    /**
     * Fecha a ficha: calcula o total (a regra de cálculo e o impedimento de
     * fechamento duplicado ficam encapsulados na própria entidade Ficha),
     * e persiste o resultado.
     */
    public ResultadoOperacao fecharFicha(UUID fichaId) {
        Ficha ficha = fichaRepository.buscarPorId(fichaId);
        if (ficha == null) {
            return ResultadoOperacao.erro("Ficha não encontrada.");
        }

        try {
            ficha.fechar();
            fichaRepository.atualizar(ficha);
            return ResultadoOperacao.sucesso(
                    String.format("Ficha fechada! Total cobrado: R$ %.2f.", ficha.getValorTotalFechamento()));
        } catch (IllegalStateException e) {
            return ResultadoOperacao.erro(e.getMessage());
        }
    }

    public List<Ficha> listarAbertas() {
        return fichaRepository.listarAbertas();
    }

    public List<Ficha> listarTodas() {
        return fichaRepository.listarTodas();
    }

    public Ficha buscarPorId(UUID id) {
        return fichaRepository.buscarPorId(id);
    }
}
