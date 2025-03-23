package bot.db.controllers;

import bot.db.services.Service;
import java.util.List;
import java.util.Optional;

public abstract class Controller<T, ID> {
    protected final Service<T, ID> service;

    protected Controller(Service<T, ID> service) {
        this.service = service;
    }

    public T save(T entity) {
        return service.save(entity);
    }

    public Optional<T> findById(ID id) {
        return service.findById(id);
    }

    public List<T> findAll() {
        return service.findAll();
    }

    public void delete(T entity) {
        service.delete(entity);
    }

    public void deleteById(ID id) {
        service.deleteById(id);
    }

    public boolean existsById(ID id) {
        return service.existsById(id);
    }
}