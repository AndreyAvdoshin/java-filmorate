package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.service.BaseService;
import ru.yandex.practicum.filmorate.service.Validator;

import javax.validation.Valid;
import java.util.List;

@Slf4j
public abstract class Controller<T extends Entity> {

    private final BaseService<T> service;

    public Controller(BaseService<T> service) {
        this.service = service;
    }

    @GetMapping
    public List<T> get() {
        return service.get();
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable int id) {
        return service.getEntity(id);
    }

    @PostMapping
    public T create(@Valid @RequestBody T entity) {
        Validator.validate(entity);
        return service.create(entity);
    }

    @PutMapping
    public T update(@Valid @RequestBody @NonNull T entity) {
        Validator.validate(service.getEntity(entity.getId()));
        return service.update(entity);
    }

}
