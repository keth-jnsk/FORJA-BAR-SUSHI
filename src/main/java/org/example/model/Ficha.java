package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
public class Ficha {

    public enum StatusFicha {
        ABERTA, FECHADA
    }

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(nullable = false, name = "pedido_id")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFicha status;

    @Column(nullable = false, name = "data_abertura")
    private LocalDateTime dataAbertura;

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    @Column(name = "valor_total_fechamento")
    private double valorTotalFechamento;

    protected Ficha() {
        // exigido pelo JPA
    }

    public Ficha(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("A ficha precisa estar associada a um pedido.");
        }
        this.pedido = pedido;
        this.status = StatusFicha.ABERTA;
        this.dataAbertura = LocalDateTime.now();
    }

    /**
     * Fecha a ficha: calcula o valor total (já com desconto se aplicável),
     * altera o status para FECHADA e registra a data/hora do fechamento.
     * Lança exceção se a ficha já estiver fechada, evitando fechamento duplicado.
     */
    public void fechar() {
        if (status == StatusFicha.FECHADA) {
            throw new IllegalStateException("Esta ficha já está fechada.");
        }
        this.valorTotalFechamento = pedido.calcularTotalComDesconto();
        this.status = StatusFicha.FECHADA;
        this.dataFechamento = LocalDateTime.now();
    }

    public boolean isAberta() {
        return status == StatusFicha.ABERTA;
    }

    public UUID getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public StatusFicha getStatus() { return status; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public double getValorTotalFechamento() { return valorTotalFechamento; }

    @Override
    public String toString() {
        return String.format("Ficha [%s] | Pedido #%d | Status: %s",
                id.toString().substring(0, 8), pedido.getId(), status);
    }
}
