package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Collections;

@Stateless
@LocalBean
public class TransaccionClasificacionDAO extends DefaultDataAcces<TransaccionClasificacion, Object> implements Serializable {

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    private static final Logger LOGGER = Logger.getLogger(TransaccionClasificacionDAO.class.getName());

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

    /**
     * Buscar clasificaciones por transacción
     */
    public List<TransaccionClasificacion> findByTransaccion(Transaccion transaccion) {
        if (transaccion == null || transaccion.getId() == null) {
            return Collections.emptyList();
        }

        try {
            return em.createQuery(
                            "SELECT tc FROM TransaccionClasificacion tc " +
                                    "WHERE tc.transaccion.id = :transaccionId " +
                                    "ORDER BY tc.createdAt DESC", TransaccionClasificacion.class)
                    .setParameter("transaccionId", transaccion.getId())
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error buscando clasificaciones por transacción", e);
            return Collections.emptyList();
        }
    }
}