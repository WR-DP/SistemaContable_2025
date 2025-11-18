package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;
import java.util.List;
import java.util.List;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ArchivoCargadoDAO extends DefaultDataAcces<ArchivoCargado, Object> implements Serializable {

    @PersistenceContext(unitName = "SICPu")
    private EntityManager em;

    public ArchivoCargadoDAO() {
        super(ArchivoCargado.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<ArchivoCargado> getEntityClass() {
        return ArchivoCargado.class;
    }
    @Override
    public ArchivoCargado findById(Object id) {
        if (id == null) return null;
        return em.find(ArchivoCargado.class, id);
    }

    //@Override
    public void edit(Transaccion transaccionSeleccionado) { // Este DAO no edita transacciones
    }
    public void editArchivo(ArchivoCargado archivo) {
        em.merge(archivo);
    }
    public List<ArchivoCargado> findAll() {
        return getEntityManager()
                .createNamedQuery("ArchivoCargado.findAll", ArchivoCargado.class)
                .getResultList();
    }

    public List<ArchivoCargado> findByNombreLike(String nombre) {
        return getEntityManager()
                .createNamedQuery("ArchivoCargado.findByNombreLike", ArchivoCargado.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    public List<ArchivoCargado> findByUsuario(String usuario) {
        return getEntityManager()
                .createNamedQuery("ArchivoCargado.findByUsuario", ArchivoCargado.class)
                .setParameter("usuario", usuario)
                .getResultList();
    }

}