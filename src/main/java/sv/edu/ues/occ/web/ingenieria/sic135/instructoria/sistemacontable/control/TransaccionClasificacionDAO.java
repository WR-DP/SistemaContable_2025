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

    @Override
    public void edit(Transaccion transaccionSeleccionado) {

    }
    //private static final Logger LOGGER = Logger.getLogger(TransaccionDAO.class.getName());

}
