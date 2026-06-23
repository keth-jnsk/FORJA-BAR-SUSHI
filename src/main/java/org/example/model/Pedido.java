package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table
public class Pedido {

    public enum StatusPedido {
        ABERTO, EM_PREPARO, PRONTO, ENTREGUE, CANCELADO
    }

    public static final double VALOR_MINIMO_PARA_DESCONTO = 100.0;
    public static final double PERCENTUAL_DESCONTO = 0.10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name = "numero_mesa")
    private int numeroMesa;

    @Column(nullable = false, name = "data_criacao")
    private LocalDateTime dataCriacao;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    public Pedido() {
        this.status = StatusPedido.ABERTO;
        this.dataCriacao = LocalDateTime.now();
    }

    public Pedido(int numeroMesa) {
        this();
        this.numeroMesa = numeroMesa;
    }

    /**
     * Adiciona um item ao pedido. Só é permitido enquanto o pedido
     * estiver ABERTO — depois de confirmado/cancelado não é mais
     * possível alterar os itens.
     */
    public void adicionarItem(Produto produto, int quantidade) {
        if (status != StatusPedido.ABERTO) {
            throw new IllegalStateException(
                    "Não é possível adicionar itens a um pedido com status " + status + ".");
        }
        ItemPedido item = new ItemPedido(produto, quantidade);
        item.setPedido(this);
        itens.add(item);
    }

    public double calcularTotal() {
        return itens.stream()
                .mapToDouble(ItemPedido::calcularSubtotal)
                .sum();
    }

    /** Total já considerando o desconto automático de 10% acima de R$ 100. */
    public double calcularTotalComDesconto() {
        double total = calcularTotal();
        return possuiDesconto() ? total * (1 - PERCENTUAL_DESCONTO) : total;
    }

    public boolean possuiDesconto() {
        return calcularTotal() > VALOR_MINIMO_PARA_DESCONTO;
    }

    public void avancarStatus() {
        switch (status) {
            case ABERTO -> status = StatusPedido.EM_PREPARO;
            case EM_PREPARO -> status = StatusPedido.PRONTO;
            case PRONTO -> status = StatusPedido.ENTREGUE;
            default -> throw new IllegalStateException("Pedido já finalizado ou cancelado.");
        }
    }

    public void confirmar() {
        if (itens.isEmpty()) {
            throw new IllegalStateException("Não é possível confirmar um pedido sem itens.");
        }
        if (status != StatusPedido.ABERTO) {
            throw new IllegalStateException("Só é possível confirmar um pedido em status ABERTO.");
        }
        status = StatusPedido.EM_PREPARO;
    }

    public void cancelar() {
        if (status == StatusPedido.ENTREGUE) {
            throw new IllegalStateException("Não é possível cancelar um pedido já entregue.");
        }
        this.status = StatusPedido.CANCELADO;
    }

    public int getId() { return id; }
    public int getNumeroMesa() { return numeroMesa; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public List<ItemPedido> getItens() { return Collections.unmodifiableList(itens); }
    public StatusPedido getStatus() { return status; }
}
