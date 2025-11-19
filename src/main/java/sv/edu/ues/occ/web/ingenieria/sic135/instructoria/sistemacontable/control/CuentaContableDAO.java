package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.CuentaContable;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

@Stateless
@LocalBean
public class CuentaContableDAO extends DefaultDataAcces<CuentaContable, Object> implements Serializable {

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    private static final Logger LOGGER = Logger.getLogger(CuentaContableDAO.class.getName());

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

    /**
     * Buscar cuentas por nombre o código (para autocompletar) - VERSIÓN SIN TIPO
     */
    public List<CuentaContable> buscarPorNombreOCodigo(String criterio) {
        try {
            if (criterio == null || criterio.trim().isEmpty()) {
                return em.createQuery(
                                "SELECT c FROM CuentaContable c WHERE c.activa = TRUE ORDER BY c.codigo", CuentaContable.class)
                        .setMaxResults(20)
                        .getResultList();
            }

            String jpql = "SELECT c FROM CuentaContable c WHERE " +
                    "(UPPER(c.nombre) LIKE UPPER(:criterio) OR " +
                    "UPPER(c.codigo) LIKE UPPER(:criterio) OR " +
                    "UPPER(c.descripcion) LIKE UPPER(:criterio)) " +
                    "AND c.activa = TRUE " +
                    "ORDER BY c.codigo";

            return em.createQuery(jpql, CuentaContable.class)
                    .setParameter("criterio", "%" + criterio.trim() + "%")
                    .setMaxResults(20)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en buscarPorNombreOCodigo: " + e.getMessage(), e);
            return List.of();
        }
    }

    //Buscar cuenta por el codigo (usado por la IA)
    public CuentaContable findByCodigo(String codigo) {
        try {
            return em.createQuery("SELECT c FROM CuentaContable c WHERE c.codigo = :codigo AND c.activa = TRUE", CuentaContable.class)
                    .setParameter("codigo", codigo)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en findByCodigo: " + e.getMessage(), e);
            return null;
        }
    }

    //Obtener cuentas principales
    public List<CuentaContable> findCuentaPrincipales() {
        try {
            return em.createQuery("SELECT c FROM CuentaContable c WHERE c.cuentaPadre IS NULL AND c.activa = TRUE ORDER BY c.codigo", CuentaContable.class)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en findCuentaPrincipales: " + e.getMessage(), e);
            return List.of();
        }
    }
    /**
     * Buscar cuentas hijas de una cuenta padre
     */
    public List<CuentaContable> findSubCuentas(String codigoPadre) {
        try {
            return em.createQuery("SELECT c FROM CuentaContable c WHERE c.cuentaPadre.codigo = :codigoPadre AND c.activa = TRUE ORDER BY c.codigo", CuentaContable.class)
                    .setParameter("codigoPadre", codigoPadre)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en findSubCuentas: " + e.getMessage(), e);
            return List.of();
        }
    }
}