package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.example.model.Usuario;
import java.util.List;

public class UsuarioRepository {

    private EntityManager em;

    public UsuarioRepository() {
        em = Persistence.createEntityManagerFactory("meuPU").createEntityManager();
    }

    public Usuario buscarPorLogin(String login) {
        List<Usuario> result = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.login = :login",
                Usuario.class
        ).setParameter("login", login).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public void salvar(Usuario u) {
        em.getTransaction().begin();
        em.persist(u);
        em.getTransaction().commit();
    }
}
