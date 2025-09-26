package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;

import java.io.Serializable;

@Stateless
@LocalBean
public class ArchivoCargadoDAO extends DefaultDataAcces<ArchivoCargado, Object> implements Serializable {
    public ArchivoCargadoDAO() {
        super(ArchivoCargado.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return null;
    }

    @Override
    protected Class<ArchivoCargado> getEntityClass() {
        return null;
    }
}
