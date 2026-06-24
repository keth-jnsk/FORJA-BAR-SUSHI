package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Ficha;
import org.example.util.JpaUtil;

import java.util.List;

public class FichaRepository {

    public void salvar(Ficha ficha) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ficha);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            rollbackSeAtivo(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public Ficha buscarPorId(String id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Ficha> resultado = em.createQuery(
                    "SELECT DISTINCT f FROM Ficha f LEFT JOIN FETCH f.pedido p LEFT JOIN FETCH p.itens WHERE f.id = :id",
                    Ficha.class
            ).setParameter("id", id).getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } finally {
            em.close();
        }
    }

    public List<Ficha> listarTodas() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT f FROM Ficha f LEFT JOIN FETCH f.pedido p LEFT JOIN FETCH p.itens ORDER BY f.dataAbertura ASC",
                    Ficha.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Ficha> listarAbertas() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT f FROM Ficha f LEFT JOIN FETCH f.pedido p LEFT JOIN FETCH p.itens WHERE f.status = :status ORDER BY f.dataAbertura ASC",
                    Ficha.class
            ).setParameter("status", Ficha.StatusFicha.ABERTA).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Persiste o fechamento de uma ficha já alterada em memória
     * (status, data de fechamento e valor total já calculados pela entidade).
     */
    public void atualizar(Ficha ficha) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(ficha);
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
