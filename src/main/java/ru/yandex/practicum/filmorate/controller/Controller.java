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

    private final BaseService<T> baseService;

    public Controller(BaseService<T> service) {
        this.baseService = service;
    }

    @GetMapping
    public List<T> get() {
        return baseService.get();
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable int id) {
        return baseService.getEntity(id);
    }

    @PostMapping
    public T create(@Valid @RequestBody T entity) {
        Validator.validate(entity);
        return baseService.create(entity);
    }

    @PutMapping
    public T update(@Valid @RequestBody @NonNull T entity) {
        Validator.validate(entity);
        return baseService.update(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        baseService.delete(id);
    }

}
