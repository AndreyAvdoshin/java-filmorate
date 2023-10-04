package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    public final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping
    public List<Film> getFilms() {
        log.info("Количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        // Написать валидацию
        Validator.validate(film);
        film.setId(++id);
        films.put(id, film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        // Написать валидацию
        Validator.validate(films.get(film.getId()));
        films.put(film.getId(), film);
        log.info("Фильм {} изменен", film);
        return films.get(film.getId());
    }
}
