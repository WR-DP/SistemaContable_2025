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



    public List<Transaccion> findByArchivoCargado(UUID archivoId){
        return em.createQuery(
                        "SELECT t FROM Transaccion t WHERE t.archivoCargado.id = :archivoId ORDER BY t.filaExcel",
                        Transaccion.class
                ).setParameter("archivoId", archivoId)
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