package org.example.util;

/**
 * Exceções vindas do Hibernate/JDBC costumam vir "embrulhadas" — a mensagem
 * de mais alto nível (ex.: "Error while committing the transaction") raramente
 * diz o que realmente deu errado no banco. Esse utilitário desce a cadeia de
 * causas (getCause()) até a mais profunda, que normalmente traz a mensagem
 * de verdade (ex.: a constraint do banco que falhou).
 */
public final class ErroUtil {

    private ErroUtil() {
    }

    public static String causaRaiz(Throwable t) {
        if (t == null) {
            return "Erro desconhecido.";
        }
        Throwable atual = t;
        while (atual.getCause() != null && atual.getCause() != atual) {
            atual = atual.getCause();
        }
        String mensagem = atual.getMessage();
        if (mensagem == null || mensagem.isBlank()) {
            mensagem = atual.getClass().getSimpleName();
        }
        return mensagem;
    }
}
