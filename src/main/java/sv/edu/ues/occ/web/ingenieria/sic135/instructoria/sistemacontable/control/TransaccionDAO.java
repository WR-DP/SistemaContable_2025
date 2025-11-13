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


}
