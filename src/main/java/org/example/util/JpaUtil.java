package org.example.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Fábrica única de EntityManager para toda a aplicação.
 * Evita que cada repository crie sua própria EntityManagerFactory
 * (operação cara e que antes era repetida em cada classe de repositório).
 */
public final class JpaUtil {

    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("meuPU");

    private JpaUtil() {
    }

    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }

    public static void fechar() {
        if (FACTORY.isOpen()) {
            FACTORY.close();
        }
    }
}
