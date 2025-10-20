package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Categoria;

import java.io.Serializable;

@Stateless
@LocalBean
public class CategoriaDAO extends DefaultDataAcces<Categoria, Object> implements Serializable {

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    public CategoriaDAO() {
        super(Categoria.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Categoria> getEntityClass() {
        return Categoria.class;
    }
    @Override
    public Categoria findById(Object id) {
        return super.findById(id);
    }
    @Override
    public int count() throws IllegalStateException {
        return super.count();
    }




}
