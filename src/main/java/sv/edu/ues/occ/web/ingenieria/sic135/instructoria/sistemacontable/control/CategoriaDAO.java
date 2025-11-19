package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Categoria;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;
import java.util.List;

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

    //findByID
    public Categoria findByID(Long id) {
        return em.find(Categoria.class, id);
    }

    // Búsqueda por nombre (LIKE, case-insensitive)
    public List<Categoria> findByNombreLike(String q) {
        if (q == null) q = "";
        String pattern = "%" + q.toLowerCase() + "%";
        return em.createQuery("SELECT c FROM Categoria c WHERE LOWER(c.nombre) LIKE :pat ORDER BY c.nombre", Categoria.class)
                .setParameter("pat", pattern)
                .setMaxResults(50)
                .getResultList();
    }


}
