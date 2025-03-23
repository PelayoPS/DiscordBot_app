package bot.db.services.impl;

import bot.db.repositories.Repository;
import bot.db.services.Service;
import java.util.List;
import java.util.Optional;

public abstract class AbstractService<T, ID> implements Service<T, ID> {
    protected final Repository<T, ID> repository;

    protected AbstractService(Repository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}