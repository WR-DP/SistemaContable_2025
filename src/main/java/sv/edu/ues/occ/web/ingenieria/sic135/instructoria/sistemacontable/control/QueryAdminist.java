package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;


public class QueryAdminist {
    private static final String PERSISTENCE_UNIT = "miUnidadPersistencia";

    /**
     * Función genérica para ejecutar una NamedQuery con un parámetro de fecha
     * @param namedQueryName Nombre de la NamedQuery
     * @param fecha Parametro de fecha (puede ser null si no se necesita)
     * @param resultClass Clase que esperamos en el resultado (Object[].class o Entidad.class)
     * @param <T> Tipo genérico del resultado
     * @return Lista de resultados de la consulta
     */
    //este metodo que tengo aqui es para consultas con una sola fecha, siempre gragarle una variable donde recibiras la lista del getresulset//
    public static <T> List<T> ejecutarNamedQuery(String namedQueryName, LocalDate fecha, Class<T> resultClass) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        List<T> resultados = null;

        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            em = emf.createEntityManager();

            TypedQuery<T> query = em.createNamedQuery(namedQueryName, resultClass);

            if (fecha != null) {
                query.setParameter("fecha", fecha);
            }

            resultados = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }

        return resultados;
    }
    //este metodo que tengo aqui es para consultas con rango de fechas siempre gragarle una variable donde recibiras la lista del getresulset//
    public static <T> List<T> ejecutarNamedQueryRangoFechas(String namedQueryName, LocalDate fechaInicio, LocalDate fechaFin, Class<T> resultClass) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        List<T> resultados = null;

        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            em = emf.createEntityManager();

            TypedQuery<T> query = em.createNamedQuery(namedQueryName, resultClass);

            if (fechaInicio != null) {
                query.setParameter("fechaInicio", fechaInicio);
            }
            if (fechaFin != null) {
                query.setParameter("fechaFin", fechaFin);
            }

            resultados = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }

        return resultados;
    }
}
