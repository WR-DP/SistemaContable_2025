package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.io.Serializable;

@Stateless
@LocalBean
public class TransaccionClasificacionDAO extends DefaultDataAcces<TransaccionClasificacion, Object> implements Serializable {
    public TransaccionClasificacionDAO() {
        super(TransaccionClasificacion.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return null;
    }

    @Override
    protected Class<TransaccionClasificacion> getEntityClass() {
        return null;
    }
}
