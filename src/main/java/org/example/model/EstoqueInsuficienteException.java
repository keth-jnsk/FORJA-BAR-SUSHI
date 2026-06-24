package org.example.model;

/**
 * Lançada quando uma operação tenta retirar do estoque mais unidades
 * do que as disponíveis. Usada para impedir estoque negativo em
 * qualquer ponto do sistema.
 */
public class EstoqueInsuficienteException extends RuntimeException {

    private final String nomeProduto;
    private final int quantidadeDisponivel;
    private final int quantidadeSolicitada;

    public EstoqueInsuficienteException(String nomeProduto, int quantidadeDisponivel, int quantidadeSolicitada) {
        super(String.format(
                "Estoque insuficiente para '%s'. Disponível: %d, solicitado: %d.",
                nomeProduto, quantidadeDisponivel, quantidadeSolicitada));
        this.nomeProduto = nomeProduto;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public String getNomeProduto() { return nomeProduto; }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public int getQuantidadeSolicitada() { return quantidadeSolicitada; }
}
