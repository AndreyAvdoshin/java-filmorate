package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;


import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class Storage<T extends Entity> {

    final Map<Integer, T> entities = new HashMap<>();
    private int id = 0;

    public List<T> get() {
        return new ArrayList<>(entities.values());
    }

    public T create(T entity) {
        entity.setId(++id);
        entities.put(id, entity);
        log.info("Сохранен объект: {}", entity);
        return entity;
    }

    public T update(T entity) {
        entities.put(entity.getId(), entity);
        return entity;
    }

    public T getEntityById(int id) {
        T entity = entities.get(id);
        if (entity == null) {
            throw new NotFoundException("Объект по id - " + id + " не найден");
        }
        return entity;
    }

}
