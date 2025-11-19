package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Collections;
import java.time.LocalDate;


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


    //findByTransaccionId
    public TransaccionClasificacion findByTransaccionId(UUID transaccionId) {
        if (transaccionId == null) return null;
        try {
            List<TransaccionClasificacion> list = em.createQuery(
                            "SELECT tc FROM TransaccionClasificacion tc WHERE tc.transaccion.id = :tid",
                            TransaccionClasificacion.class)
                    .setParameter("tid", transaccionId)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.getFirst();
        } catch (Exception e) {
            return null;
        }
    }

}
