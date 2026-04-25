package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.example.model.Produto;
import org.example.model.TipoProduto;

import java.util.List;

public class ProdutoRepo {

    private EntityManager em;

    public ProdutoRepo() {
        em = Persistence.createEntityManagerFactory("meuPU").createEntityManager();
    }

    public void salvar(Produto p) {
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
        System.out.println("Produto '" + p.getNome() + "' salvo.");
    }

    public List<Produto> buscarTodos() {
        return em.createQuery(
                "SELECT p FROM Produto p WHERE p.ativo = true", Produto.class
        ).getResultList();
    }

    public Produto buscarPorId(int id) {
        Produto p = em.find(Produto.class, id);
        if (p != null && !p.isAtivo()) return null;
        return p;
    }

    public Produto buscarPorNome(String nome) {
        List<Produto> result = em.createQuery(
                "SELECT p FROM Produto p WHERE LOWER(p.nome) = LOWER(:nome) AND p.ativo = true",
                Produto.class
        ).setParameter("nome", nome).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Produto> buscarPorTipo(TipoProduto tipo) {
        return em.createQuery(
                "SELECT p FROM Produto p WHERE p.tipo = :tipo AND p.ativo = true",
                Produto.class
        ).setParameter("tipo", tipo).getResultList();
    }

    public boolean atualizar(Produto p) {
        em.getTransaction().begin();
        em.merge(p);
        em.getTransaction().commit();
        return true;
    }

    public boolean deletar(int id) {
        Produto p = em.find(Produto.class, id);
        if (p == null) return false;
        em.getTransaction().begin();
        p.setAtivo(false);
        em.merge(p);
        em.getTransaction().commit();
        return true;
    }
}