package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Produto;
import org.example.model.TipoProduto;
import org.example.util.JpaUtil;

import java.util.List;

public class ProdutoRepo {

    public void salvar(Produto produto) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(produto);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            rollbackSeAtivo(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Produto> buscarTodos() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Produto p WHERE p.ativo = true ORDER BY p.id ASC", Produto.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public Produto buscarPorId(int id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Produto produto = em.find(Produto.class, id);
            return (produto != null && produto.isAtivo()) ? produto : null;
        } finally {
            em.close();
        }
    }

    public Produto buscarPorNome(String nome) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Produto> resultado = em.createQuery(
                    "SELECT p FROM Produto p WHERE LOWER(p.nome) = LOWER(:nome) AND p.ativo = true",
                    Produto.class
            ).setParameter("nome", nome).getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } finally {
            em.close();
        }
    }

    public List<Produto> buscarPorTipo(TipoProduto tipo) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Produto p WHERE p.tipo = :tipo AND p.ativo = true",
                    Produto.class
            ).setParameter("tipo", tipo).getResultList();
        } finally {
            em.close();
        }
    }

    public void atualizar(Produto produto) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(produto);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            rollbackSeAtivo(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean deletar(int id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Produto produto = em.find(Produto.class, id);
            if (produto == null) {
                return false;
            }
            em.getTransaction().begin();
            produto.desativar();
            em.merge(produto);
            em.getTransaction().commit();
            return true;
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
