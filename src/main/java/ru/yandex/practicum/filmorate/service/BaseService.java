package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public abstract class BaseService<T extends Entity> {

    protected final Storage<T> storage;

    public BaseService(Storage<T> storage) {
        this.storage = storage;
    }

    public List<T> get() {
        return new ArrayList<>(storage.get());
    }

    public T create(T entity) {
        return storage.create(entity);
    }

    public T update(T entity) {
        return storage.update(entity);
    }

    public void delete(int id) {
        storage.delete(id);
    }

    public T getEntity(int id) {
        return storage.getEntityById(id);
    }

}
