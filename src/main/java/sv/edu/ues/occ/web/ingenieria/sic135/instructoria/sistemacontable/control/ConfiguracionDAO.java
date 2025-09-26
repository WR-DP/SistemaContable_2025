package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Configuracion;

import java.io.Serializable;

@Stateless
@LocalBean
public class ConfiguracionDAO extends  DefaultDataAcces<Configuracion, Object> implements Serializable {
    public ConfiguracionDAO() {
        super(Configuracion.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return null;
    }

    @Override
    protected Class<Configuracion> getEntityClass() {
        return null;
    }
}
