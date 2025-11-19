package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary;

import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Named
@Stateless
public class TransaccionBean {


    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    // Propiedad para guardar la id seleccionada antes de eliminar
    private UUID selectedId;

    public UUID getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(UUID selectedId) {
        this.selectedId = selectedId;
    }

    public List<Transaccion> getTransacciones() {
        try {
            TypedQuery<Transaccion> query = em.createNamedQuery("Transaccion.findAll", Transaccion.class);
            List<Transaccion> transacciones = query.getResultList();
            System.out.println("TransaccionBean: Se encontraron " + (transacciones != null ? transacciones.size() : 0) + " transacciones.");
            return transacciones;
        } catch (Exception e) {
            System.err.println("TransaccionBean: Error al obtener transacciones: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Devuelve lista vacía en caso de error
        }
    }

    // Nuevo: eliminar transacción por id (UUID) - método con parámetro (no usado por la vista)
    public String eliminar(UUID id) {
        if (id == null) {
            System.err.println("TransaccionBean: ID nulo en eliminar()");
            return "transacciones?faces-redirect=true";
        }
        try {
            Transaccion t = em.find(Transaccion.class, id);
            if (t != null) {
                em.remove(t);
                System.out.println("TransaccionBean: Eliminada transaccion " + id);
            } else {
                System.out.println("TransaccionBean: No se encontró transaccion con id " + id);
            }
        } catch (Exception e) {
            System.err.println("TransaccionBean: Error al eliminar transaccion: " + e.getMessage());
            e.printStackTrace();
        }
        return "transacciones?faces-redirect=true";
    }

    // Nuevo: método sin parámetros utilizado por la vista (usa selectedId)
    public String eliminar() {
        if (this.selectedId == null) {
            System.err.println("TransaccionBean: selectedId nulo en eliminar()");
            return "transacciones?faces-redirect=true";
        }
        try {
            Transaccion t = em.find(Transaccion.class, this.selectedId);
            if (t != null) {
                em.remove(t);
                System.out.println("TransaccionBean: Eliminada transaccion " + this.selectedId);
            } else {
                System.out.println("TransaccionBean: No se encontró transaccion con id " + this.selectedId);
            }
        } catch (Exception e) {
            System.err.println("TransaccionBean: Error al eliminar transaccion: " + e.getMessage());
            e.printStackTrace();
        } finally {
            this.selectedId = null;
        }
        return "transacciones?faces-redirect=true";
    }
}
