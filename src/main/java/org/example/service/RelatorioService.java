package org.example.service;

import org.example.model.ItemPedido;
import org.example.model.Pedido;
import org.example.util.RelatorioDoDia;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioService {

    private final PedidoService pedidoService;

    public RelatorioService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Relatório do dia: antes este método somava TODOS os pedidos já
     * cadastrados no sistema (de qualquer data), pois a entidade Pedido não
     * tinha nenhum campo de data — por isso o relatório "do dia" mostrava
     * dados incorretos (ou nada, em bancos novos sem pedidos antigos).
     * Agora filtra de fato pelos pedidos confirmados/criados no dia atual.
     */
    public RelatorioDoDia gerarRelatorioDoDia() {
        LocalDate hoje = LocalDate.now();
        List<Pedido> pedidosDoDia = pedidoService.listarTodos().stream()
                .filter(p -> p.getDataCriacao() != null && p.getDataCriacao().toLocalDate().isEqual(hoje))
                .toList();
        return montarRelatorio(pedidosDoDia);
    }

    /** Relatório do mês: mesma lógica, mas considerando todo o mês/ano atual. */
    public RelatorioDoDia gerarRelatorioDoMes() {
        LocalDate hoje = LocalDate.now();
        List<Pedido> pedidosDoMes = pedidoService.listarTodos().stream()
                .filter(p -> p.getDataCriacao() != null
                        && p.getDataCriacao().getMonthValue() == hoje.getMonthValue()
                        && p.getDataCriacao().getYear() == hoje.getYear())
                .toList();
        return montarRelatorio(pedidosDoMes);
    }

    private RelatorioDoDia montarRelatorio(List<Pedido> pedidos) {
        double totalGeral = 0;
        int totalItensVendidos = 0;
        Map<String, Integer> contagemProdutos = new HashMap<>();

        for (Pedido pedido : pedidos) {
            totalGeral += pedido.calcularTotalComDesconto();
            for (ItemPedido item : pedido.getItens()) {
                totalItensVendidos += item.getQuantidadePedida();
                contagemProdutos.merge(item.getProduto().getNome(), item.getQuantidadePedida(), Integer::sum);
            }
        }

        String produtoMaisVendido = contagemProdutos.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return new RelatorioDoDia(pedidos, totalGeral, totalItensVendidos, produtoMaisVendido, contagemProdutos);
    }
}
