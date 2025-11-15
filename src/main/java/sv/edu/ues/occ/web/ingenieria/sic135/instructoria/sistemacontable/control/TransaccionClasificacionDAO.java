package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.io.Serializable;
import java.util.List;

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


    public List<TransaccionClasificacion> findTransaccionesPendientes(){
        return em.createQuery("SELECT t FROM TransaccionClasificacion t WHERE t.cuentaContableDebe.id  IS NULL ORDER BY t.createdAt", TransaccionClasificacion.class)
                .getResultList();
    }

//    public List<TransaccionClasificacion> findByDescripcion(String filtroDescripcion){
//        if(filtroDescripcion == null || filtroDescripcion.trim().isEmpty()){
//            return findTransaccionesPendientes();
//        }
//        String patron = "%" + filtroDescripcion.toLowerCase() + "%";               //cual seria la logica de este filtro?
//        return em.createQuery("SELECT t FROM TransaccionClasificacion t WHERE LOWER(t.cuentaContableDebe.descripcion) LIKE :patron AND t.cuentaContable IS NULL ORDER BY t.fecha", TransaccionClasificacion.class)
//                .setParameter("patron", patron)
//                .getResultList();
//    }

//    public List<TransaccionClasificacion> findTransaccionesClasificadas(){
//        return em.createQuery(
//                "SELECT t FROM TransaccionClasificacion t WHERE t.cuentaContable IS NOT NULL ORDER BY t.fecha DESC",
//                TransaccionClasificacion.class
//        ).getResultList();
//    }

    public long countPendientes(){
        return em.createQuery(
                "SELECT COUNT(t) FROM TransaccionClasificacion t WHERE t.cuentaContableDebe.id IS NULL",
                Long.class
        ).getSingleResult();
    }

    public long countClasificadas(){
        return em.createQuery(
                "SELECT COUNT(t) FROM TransaccionClasificacion t WHERE t.cuentaContableDebe.id IS NOT NULL",
                Long.class
        ).getSingleResult();
    }

}
