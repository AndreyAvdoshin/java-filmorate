package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController extends Controller<Genre>{

    private final GenreService service;

    @Autowired
    public GenreController(GenreService service) {
        super(service);
        this.service = service;
    }

    @GetMapping
    public List<Genre> get() {
        return service.getGenres();
    }

}
