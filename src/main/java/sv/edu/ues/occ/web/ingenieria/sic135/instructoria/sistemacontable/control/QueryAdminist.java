package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

/*
  Modificaciones realizadas por el desarrollador externo:
  - Convertida la clase a EJB gestionado con @Stateless para usar el EntityManager del contenedor.
  - Reemplazado el uso manual de EntityManagerFactory/Persistence.createEntityManagerFactory por
    inyección de EntityManager con @PersistenceContext(unitName = "SICPu").
  - Mejor manejo de errores y logging; los métodos ahora devuelven listas vacías en caso de fallo.
  Motivo: integrar la utilidad de consultas con la unidad de persistencia definida en persistence.xml
  y evitar crear EMF por cada invocación (anti-pattern en aplicaciones gestionadas).
  Fecha: 2025-11-10
*/

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class QueryAdminist {
    private static final Logger LOGGER = Logger.getLogger(QueryAdminist.class.getName());

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    /**
     * Función genérica para ejecutar una NamedQuery con un parámetro de fecha
     * @param namedQueryName Nombre de la NamedQuery
     * @param fecha Parametro de fecha (puede ser null si no se necesita)
     * @param resultClass Clase que esperamos en el resultado (Object[].class o Entidad.class)
     * @param <T> Tipo genérico del resultado
     * @return Lista de resultados de la consulta (vacía si ocurre un error)
     */
    public <T> List<T> ejecutarNamedQuery(String namedQueryName, LocalDate fecha, Class<T> resultClass) {
        try {
            TypedQuery<T> query = em.createNamedQuery(namedQueryName, resultClass);
            if (fecha != null) {
                query.setParameter("fecha", fecha);
            }
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error ejecutando named query: " + namedQueryName, e);
            return Collections.emptyList();
        }
    }

    /**
     * Función genérica para ejecutar una NamedQuery con rango de fechas
     * @param namedQueryName Nombre de la NamedQuery
     * @param fechaInicio Fecha inicial del rango
     * @param fechaFin Fecha final del rango
     * @param resultClass Clase esperada en el resultado
     * @param <T> Tipo genérico del resultado
     * @return Lista de resultados de la consulta (vacía si ocurre un error)
     */
    public <T> List<T> ejecutarNamedQueryRangoFechas(String namedQueryName, LocalDate fechaInicio, LocalDate fechaFin, Class<T> resultClass) {
        try {
            TypedQuery<T> query = em.createNamedQuery(namedQueryName, resultClass);
            if (fechaInicio != null) {
                query.setParameter("fechaInicio", fechaInicio);
            }
            if (fechaFin != null) {
                query.setParameter("fechaFin", fechaFin);
            }
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error ejecutando named query rango: " + namedQueryName, e);
            return Collections.emptyList();
        }
    }
}
