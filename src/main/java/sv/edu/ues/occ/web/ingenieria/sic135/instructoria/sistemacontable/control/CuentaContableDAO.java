package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.CuentaContable;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;
import java.util.List;

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



    //Buscar cuenta por el codigo (usado por la IA)
    public CuentaContable findByCodigo(String codigo) {
        try {
            return em.createQuery("SELECT c FROM CuentaContable c WHERE c.codigo = :codigo", CuentaContable.class)
                    .setParameter("codigo", codigo)
                    .getSingleResult();
        }catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    //Obtener cuentas principales
    public List<CuentaContable> findCuentaPrincipales() {
        return em.createQuery("SELECT c FROM CuentaContable c WHERE c.cuentaPadre IS NULL AND c.activa = TRUE ORDER BY c.codigo", CuentaContable.class)
                .getResultList();
    }
}
