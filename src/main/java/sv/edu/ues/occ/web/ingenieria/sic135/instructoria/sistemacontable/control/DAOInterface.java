package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import java.util.List;

public interface DAOInterface <T, ID> {

    void create(T entity) throws IllegalStateException, IllegalArgumentException;
    T findById(Object id) throws IllegalArgumentException, IllegalStateException;
    void delete(T entity) throws IllegalStateException, IllegalArgumentException;
    T update(T entity) throws IllegalStateException, IllegalArgumentException;
    List<T> findRange(int first, int max) throws IllegalArgumentException;
    int count () throws IllegalStateException;
    List<T> findAll() throws IllegalStateException;

}
