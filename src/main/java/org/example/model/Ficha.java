package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fichas")
public class Ficha {

    public enum StatusFicha {
        ABERTA, FECHADA
    }

    // A coluna no banco é VARCHAR(36) (não um tipo nativo "uuid" do Postgres).
    // Mapear esse campo como java.util.UUID faz o Hibernate, dependendo da
    // versão/driver, tentar tratar o valor lido como UUID nativo e falhar
    // com ClassCastException ("Cannot cast java.lang.String to java.util.UUID").
    // Por isso o id é tratado como String em todo o sistema — o valor
    // continua sendo um UUID por dentro, só que gerado e guardado como texto.
    @Id
    @Column(length = 36)
    private String id;

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
    private Double valorTotalFechamento; // null enquanto a ficha está aberta — só recebe valor ao fechar.

    protected Ficha() {
        // exigido pelo JPA
    }

    public Ficha(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("A ficha precisa estar associada a um pedido.");
        }
        this.id = UUID.randomUUID().toString();
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
        pedido.finalizarAtendimento();
    }

    public boolean isAberta() {
        return status == StatusFicha.ABERTA;
    }

    public String getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public StatusFicha getStatus() { return status; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public double getValorTotalFechamento() { return valorTotalFechamento == null ? 0.0 : valorTotalFechamento; }

    @Override
    public String toString() {
        return String.format("Ficha [%s] | Pedido #%d | Status: %s",
                id.substring(0, 8), pedido.getId(), status);
    }
}
