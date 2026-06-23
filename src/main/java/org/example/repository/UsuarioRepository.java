package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Usuario;
import org.example.util.JpaUtil;

import java.util.List;

public class UsuarioRepository {

    public Usuario buscarPorLogin(String login) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Usuario> resultado = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.login = :login",
                    Usuario.class
            ).setParameter("login", login).getResultList();
            return resultado.isEmpty() ? null : resultado.get(0);
        } finally {
            em.close();
        }
    }

    public void salvar(Usuario usuario) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
