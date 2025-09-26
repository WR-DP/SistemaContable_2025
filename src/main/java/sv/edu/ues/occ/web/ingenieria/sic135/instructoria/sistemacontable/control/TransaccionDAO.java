package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;

@Stateless
@LocalBean
public class TransaccionDAO extends DefaultDataAcces<Transaccion, Object> implements Serializable {
    public TransaccionDAO() {
        super(Transaccion.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return null;
    }

    @Override
    protected Class<Transaccion> getEntityClass() {
        return null;
    }
}
