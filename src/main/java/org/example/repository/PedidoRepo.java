package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Pedido;
import org.example.util.JpaUtil;

import java.util.List;

public class PedidoRepo {

    public void salvar(Pedido pedido) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(pedido);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            rollbackSeAtivo(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public Pedido buscarPorId(int id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Pedido> resultado = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :id", Pedido.class
            ).setParameter("id", id).getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } finally {
            em.close();
        }
    }

    public List<Pedido> buscarTodos() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens ORDER BY p.id ASC", Pedido.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizar(Pedido pedido) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(pedido);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            rollbackSeAtivo(em);
            throw e;
        } finally {
            em.close();
        }
    }

    private void rollbackSeAtivo(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
