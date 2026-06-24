package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(nullable = false, name = "quantidade")
    private int quantidadePedida;

    @Column(nullable = false)
    private double subtotal;

    protected ItemPedido() {
        // exigido pelo JPA
    }

    public ItemPedido(Produto produto, int quantidadePedida) {
        if (produto == null) {
            throw new IllegalArgumentException("O produto do item não pode ser nulo.");
        }
        if (quantidadePedida <= 0) {
            throw new IllegalArgumentException("A quantidade pedida deve ser maior que zero.");
        }
        this.produto = produto;
        this.quantidadePedida = quantidadePedida;
        this.subtotal = produto.getPreco() * quantidadePedida;
    }

    void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Pedido getPedido() { return pedido; }
    public Produto getProduto() { return produto; }
    public int getQuantidadePedida() { return quantidadePedida; }

    /** Subtotal calculado e congelado no momento em que o item foi adicionado ao pedido. */
    public double calcularSubtotal() {
        return subtotal;
    }

    @Override
    public String toString() {
        return String.format("%dx %s — R$ %.2f",
                quantidadePedida, produto.getNome(), calcularSubtotal());
    }
}
