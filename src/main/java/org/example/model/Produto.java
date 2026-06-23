package org.example.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private double preco;

    @Column(nullable = false)
    private int quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProduto tipo;

    @Column(nullable = false)
    private boolean ativo;

    @OneToMany(mappedBy = "produto")
    private List<ItemPedido> itens;

    public static final int ESTOQUE_MINIMO_ALERTA = 5;

    protected Produto() {
        // construtor exigido pelo JPA
    }

    public Produto(String nome, double preco, int quantidade, TipoProduto tipo) {
        validarNome(nome);
        validarPreco(preco);
        validarQuantidade(quantidade);
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do produto é obrigatório.");
        }
        this.nome = nome.trim();
        this.preco = preco;
        this.quantidade = quantidade;
        this.tipo = tipo;
        this.ativo = true;
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do produto não pode ser vazio.");
        }
    }

    private static void validarPreco(double preco) {
        if (preco <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
    }

    private static void validarQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }
    }

    /**
     * Reduz o estoque em `quantidade` unidades.
     * Lança exceção se a quantidade solicitada for maior do que o disponível,
     * impedindo estoque negativo.
     */
    public void baixarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a baixar deve ser maior que zero.");
        }
        if (quantidade > this.quantidade) {
            throw new EstoqueInsuficienteException(this.nome, this.quantidade, quantidade);
        }
        this.quantidade -= quantidade;
    }

    /** Devolve unidades ao estoque (ex.: cancelamento de pedido). */
    public void estornarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a estornar deve ser maior que zero.");
        }
        this.quantidade += quantidade;
    }

    public boolean possuiEstoqueSuficiente(int quantidadeDesejada) {
        return this.quantidade >= quantidadeDesejada;
    }

    public boolean estoqueEstaBaixo() {
        return this.quantidade <= ESTOQUE_MINIMO_ALERTA;
    }

    public void atualizarPreco(double novoPreco) {
        validarPreco(novoPreco);
        this.preco = novoPreco;
    }

    public void atualizarNome(String novoNome) {
        validarNome(novoNome);
        this.nome = novoNome.trim();
    }

    public void desativar() {
        this.ativo = false;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }
    public TipoProduto getTipo() { return tipo; }
    public boolean isAtivo() { return ativo; }

    @Override
    public String toString() {
        return String.format("[%d] %s | R$ %.2f | Estoque: %d | %s",
                id, nome, preco, quantidade, tipo);
    }
}
