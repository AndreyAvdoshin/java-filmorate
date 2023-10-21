package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
