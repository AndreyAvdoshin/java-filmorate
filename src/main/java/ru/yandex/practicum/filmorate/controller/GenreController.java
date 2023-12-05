package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotAllowedException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

@RestController
@RequestMapping("/genres")
public class GenreController extends Controller<Genre> {

    private final GenreService service;

    public GenreController(GenreService service) {
        super(service);
        this.service = service;
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        throw new NotAllowedException("Метод не разрешен для данного ресурса");
    }
}
