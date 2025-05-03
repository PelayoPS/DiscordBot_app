package bot.services.impl;

import bot.repositories.Repository;
import bot.services.Service;
import java.util.List;
import java.util.Optional;

/**
 * Implementación abstracta base para los servicios.
 * Proporciona operaciones CRUD genéricas delegando en el repositorio
 * correspondiente.
 * 
 * @author PelayoPS
 */
public abstract class AbstractService<T, ID> implements Service<T, ID> {
    protected final Repository<T, ID> repository;

    /**
     * Constructor de AbstractService.
     * 
     * @param repository Repositorio asociado al servicio
     */
    protected AbstractService(Repository<T, ID> repository) {
        this.repository = repository;
    }

    /**
     * Guarda una entidad.
     * 
     * @param entity Entidad a guardar
     * @return Entidad guardada
     */
    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    /**
     * Busca una entidad por su ID.
     * 
     * @param id ID de la entidad
     * @return Optional con la entidad encontrada o vacío si no existe
     */
    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    /**
     * Obtiene todas las entidades.
     * 
     * @return Lista de entidades
     */
    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    /**
     * Elimina una entidad por su ID.
     * 
     * @param id ID de la entidad a eliminar
     */
    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    /**
     * Verifica si existe una entidad por su ID.
     * 
     * @param id ID a verificar
     * @return true si existe, false si no
     */
    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}