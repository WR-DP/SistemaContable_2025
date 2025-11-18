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
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class TransaccionDAO extends DefaultDataAcces<Transaccion, Object> implements Serializable {

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    public TransaccionDAO() {
        super(Transaccion.class);
    }

    private static final Logger LOGGER = Logger.getLogger(TransaccionDAO.class.getName());

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
        em.merge(transaccion);
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


    //================================ solucionar la logica de estas querys ================================
//    public List<TransaccionClasificacion> findTransaccionesPendientes(){
//        return em.createQuery("SELECT t FROM TransaccionClasificacion t WHERE t.cuentaContableDebe IS NULL ORDER BY t.createdAt", TransaccionClasificacion.class)
//                .getResultList();
//    }
    /**
     * Obtiene la lista de transacciones que aún no tienen una CuentaContable asignada.
     * Esto las marca como 'pendientes' de clasificación.
     * Se asume que la entidad Transacción tiene el campo 'cuentaContable'.
     * */
    public List<Transaccion> findTransaccionesPendientes(){
        return  em.createQuery("SELECT t FROM Transaccion t WHERE t.transaccionClasificacionCollection IS EMPTY ORDER BY t.fecha",Transaccion.class)
                .getResultList();
    }

    public List<Transaccion> findByDescripcion(String filtroDescripcion){
        String patron = "%"+filtroDescripcion.toLowerCase()+"%";
        return em.createQuery("SELECT t FROM Transaccion t WHERE LOWER(t.descripcion) LIKE :patron AND t.transaccionClasificacionCollection IS EMPTY ORDER BY t.fecha", Transaccion.class)
                .setParameter("patron", patron)
                .getResultList();
    }

    public List<TransaccionClasificacion> findTransaccionesClasificadas(){
        return em.createQuery(
                "SELECT t FROM TransaccionClasificacion t WHERE t.id IS NOT NULL ORDER BY t.createdAt DESC",
                TransaccionClasificacion.class
        ).getResultList();
    }

    public long countPendientes(){
        return em.createQuery(
                "SELECT COUNT(t) FROM TransaccionClasificacion t WHERE t.cuentaContableDebe IS NULL OR t.cuentaContableHaber IS NULL",
                Long.class
        ).getSingleResult();
    }

//    public long countClasificadas(){
//        return em.createQuery(
//                "SELECT COUNT(t) FROM Transaccion t WHERE t.cuentaContable IS NOT NULL",
//                Long.class
//        ).getSingleResult();
//    }

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
    public List<Transaccion> findByArchivoId(Object archivoId) {
        return em.createQuery("""
        SELECT t FROM Transaccion t
        WHERE t.archivoCargado.id = :archivoId
        ORDER BY t.fecha DESC
    """, Transaccion.class)
                .setParameter("archivoId", archivoId)
                .getResultList();
    }
    /**
     * Busca transacciones pendientes cuya descripción coincide parcialmente con un filtro.
     * @param filtroDescripcion EL texto  a buscar en la descripcion
     * @return Lista de transacciones pedientes que coinciden
     */
    public List<Transaccion> finndByDescripcion(String filtroDescripcion){
        String patron = "%"+filtroDescripcion.toLowerCase()+"%";
        return em.createQuery("SELECT t FROM Transaccion t WHERE LOWER(t.descripcion) LIKE :patron AND t.transaccionClasificacionCollection IS EMPTY ORDER BY t.fecha", Transaccion.class)
                .setParameter("patron", patron)
                .getResultList();
    }

    /**
     * Obtiene transacciones clasificadas según tipo de transacción (ej. "VENTA", "COMPRA")
     * dentro de un rango de fechas y opcionalmente dentro de un archivo.
     * Devuelve DISTINCT para evitar duplicados si hay múltiples clasificaciones.
     */
    public List<Transaccion> findForFacturacionByTipo(Object archivoId, String tipoTransaccion, LocalDate desde, LocalDate hasta) {
        if (tipoTransaccion == null || tipoTransaccion.isBlank()) {
            LOGGER.log(Level.WARNING, "Tipo de transacción para facturación nulo o vacío.");
            return Collections.emptyList();
        }
        try {
            String jpql = "SELECT DISTINCT t FROM Transaccion t JOIN t.transaccionClasificacionCollection tc " +
                    "WHERE LOWER(tc.tipoTransaccion) = :tipo " +
                    "AND t.fecha BETWEEN :desde AND :hasta ";
            if (archivoId != null) {
                jpql += "AND t.archivoCargadoId.id = :archivoId ";
            }
            jpql += "ORDER BY t.fecha ASC";

            var query = em.createQuery(jpql, Transaccion.class)
                    .setParameter("tipo", tipoTransaccion.toLowerCase())
                    .setParameter("desde", desde)
                    .setParameter("hasta", hasta);
            if (archivoId != null) {
                query.setParameter("archivoId", archivoId);
            }
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo transacciones para facturación", e);
            return Collections.emptyList();
        }
    }

    /**
     * Conveniencia: calcula el rango según periodo y delega a findForFacturacionByTipo
     */
    public List<Transaccion> findForFacturacion(Object archivoId, String tipoTransaccion, String periodo, Integer anio, Integer mes, Integer trimestre) {
        if (archivoId == null) {
            return Collections.emptyList();
        }
        if (periodo == null || periodo.isBlank()) {
            // si no se especifica periodo devolvemos todas las transacciones del tipo
            try {
                String jpql = "SELECT DISTINCT t FROM Transaccion t JOIN t.transaccionClasificacionCollection tc " +
                        "WHERE LOWER(tc.tipoTransaccion) = :tipo " +
                        "AND t.archivoCargado.id = :archivoId " +
                        "ORDER BY t.fecha ASC";
                return em.createQuery(jpql, Transaccion.class)
                        .setParameter("tipo", tipoTransaccion.toLowerCase())
                        .setParameter("archivoId", archivoId)
                        .getResultList();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error obteniendo transacciones para facturación (sin periodo)", e);
                return Collections.emptyList();
            }
        }

        try {
            String p = periodo.trim().toLowerCase();
            LocalDate desde;
            LocalDate hasta;
            switch (p) {
                case "mensual":
                    if (anio == null || mes == null) {
                        LOGGER.log(Level.WARNING, "Periodo mensual sin año o mes: devolviendo vacio.");
                        return Collections.emptyList();
                    }
                    if (mes < 1 || mes > 12) {
                        LOGGER.log(Level.WARNING, "Mes fuera de rango: " + mes + ". Devolviendo vacio.");
                        return Collections.emptyList();
                    }
                    desde = LocalDate.of(anio, mes, 1);
                    hasta = desde.withDayOfMonth(desde.lengthOfMonth());
                    break;
                case "trimestral":
                    if (anio == null || trimestre == null) {
                        LOGGER.log(Level.WARNING, "Periodo trimestral sin año o trimestre: devolviendo vacio.");
                        return Collections.emptyList();
                    }
                    if (trimestre < 1 || trimestre > 4) {
                        LOGGER.log(Level.WARNING, "Trimestre fuera de rango: " + trimestre + ". Devolviendo vacio.");
                        return Collections.emptyList();
                    }
                    int startMonth = 1 + (trimestre - 1) * 3;
                    desde = LocalDate.of(anio, startMonth, 1);
                    int endMonth = startMonth + 2;
                    LocalDate endMonthDate = LocalDate.of(anio, endMonth, 1);
                    hasta = endMonthDate.withDayOfMonth(endMonthDate.lengthOfMonth());
                    break;
                case "anual":
                    if (anio == null) {
                        LOGGER.log(Level.WARNING, "Periodo anual sin año: devolviendo vacio.");
                        return Collections.emptyList();
                    }
                    desde = LocalDate.of(anio, 1, 1);
                    hasta = LocalDate.of(anio, 12, 31);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Periodo desconocido: " + periodo + ". Devolviendo vacio.");
                    return Collections.emptyList();
            }

            return findForFacturacionByTipo(archivoId, tipoTransaccion, desde, hasta);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculando periodo para facturación", e);
            return Collections.emptyList();
        }
    }

    /**
     * Filtra transacciones por periodo contable (mensual, trimestral, anual).
     * Si los parámetros necesarios para el periodo no se reciben o son inválidos,
     * devuelve todas las transacciones del archivo (comportamiento por defecto).
     *
     * @param archivoId  id del ArchivoCargado
     * @param periodo    "mensual", "trimestral" o "anual" (case-insensitive)
     * @param anio       año (ej. 2025)
     * @param mes        mes (1-12) requerido para mensual
     * @param trimestre  trimestre (1-4) requerido para trimestral
     * @return lista de transacciones filtradas
     */
    public List<Transaccion> findByArchivoIdAndPeriodo(Object archivoId, String periodo, Integer anio, Integer mes, Integer trimestre) {
        if (archivoId == null) {
            return Collections.emptyList();
        }
        if (periodo == null || periodo.isBlank()) {
            return findByArchivoId(archivoId);
        }
        try {
            String p = periodo.trim().toLowerCase();
            LocalDate desde;
            LocalDate hasta;
            switch (p) {
                case "mensual":
                    if (anio == null || mes == null) {
                        LOGGER.log(Level.WARNING, "Periodo mensual sin año o mes: devolviendo todas las transacciones.");
                        return findByArchivoId(archivoId);
                    }
                    if (mes < 1 || mes > 12) {
                        LOGGER.log(Level.WARNING, "Mes fuera de rango: " + mes + ". Devolviendo todas las transacciones.");
                        return findByArchivoId(archivoId);
                    }
                    desde = LocalDate.of(anio, mes, 1);
                    hasta = desde.withDayOfMonth(desde.lengthOfMonth());
                    break;
                case "trimestral":
                    if (anio == null || trimestre == null) {
                        LOGGER.log(Level.WARNING, "Periodo trimestral sin año o trimestre: devolviendo todas las transacciones.");
                        return findByArchivoId(archivoId);
                    }
                    if (trimestre < 1 || trimestre > 4) {
                        LOGGER.log(Level.WARNING, "Trimestre fuera de rango: " + trimestre + ". Devolviendo todas las transacciones.");
                        return findByArchivoId(archivoId);
                    }
                    int startMonth = 1 + (trimestre - 1) * 3;
                    desde = LocalDate.of(anio, startMonth, 1);
                    int endMonth = startMonth + 2;
                    LocalDate endMonthDate = LocalDate.of(anio, endMonth, 1);
                    hasta = endMonthDate.withDayOfMonth(endMonthDate.lengthOfMonth());
                    break;
                case "anual":
                    if (anio == null) {
                        LOGGER.log(Level.WARNING, "Periodo anual sin año: devolviendo todas las transacciones.");
                        return findByArchivoId(archivoId);
                    }
                    desde = LocalDate.of(anio, 1, 1);
                    hasta = LocalDate.of(anio, 12, 31);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Periodo desconocido: " + periodo + ". Devolviendo todas las transacciones.");
                    return findByArchivoId(archivoId);
            }

            return findByArchivoIdAndDateRange(archivoId, desde, hasta);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error filtrando transacciones por periodo", e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtiene transacciones por tipo (ej. "VENTA", "COMPRA") sin filtrar por archivo o periodo.
     * Útil para la vista donde solo queremos filtrar por tipo.
     */
    public List<Transaccion> findForFacturacionByTipoAll(String tipoTransaccion) {
        if (tipoTransaccion == null || tipoTransaccion.isBlank()) {
            LOGGER.log(Level.WARNING, "Tipo de transacción para facturación nulo o vacío.");
            return Collections.emptyList();
        }
        try {
            String jpql = "SELECT DISTINCT t FROM Transaccion t JOIN t.transaccionClasificacionCollection tc " +
                    "WHERE LOWER(tc.tipoTransaccion) = :tipo ORDER BY t.fecha ASC";
            return em.createQuery(jpql, Transaccion.class)
                    .setParameter("tipo", tipoTransaccion.toLowerCase())
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo transacciones para facturación (all files)", e);
            return Collections.emptyList();
        }
    }


    /**
     * Obtiene transacciones por tipo (ej. "VENTA", "COMPRA") sin filtrar por archivo o periodo,
     * pero excluyendo transacciones que además tengan clasificaciones de tipo distinto.
     * Útil para la vista donde queremos solo transacciones exclusivamente de un tipo.
     */
    public List<Transaccion> findForFacturacionByTipoAllExclusive(String tipoTransaccion) {
        if (tipoTransaccion == null || tipoTransaccion.isBlank()) {
            LOGGER.log(Level.WARNING, "Tipo de transacción para facturación nulo o vacío.");
            return Collections.emptyList();
        }
        try {
            String jpql = "SELECT DISTINCT t FROM Transaccion t JOIN t.transaccionClasificacionCollection tc " +
                    "WHERE LOWER(tc.tipoTransaccion) = :tipo " +
                    "AND NOT EXISTS (SELECT tc2 FROM TransaccionClasificacion tc2 WHERE tc2.transaccion = t AND LOWER(tc2.tipoTransaccion) <> :tipo) " +
                    "ORDER BY t.fecha ASC";
            return em.createQuery(jpql, Transaccion.class)
                    .setParameter("tipo", tipoTransaccion.toLowerCase())
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo transacciones para facturación (all exclusive)", e);
            return Collections.emptyList();
        }
    }



    /**
     * Búsqueda más flexible por tipo: 1) busca por clasificación (igual que findForFacturacionByTipoAll),
     * 2) si no encuentra intenta buscar por palabra clave en la descripción,
     * 3) si sigue vacío, carga todas y filtra en memoria por ocurrencia parcial en la descripción.
     */
    public List<Transaccion> findForFacturacionByTipoFlexible(String tipoTransaccion) {
        if (tipoTransaccion == null || tipoTransaccion.isBlank()) return Collections.emptyList();
        String tipoLower = tipoTransaccion.trim().toLowerCase();
        try {
            // 1) intento por clasificación (exacto, case-insensitive)
            List<Transaccion> res = findForFacturacionByTipoAll(tipoTransaccion);
            if (res != null && !res.isEmpty()) return res;

            // preparar patrón para LIKE (clasificación y descripción)
            String patron = "%" + tipoLower + "%";

            // 1b) intento por clasificación usando LIKE (por si hay espacios o variantes)
            List<Transaccion> byClasLike = em.createQuery("SELECT DISTINCT t FROM Transaccion t JOIN t.transaccionClasificacionCollection tc WHERE LOWER(tc.tipoTransaccion) LIKE :patron ORDER BY t.fecha ASC", Transaccion.class)
                    .setParameter("patron", patron)
                    .getResultList();
            if (byClasLike != null && !byClasLike.isEmpty()) return byClasLike;

            // 2) intento por descripción (LIKE)
            List<Transaccion> byDesc = em.createQuery("SELECT t FROM Transaccion t WHERE LOWER(t.descripcion) LIKE :patron ORDER BY t.fecha ASC", Transaccion.class)
                    .setParameter("patron", patron)
                    .getResultList();
            if (byDesc != null && !byDesc.isEmpty()) return byDesc;

            // 3) fallback: cargar todas y filtrar en memoria por descripción
            List<Transaccion> todas = em.createQuery("SELECT t FROM Transaccion t ORDER BY t.fecha ASC", Transaccion.class)
                    .getResultList();
            if (todas == null || todas.isEmpty()) return Collections.emptyList();
            List<Transaccion> filtradas = new java.util.ArrayList<>();
            for (Transaccion t : todas) {
                if (t.getDescripcion() != null && t.getDescripcion().toLowerCase().contains(tipoLower)) {
                    filtradas.add(t);
                }
            }
            return filtradas;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en búsqueda flexible de transacciones por tipo", e);
            return Collections.emptyList();
        }
    }

}