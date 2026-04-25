package org.example.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table
public class Ficha {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn( nullable = false)
    private Pedido pedido;

    @Column(nullable = false)
    private boolean usada;

    public Ficha() {}

    public Ficha(Pedido pedido) {
        this.pedido = pedido;
        this.usada = false;
    }

    public Ficha(UUID id, Pedido pedido, boolean usada) {
        this.id = id;
        this.pedido = pedido;
        this.usada = usada;
    }

    public Ficha(int pedidoId) {
    }

    public void fechar() {
        if (this.usada) {
            System.out.println("Ficha já fechada.");
            return;
        }
        this.usada = true;
    }

    public boolean isAberta() {
        return !usada;
    }

    public UUID getId() { return id; }
    public Pedido getPedido() { return pedido; }
    public boolean isUsada() { return usada; }

    @Override
    public String toString() {
        return String.format("Ficha [%s] | Pedido #%d | Status: %s",
                id.toString().substring(0, 8),
                pedido.getId(),
                usada ? "FECHADA" : "ABERTA");
    }
}