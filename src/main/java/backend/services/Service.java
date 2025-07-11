package backend.services;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para los servicios.
 * Define operaciones CRUD básicas para cualquier entidad de negocio.
 * 
 * @param <T>  Tipo de entidad
 * @param <ID> Tipo del identificador
 * @author PelayoPS
 */
public interface Service<T, ID> {
    /**
     * Guarda una entidad.
     * 
     * @param entity Entidad a guardar
     * @return Entidad guardada
     */
    T save(T entity);

    /**
     * Elimina una entidad por su identificador.
     * 
     * @param id Identificador de la entidad a eliminar
     */
    void deleteById(ID id);

    /**
     * Busca una entidad por su identificador.
     * 
     * @param id Identificador de la entidad
     * @return Optional con la entidad encontrada o vacío si no existe
     */
    Optional<T> findById(ID id);

    /**
     * Obtiene todas las entidades.
     * 
     * @return Lista de entidades
     */
    List<T> findAll();

    /**
     * Verifica si existe una entidad por su identificador.
     * 
     * @param id Identificador a verificar
     * @return true si existe, false si no
     */
    boolean existsById(ID id);
}