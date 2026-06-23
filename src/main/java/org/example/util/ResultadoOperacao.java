package org.example.util;

/**
 * Resultado padronizado de uma operação de serviço.
 * Em vez de imprimir mensagens no console (responsabilidade que não é do
 * Service) ou devolver apenas um boolean sem contexto, os Services devolvem
 * este objeto para que o Controller decida como exibir a mensagem na View.
 */
public final class ResultadoOperacao {

    private final boolean sucesso;
    private final String mensagem;

    private ResultadoOperacao(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    public static ResultadoOperacao sucesso(String mensagem) {
        return new ResultadoOperacao(true, mensagem);
    }

    public static ResultadoOperacao erro(String mensagem) {
        return new ResultadoOperacao(false, mensagem);
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }
}
