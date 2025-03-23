package bot.db.services;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para los servicios.
 * 
 * @param <T>  Tipo de entidad
 * @param <ID> Tipo del identificador
 */
public interface Service<T, ID> {
    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void delete(T entity);

    void deleteById(ID id);

    boolean existsById(ID id);
}