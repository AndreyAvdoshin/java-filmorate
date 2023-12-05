package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService extends BaseService<Film> {

    Storage<User> userStorage;
    LikeDbStorage likeDbStorage;
    Storage<Director> directorDbStorage;
    FilmDbStorage filmDbStorage;

    public FilmService(FilmDbStorage storage,
                       Storage<User> userStorage,
                       LikeDbStorage likeDbStorage,
                       Storage<Director> directorDbStorage) {
        super(storage);
        this.filmDbStorage = storage;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.directorDbStorage = directorDbStorage;
    }

    public void addLike(int filmId, int userId) {
        getEntity(filmId);
        userStorage.getEntityById(userId);
        likeDbStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        getEntity(filmId);
        userStorage.getEntityById(userId);
        likeDbStorage.deleteLike(filmId, userId);
    }

    public List<Film> getDirectorFilmsBySortField(int directorId, String sortField) {
        directorDbStorage.getEntityById(directorId);
        List<Film> directorFilms = filmDbStorage.getDirectorFilms(directorId);
        if (sortField.equals("likes")) {
            return directorFilms.stream()
                    .sorted(Comparator.comparingInt(Film::getLikesCount).reversed().thenComparing(Film::getId))
                    .collect(Collectors.toList());
        }
        return directorFilms;
    }

    public List<Film> getRatedFilms(Integer count, Integer genreId, Integer releaseYear) {
        return filmDbStorage.getRatedFilms(count, genreId, releaseYear);
    }

    public List<Film> getCommonFilms(int id, int friendId) {
        userStorage.getEntityById(id);
        userStorage.getEntityById(friendId);
        return filmDbStorage.getCommonFilms(id, friendId);
    }

    public List<Film> getFilmsByQueryFieldAndCategories(String queryField, List<String> queryCategories) {
        return filmDbStorage.getFilmsByQueryFieldAndCategories(queryField, queryCategories).stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed().thenComparing(Film::getId))
                .collect(Collectors.toList());
    }

}
