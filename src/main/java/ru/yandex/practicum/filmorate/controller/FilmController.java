package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends Controller<Film> {

    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        super(service);
        this.service = service;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        if (id <= 0) {
            throw new IncorrectParameterException("id");
        } else if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        }

        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
         if (userId <= 0) {
            throw new NotFoundException("Пользователь с id - " + userId + " не найден");
        }
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getRatedFilms(@RequestParam(defaultValue = "10") Integer count) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return service.getRatedFilms(count);
    }

}
