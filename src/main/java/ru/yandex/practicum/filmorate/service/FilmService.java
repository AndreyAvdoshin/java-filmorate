package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService extends BaseService<Film> {

    @Autowired
    public FilmService(@Qualifier("FilmDBStorage") Storage<Film> storage) {
        super(storage);
    }

    public Film getFilm(int id) {
        checkFilm(id);
        return storage.getEntityById(id);
    }

    public void addLike(int filmId, int userId) {
        checkFilm(filmId);
        storage.getEntityById(filmId).setLike(userId);
    }

    public void deleteLike(int filmId, int userId) {
        checkFilm(filmId);
        storage.getEntityById(filmId).removeLike(userId);
    }

    public List<Film> getRatedFilms(int count) {
        List<Film> films = storage.getAll();
        films.sort(Comparator.comparingInt(Film::getLikesCount).reversed());
        return films.stream().limit(count).collect(Collectors.toList());
    }

    public void checkFilm(int filmId) {
        Film film = storage.getEntityById(filmId);
        if (film == null) {
            log.debug("Запрос фильма по id - {}", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
    }

    @Override
    public Film getEntity(int id) {
        if (storage.getEntityById(id) == null) {
            throw new NotFoundException("Фильм с id - " + id + " не найден");
        }
        return super.getEntity(id);
    }

}
