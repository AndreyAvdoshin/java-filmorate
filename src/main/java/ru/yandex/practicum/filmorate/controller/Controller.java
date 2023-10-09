package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.service.Validator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class Controller<T extends Entity> {

    protected final Map<Integer, T> entities = new HashMap<>();
    protected int id = 0;

    @GetMapping
    public List<T> get() {
        log.info("Количество объектов в ответе: {}", entities.size());
        return new ArrayList<>(entities.values());
    }

    @PostMapping
    public T create(@Valid @RequestBody T entity) {
        Validator.validate(entity);
        entity.setId(++id);
        entities.put(id, entity);
        log.info("Добавлен объект: {}", entity);
        return entity;
    }

    @PutMapping
    public T update(@Valid @RequestBody T entity) {
        Validator.validate(entities.get(entity.getId()));
        entities.put(entity.getId(), entity);
        log.info("{} изменен", entity);
        return entities.get(entity.getId());
    }
}
