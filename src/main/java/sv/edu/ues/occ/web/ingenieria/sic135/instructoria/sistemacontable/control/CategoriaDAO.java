package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Categoria;

import java.io.Serializable;

@Stateless
@LocalBean
public class CategoriaDAO extends DefaultDataAcces<Categoria, Object> implements Serializable {
    public CategoriaDAO() {
        super(Categoria.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return null;
    }

    @Override
    protected Class<Categoria> getEntityClass() {
        return null;
    }
}
