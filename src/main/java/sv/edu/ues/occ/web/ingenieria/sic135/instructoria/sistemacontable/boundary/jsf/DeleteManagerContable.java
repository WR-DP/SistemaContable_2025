package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

@Stateless
public class DeleteManagerContable {

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;


    // ======================================================================
    // ===== CONTAR DEPENDENCIAS DE ARCHIVO =================================
    // ======================================================================

    public int contarTransaccionesDeArchivo(UUID idArchivo) {
        return em.createQuery(
                "SELECT COUNT(t) FROM Transaccion t WHERE t.archivoCargado.id = :id",
                Long.class
        )
        .setParameter("id", idArchivo)
        .getSingleResult()
        .intValue();
    }

    public int contarClasificacionesDeArchivo(UUID idArchivo) {
        return em.createQuery(
                "SELECT COUNT(tc) FROM TransaccionClasificacion tc " +
                "WHERE tc.transaccion.id IN (" +
                   "SELECT t.id FROM Transaccion t WHERE t.archivoCargado.id = :id" +
                ")",
                Long.class
        )
        .setParameter("id", idArchivo)
        .getSingleResult()
        .intValue();
    }


    // ======================================================================
    // ===== ELIMINAR EN CASCADA ARCHIVO → TRANSACCION → CLASIFICACION ======
    // ======================================================================

    public void eliminarArchivoEnCascada(UUID idArchivo) {

        // 1. ELIMINAR CLASIFICACIONES (nietos)
        em.createQuery(
                "DELETE FROM TransaccionClasificacion tc " +
                "WHERE tc.transaccion.id IN (" +
                    "SELECT t.id FROM Transaccion t WHERE t.archivoCargado.id = :id" +
                ")"
        )
        .setParameter("id", idArchivo)
        .executeUpdate();

        // 2. ELIMINAR TRANSACCIONES (hijos)
        em.createQuery(
                "DELETE FROM Transaccion t WHERE t.archivoCargado.id = :id"
        )
        .setParameter("id", idArchivo)
        .executeUpdate();

        // 3. ELIMINAR ARCHIVO
        em.createQuery(
                "DELETE FROM ArchivoCargado a WHERE a.id = :id"
        )
        .setParameter("id", idArchivo)
        .executeUpdate();
    }

}
