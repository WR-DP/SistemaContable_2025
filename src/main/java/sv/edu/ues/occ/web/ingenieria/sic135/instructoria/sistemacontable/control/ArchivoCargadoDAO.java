package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ArchivoCargadoDAO extends DefaultDataAcces<ArchivoCargado, Object> implements Serializable {
    @PersistenceContext(unitName = "SistemacontablePU")
    private EntityManager em;

    public ArchivoCargadoDAO() {
        super(ArchivoCargado.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<ArchivoCargado> getEntityClass() {
        return ArchivoCargado.class;
    }

    @Override
    public ArchivoCargado findById(Object id) {
        return super.findById(id);
    }

    @Override
    public int count() throws IllegalStateException {
        return super.count();
    }

    //private List<ArchivoCargado> listaArchivoCargado;

    private static final Logger log= Logger.getLogger(ArchivoCargadoDAO.class.getName());
}
