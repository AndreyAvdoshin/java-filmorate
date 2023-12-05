package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static ru.yandex.practicum.filmorate.Constants.FILM_SORT_FIELDS;
import static ru.yandex.practicum.filmorate.Constants.QUERY_CATEGORY_FIELDS;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends Controller<Film> {

    private final FilmService service;

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
    public List<Film> getRatedFilms(@RequestParam(defaultValue = "10", required = false) @Positive Integer count,
                                    @RequestParam(required = false) @Positive Integer genreId,
                                    @RequestParam(required = false) @Positive Integer year) {
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return service.getRatedFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Integer userId, Integer friendId) {
        if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        } else if (friendId <= 0) {
            throw new IncorrectParameterException("friendId");
        }
        return service.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable(name = "directorId") int directorId,
                                    @RequestParam(name = "sortBy",
                                            defaultValue = "year",
                                            required = false) String sortField) {
        if (directorId <= 0) {
            throw new NotFoundException("Режиссер с id - " + directorId + " не найден");
        }
        if (!FILM_SORT_FIELDS.contains(sortField)) {
            throw new IncorrectParameterException(sortField);
        }
        return service.getDirectorFilmsBySortField(directorId, sortField);
    }

    @GetMapping("/search")
    public List<Film> getFilmsByQueryField(@RequestParam(name = "query") String queryField,
                                           @RequestParam(name = "by",
                                                   defaultValue = "director,title") String queryCategoryStr) {
        List<String> queryCategories = List.of(queryCategoryStr.split(","));
        List<String> unknownCategories = new ArrayList<>(queryCategories);
        unknownCategories.removeAll(QUERY_CATEGORY_FIELDS);

        if (!unknownCategories.isEmpty()) {
            throw new IncorrectParameterException(unknownCategories.toString());
        }
        if (!Pattern.matches("^[\\sа-яА-Яa-zA-Z0-9]+$", queryField)) {
            throw new IncorrectParameterException(queryField);
        }
        return service.getFilmsByQueryFieldAndCategories(queryField, queryCategories);
    }
}
