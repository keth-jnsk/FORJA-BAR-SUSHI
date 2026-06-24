package org.example.util;

import org.example.model.Pedido;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DTO de saída do relatório do dia/mês. Vive em `util` (e não em `service`)
 * justamente para que a View possa receber esse dado do Controller sem
 * precisar importar nada do pacote de Service — reforçando a regra de que
 * nenhuma View acessa Service diretamente, nem mesmo seus tipos.
 */
public final class RelatorioDoDia {

    private final List<Pedido> pedidos;
    private final double totalGeral;
    private final int totalItensVendidos;
    private final String produtoMaisVendido;
    private final Map<String, Integer> quantidadeVendidaPorProduto;

    public RelatorioDoDia(List<Pedido> pedidos, double totalGeral, int totalItensVendidos,
                           String produtoMaisVendido, Map<String, Integer> quantidadeVendidaPorProduto) {
        this.pedidos = pedidos;
        this.totalGeral = totalGeral;
        this.totalItensVendidos = totalItensVendidos;
        this.produtoMaisVendido = produtoMaisVendido;
        this.quantidadeVendidaPorProduto = quantidadeVendidaPorProduto == null
                ? new LinkedHashMap<>() : quantidadeVendidaPorProduto;
    }

    public List<Pedido> getPedidos() { return pedidos; }
    public double getTotalGeral() { return totalGeral; }
    public int getTotalItensVendidos() { return totalItensVendidos; }
    public Optional<String> getProdutoMaisVendido() { return Optional.ofNullable(produtoMaisVendido); }

    /** Quantidade vendida de cada produto no período do relatório — usado para montar o gráfico. */
    public Map<String, Integer> getQuantidadeVendidaPorProduto() { return quantidadeVendidaPorProduto; }
}
