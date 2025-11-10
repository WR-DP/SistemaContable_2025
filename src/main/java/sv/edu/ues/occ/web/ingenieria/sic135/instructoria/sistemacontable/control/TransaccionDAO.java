package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
        return em.find(Transaccion.class, id);
    }

    @Override
    public int count() throws IllegalStateException {
        Long result = em.createQuery("SELECT COUNT(t) FROM Transaccion t", Long.class)
                .getSingleResult();
        return result != null ? result.intValue() : 0;
    }

    public List<Transaccion> findAll() {
        return em.createQuery("SELECT t FROM Transaccion t", Transaccion.class)
                .getResultList();
    }

    public void edit(Transaccion transaccionSeleccionado) {
        em.merge(transaccionSeleccionado);
    }

    public void create(Transaccion transaccion) {
        em.persist(transaccion);
    }

    public void delete(Transaccion transaccion) {
        if(!em.contains(transaccion)) {
            transaccion = em.merge(transaccion);
        }
        em.remove(transaccion);
    }

    public List<Transaccion> findRange(int[] range) {
        return em.createQuery("SELECT t FROM Transaccion t", Transaccion.class)
                .setFirstResult(range[0])
                .setMaxResults(range[1] - range[0])
                .getResultList();
    }

    // Métodos para el sistema de clasificación

    public List<Transaccion> findTransaccionesPendientes(){
        return em.createQuery("SELECT t FROM Transaccion t WHERE t.cuentaContable IS NULL ORDER BY t.fecha", Transaccion.class)
                .getResultList();
    }

    public List<Transaccion> findByDescripcion(String filtroDescripcion){
        if(filtroDescripcion == null || filtroDescripcion.trim().isEmpty()){
            return findTransaccionesPendientes();
        }
        String patron = "%" + filtroDescripcion.toLowerCase() + "%";
        return em.createQuery("SELECT t FROM Transaccion t WHERE LOWER(t.descripcion) LIKE :patron AND t.cuentaContable IS NULL ORDER BY t.fecha", Transaccion.class)
                .setParameter("patron", patron)
                .getResultList();
    }

    public List<Transaccion> findTransaccionesClasificadas(){
        return em.createQuery(
                "SELECT t FROM Transaccion t WHERE t.cuentaContable IS NOT NULL ORDER BY t.fecha DESC",
                Transaccion.class
        ).getResultList();
    }

    public long countPendientes(){
        return em.createQuery(
                "SELECT COUNT(t) FROM Transaccion t WHERE t.cuentaContable IS NULL",
                Long.class
        ).getSingleResult();
    }

    public long countClasificadas(){
        return em.createQuery(
                "SELECT COUNT(t) FROM Transaccion t WHERE t.cuentaContable IS NOT NULL",
                Long.class
        ).getSingleResult();
    }

    public boolean clasificarTransaccion(UUID transaccionId, UUID cuentaContableId){
        try{
            int updated = em.createQuery(
                            "UPDATE Transaccion t SET t.cuentaContable.id = :cuentaId WHERE t.id = :transaccionId"
                    ).setParameter("cuentaId", cuentaContableId)
                    .setParameter("transaccionId", transaccionId)
                    .executeUpdate();
            return updated > 0;
        } catch(Exception e){
            System.err.println("Error clasificando transacción: " + e.getMessage());
            return false;
        }
    }

    public List<Transaccion> findByArchivoCargado(UUID archivoId){
        return em.createQuery(
                        "SELECT t FROM Transaccion t WHERE t.archivoCargado.id = :archivoId ORDER BY t.filaExcel",
                        Transaccion.class
                ).setParameter("archivoId", archivoId)
                .getResultList();
    }

    public List<Transaccion> findPendientesPaginado(int firstResult, int maxResults){
        return em.createQuery(
                        "SELECT t FROM Transaccion t WHERE t.cuentaContable IS NULL ORDER BY t.fecha DESC",
                        Transaccion.class
                ).setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public List<Transaccion> findWithFilters(String descripcion, Boolean clasificadas, LocalDate fechaDesde, LocalDate fechaHasta) {
        StringBuilder jpql = new StringBuilder("SELECT t FROM Transaccion t WHERE 1=1");

        if (descripcion != null && !descripcion.trim().isEmpty()) {
            jpql.append(" AND LOWER(t.descripcion) LIKE LOWER(:descripcion)");
        }

        if (clasificadas != null) {
            if (clasificadas) {
                jpql.append(" AND t.cuentaContable IS NOT NULL");
            } else {
                jpql.append(" AND t.cuentaContable IS NULL");
            }
        }

        if (fechaDesde != null) {
            jpql.append(" AND t.fecha >= :fechaDesde");
        }

        if (fechaHasta != null) {
            jpql.append(" AND t.fecha <= :fechaHasta");
        }

        jpql.append(" ORDER BY t.fecha DESC, t.descripcion");

        TypedQuery<Transaccion> query = em.createQuery(jpql.toString(), Transaccion.class);

        if (descripcion != null && !descripcion.trim().isEmpty()) {
            query.setParameter("descripcion", "%" + descripcion + "%");
        }

        if (fechaDesde != null) {
            query.setParameter("fechaDesde", fechaDesde);
        }

        if (fechaHasta != null) {
            query.setParameter("fechaHasta", fechaHasta);
        }

        return query.getResultList();
    }

    public List<Transaccion> findPendientesByFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        return em.createQuery(
                        "SELECT t FROM Transaccion t WHERE t.cuentaContable IS NULL AND t.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY t.fecha DESC",
                        Transaccion.class
                ).setParameter("fechaInicio", fechaInicio)
                .setParameter("fechaFin", fechaFin)
                .getResultList();
    }

    public boolean desclasificarTransaccion(UUID transaccionId) {
        try {
            int updated = em.createQuery(
                            "UPDATE Transaccion t SET t.cuentaContable = NULL WHERE t.id = :transaccionId"
                    ).setParameter("transaccionId", transaccionId)
                    .executeUpdate();

            return updated > 0;
        } catch (Exception e) {
            System.err.println("Error desclasificando transacción: " + e.getMessage());
            return false;
        }
    }

    // MÉTODOS SIMPLIFICADOS PARA REPORTES (sin DTO problemáticos)

    /**
     * Obtiene años disponibles en las transacciones
     */
    public List<Integer> findAñosDisponibles() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<Transaccion> root = cq.from(Transaccion.class);

        // Extraer año de la fecha
        Expression<Integer> año = cb.function("YEAR", Integer.class, root.get("fecha"));

        cq.select(año)
                .distinct(true)
                .orderBy(cb.desc(año));

        return em.createQuery(cq).getResultList();
    }
    /**
     * Obtiene resumen básico por año (sin DTO)
     */
    public List<Object[]> findResumenBasicoPorAño(Integer año) {
        if (año == null) {
            throw new IllegalArgumentException("El año es obligatorio");
        }

        return em.createQuery(
                        "SELECT YEAR(t.fecha), COUNT(t), SUM(t.monto), COALESCE(t.moneda, 'USD') " +
                                "FROM Transaccion t " +
                                "WHERE YEAR(t.fecha) = :año " +
                                "GROUP BY YEAR(t.fecha), t.moneda " +
                                "ORDER BY t.moneda ASC",  // Ordenar por moneda en lugar de año
                        Object[].class
                ).setParameter("año", año)
                .getResultList();
    }
}