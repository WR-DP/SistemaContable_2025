package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public abstract class DefaultDataAcces <T, ID> implements DAOInterface<T, ID> {

    public abstract EntityManager getEntityManager();//obliga a usar em
    protected abstract Class<T> getEntityClass();

    private final Class<T> TipoDato;

    public DefaultDataAcces(Class<T> TipoDato){
        this.TipoDato = TipoDato;
    }

    @Override
    public void create(T entity) throws IllegalStateException, IllegalArgumentException {
        EntityManager em = null;

        if(entity==null){
            throw new IllegalArgumentException("Parametro no valido: entity is null");
        }
        try {
            em = getEntityManager();
            if(em == null){
                throw new IllegalStateException("Error al acceder al repositorio");
            }
            em.persist(entity);

        }catch (Exception ex){
            throw new  IllegalStateException("Error al acceder al repositorio",ex);
        }
    }

    @Override
    public T findById(Object id) throws IllegalArgumentException, IllegalStateException {
        EntityManager em= null;
        if(id==null){
            throw new IllegalArgumentException("Parametro no valido: ID");
        }
        try{
            em = getEntityManager();
            if (em == null) {
                throw  new IllegalStateException("Error al acceder al repositorio");
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Error al acceder al repositorio",ex);
        }
        return em.find(TipoDato, id);
    }

    @Override
    public void delete(T entity) throws IllegalStateException, IllegalArgumentException {
        EntityManager em = null;
        if(entity==null){
            throw new IllegalArgumentException("Parametro no valido: entity is null");
        }
        try {
            em = getEntityManager();
            if(em == null){
                throw new IllegalStateException("Error al acceder al repositorio");
            }
            T managedEntity = em.merge(entity);
            em.remove(em.merge(entity));
        }catch (Exception ex){
            throw new  IllegalStateException("Error al acceder al repositorio",ex);
        }
    }

    @Override
    public T update(T entity) throws IllegalStateException, IllegalArgumentException {
        EntityManager em = null;
        if(entity==null){
            throw new IllegalArgumentException("Parametro no valido: entity is null");
        }
        try {
            em = getEntityManager();
            if(em == null){
                throw new IllegalStateException("Error al acceder al repositorio");
            }
            return em.merge(entity);
        }catch (Exception ex){
            throw new  IllegalStateException("Error al acceder al repositorio",ex);
        }
    }

    @Override
    public List<T> findRange(int min, int max) throws IllegalArgumentException, IllegalAccessException {
        if(min < 0 && max < 1){
            throw new IllegalArgumentException("Parametro no valido: min e max");
        }
        try {
            EntityManager em = getEntityManager();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(getEntityClass());
            Root<T> root = cq.from(getEntityClass());
            CriteriaQuery<T> all = cq.select(root);

            TypedQuery<T> allQuery = em.createQuery(cq);
            allQuery.setFirstResult(min);
            allQuery.setMaxResults(max);
            return allQuery.getResultList();
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo acceder al repositorio",ex);
        }
    }

    @Override
    public int count(T entity) throws IllegalStateException {
        EntityManager em= null;
        try{
            em = getEntityManager();
            if(em == null){
                throw new IllegalStateException("Error al acceder al repositorio");
            }
        }
        catch (Exception ex){
            throw new IllegalStateException("No se pudo acceder al repositorio",ex);
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(getEntityClass());
        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult().intValue();
    }
}
