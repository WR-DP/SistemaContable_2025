package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.CuentaContable;

import java.io.Serializable;

@Stateless
@LocalBean
public class CuentaContableDAO extends DefaultDataAcces<CuentaContable, Object> implements Serializable {

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    public CuentaContableDAO() {
        super(CuentaContable.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<CuentaContable> getEntityClass() {
        return CuentaContable.class;
    }
}
