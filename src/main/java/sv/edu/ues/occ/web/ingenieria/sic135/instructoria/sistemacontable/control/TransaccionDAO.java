package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Stateless
@LocalBean
public class TransaccionDAO extends DefaultDataAcces<Transaccion, Object> implements Serializable {

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    public TransaccionDAO() {
        super(Transaccion.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Transaccion> getEntityClass() {
        return Transaccion.class;
    }

    @Override
    public Transaccion findById(Object id) {
        return super.findById(id);
    }

    @Override
    public int count() throws IllegalStateException {
        return super.count();
    }

    //completar el metodo<---------------------------------------------------------------------------
    public void edit(Transaccion transaccionSeleccionado) {
        em.merge(transaccionSeleccionado);
    }


    // Metodos para el sistema de clasificaxión

    /**
     * Obtiene la lista de transacciones que aún no tienen una CuentaContable asignada.
     * Esto las marca como 'pendientes' de clasificación.
     * Se asume que la entidad Transacción tiene el campo 'cuentaContable'.
     * */
    public List<Transaccion> findTransaccionesPendinetes(){
        return  em.createQuery("SELECT t FROM  Transaccion t WHERE t.cuentaContable IS NULL ORDER BY  t.fecha",Transaccion.class)
                .getResultList();
    }


    /**
     * Busca transacciones pendientes cuya descripción coincide parcialmente con un filtro.
     * @param filtroDescripcion EL texto  a buscar en la descripcion
     * @return Lista de transacciones pedientes que coinciden
     */
    public List<Transaccion> finndByDescripcion(String filtroDescripcion){
        String patron = "%"+filtroDescripcion.toLowerCase()+"%";
        return em.createQuery("SELECT t FROM Transaccion t WHERE LOWER(t.descripcion) LIKE :patron AND t.cuentaContable IS NULL ORDER BY t.fecha", Transaccion.class)
                .setParameter("patron", patron)
                .getResultList();
    }

    public List<Transaccion> findByArchivoId(Object archivoId) {
        return em.createQuery("""
        SELECT t FROM Transaccion t
        WHERE t.archivoCargado.id = :archivoId
        ORDER BY t.fecha DESC
    """, Transaccion.class)
                .setParameter("archivoId", archivoId)
                .getResultList();
    }

    public List<Transaccion> findByArchivoIdAndDateRange(Object archivoId, LocalDate desde, LocalDate hasta) {
        return em.createQuery("""
        SELECT t FROM Transaccion t
        WHERE t.archivoCargado.id = :archivoId
          AND t.fecha BETWEEN :desde AND :hasta
        ORDER BY t.fecha ASC
    """, Transaccion.class)
                .setParameter("archivoId", archivoId)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }


}
