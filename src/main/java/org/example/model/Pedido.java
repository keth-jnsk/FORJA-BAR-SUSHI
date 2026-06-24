package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    public enum StatusPedido {
        ABERTO,      // ainda sendo montado (em memória, antes de confirmar)
        CONFIRMADO,  // confirmado e com ficha aberta — preparo ainda NÃO iniciado
        EM_PREPARO,  // funcionário acionou o início do preparo
        PRONTO,      // preparo concluído, aguardando entrega/fechamento
        ENTREGUE,    // entregue / ficha fechada
        CANCELADO
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

    public void confirmar() {
        if (itens.isEmpty()) {
            throw new IllegalStateException("Não é possível confirmar um pedido sem itens.");
        }
        if (status != StatusPedido.ABERTO) {
            throw new IllegalStateException("Só é possível confirmar um pedido em status ABERTO.");
        }
        status = StatusPedido.CONFIRMADO;
    }

    /** Ação do funcionário: avisa que a cozinha começou a preparar o pedido. */
    public void iniciarPreparo() {
        if (status != StatusPedido.CONFIRMADO) {
            throw new IllegalStateException(
                    "Só é possível iniciar o preparo de um pedido confirmado (status atual: " + status + ").");
        }
        status = StatusPedido.EM_PREPARO;
    }

    /** Ação do funcionário: avisa que o pedido está pronto para entrega/retirada. */
    public void marcarComoPronto() {
        if (status != StatusPedido.EM_PREPARO) {
            throw new IllegalStateException(
                    "Só é possível marcar como pronto um pedido em preparo (status atual: " + status + ").");
        }
        status = StatusPedido.PRONTO;
    }

    /**
     * Encerra o atendimento (chamado ao fechar a ficha do pedido). Diferente
     * das transições acima, esta não exige um status específico anterior —
     * fechar a ficha é a confirmação final de que o atendimento terminou,
     * independente de o funcionário ter marcado cada etapa intermediária.
     */
    public void finalizarAtendimento() {
        if (status == StatusPedido.CANCELADO) {
            throw new IllegalStateException("Não é possível finalizar um pedido cancelado.");
        }
        status = StatusPedido.ENTREGUE;
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
