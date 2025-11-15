package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Configuracion;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;

@Stateless
@LocalBean
public class ConfiguracionDAO extends  DefaultDataAcces<Configuracion, Object> implements Serializable {

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    public ConfiguracionDAO() {
        super(Configuracion.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Configuracion> getEntityClass() {
        return Configuracion.class;
    }


}
