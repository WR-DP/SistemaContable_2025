package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

/*
 Modificaciones realizadas por el desarrollador externo:
 - Añadido método findByArchivoIdAndPeriodo(...) para filtrar transacciones por periodos contables
   (mensual, trimestral, anual). Este método valida parámetros y calcula `desde` y `hasta` como
   `LocalDate` y delega a findByArchivoIdAndDateRange.
 - Inyección de EntityManager con @PersistenceContext(unitName = "SICPu") (coincide con persistence.xml).
 Motivo: proveer una API sencilla y centralizada para obtener transacciones por periodo contable.
 Fecha: 2025-11-10
*/

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class TransaccionDAO extends DefaultDataAcces<Transaccion, Object> implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TransaccionDAO.class.getName());

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
        return  em.createQuery("SELECT t FROM Transaccion t WHERE t.transaccionClasificacionCollection IS EMPTY ORDER BY t.fecha",Transaccion.class)
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

    public List<Transaccion> findByArchivoId(Object archivoId) {
        return em.createQuery("""
        SELECT t FROM Transaccion t
        WHERE t.archivoCargadoId.id = :archivoId
        ORDER BY t.fecha DESC
    """, Transaccion.class)
                .setParameter("archivoId", archivoId)
                .getResultList();
    }

    public List<Transaccion> findByArchivoIdAndDateRange(Object archivoId, LocalDate desde, LocalDate hasta) {
        return em.createQuery("""
        SELECT t FROM Transaccion t
        WHERE t.archivoCargadoId.id = :archivoId
          AND t.fecha BETWEEN :desde AND :hasta
        ORDER BY t.fecha ASC
    """, Transaccion.class)
                .setParameter("archivoId", archivoId)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
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

    // ===================
    // Métodos para facturación (ventas/compras)
    // ===================

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
                        "AND t.archivoCargadoId.id = :archivoId " +
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
}
