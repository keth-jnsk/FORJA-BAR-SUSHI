package org.example.util;

import org.example.model.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * DTO de saída do relatório do dia. Vive em `util` (e não em `service`)
 * justamente para que a View possa receber esse dado do Controller sem
 * precisar importar nada do pacote de Service — reforçando a regra de que
 * nenhuma View acessa Service diretamente, nem mesmo seus tipos.
 */
public final class RelatorioDoDia {

    private final List<Pedido> pedidos;
    private final double totalGeral;
    private final int totalItensVendidos;
    private final String produtoMaisVendido;

    public RelatorioDoDia(List<Pedido> pedidos, double totalGeral, int totalItensVendidos, String produtoMaisVendido) {
        this.pedidos = pedidos;
        this.totalGeral = totalGeral;
        this.totalItensVendidos = totalItensVendidos;
        this.produtoMaisVendido = produtoMaisVendido;
    }

    public List<Pedido> getPedidos() { return pedidos; }
    public double getTotalGeral() { return totalGeral; }
    public int getTotalItensVendidos() { return totalItensVendidos; }
    public Optional<String> getProdutoMaisVendido() { return Optional.ofNullable(produtoMaisVendido); }
}
