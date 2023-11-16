package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
