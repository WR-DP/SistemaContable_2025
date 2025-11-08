package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.io.Serializable;

@Stateless
@LocalBean
public class TransaccionClasificacionDAO extends DefaultDataAcces<TransaccionClasificacion, Object> implements Serializable {

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    public TransaccionClasificacionDAO() {
        super(TransaccionClasificacion.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<TransaccionClasificacion> getEntityClass() {
        return TransaccionClasificacion.class;
    }
}
