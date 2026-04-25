package org.example.service;

import org.example.model.ItemPedido;
import org.example.model.Pedido;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioService {

    private PedidoService pedidoService;

    public RelatorioService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public void gerarRelatorioDoDia() {
        List<Pedido> pedidos = pedidoService.listarTodos();

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido registrado.");
            return;
        }

        double totalGeral = 0;
        int totalItens = 0;
        Map<String, Integer> contagemProdutos = new HashMap<>();

        System.out.println("\n========== RELATÓRIO DO DIA ==========");

        for (Pedido p : pedidos) {
            double total = p.calcularTotal();
            totalGeral += total;
            System.out.printf("Pedido #%d | Status: %s | Total: R$ %.2f%n",
                    p.getId(), p.getStatus(), total);

            for (ItemPedido item : p.getItens()) {
                totalItens += item.getQuantidadePedida();
                String nome = item.getProduto().getNome();
                contagemProdutos.merge(nome, item.getQuantidadePedida(), Integer::sum);
            }
        }

        System.out.println("--------------------------------------");
        System.out.printf("Total de pedidos: %d%n", pedidos.size());
        System.out.printf("Total de itens vendidos: %d%n", totalItens);
        System.out.printf("Total geral: R$ %.2f%n", totalGeral);

        contagemProdutos.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e -> System.out.printf("Produto mais vendido: %s (%d unidades)%n",
                        e.getKey(), e.getValue()));

        System.out.println("======================================");
    }
}