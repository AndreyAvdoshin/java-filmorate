package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService extends BaseService<Film> {

    private final UserDbStorage userStorage;
    private final LikeDbStorage likeDbStorage;
    private final FeedDbStorage feedDbStorage;
    private final DirectorDbStorage directorDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    public FilmService(FilmDbStorage storage,
                       UserDbStorage userStorage,
                       LikeDbStorage likeDbStorage,
                       FeedDbStorage feedDbStorage,
                       DirectorDbStorage directorDbStorage,
                       GenreDbStorage genreDbStorage,
                       MpaDbStorage mpaDbStorage) {
        super(storage);
        this.filmDbStorage = storage;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.directorDbStorage = directorDbStorage;
        this.feedDbStorage = feedDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public List<Film> get() {
        return storage.get()
                .stream().map(this::buildFilmWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public Film getEntity(int id) {
        return buildFilmWithDetails(storage.getEntityById(id));
    }

    @Override
    public Film create(Film film) {
        film = storage.create(film);
        film.setMpa(mpaDbStorage.getEntityById(film.getMpa().getId()));
        return buildFilmWithDetails(film);
    }

    @Override
    public Film update(Film film) {
        return buildFilmWithDetails(storage.update(film));
    }

    public void addLike(int filmId, int userId) {
        checkUserAndFilm(userId, filmId);
        feedDbStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(filmId)
                .build());
        likeDbStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        checkUserAndFilm(userId, filmId);
        feedDbStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(filmId)
                .build());
        likeDbStorage.deleteLike(filmId, userId);
    }

    public List<Film> getDirectorFilmsBySortField(int directorId, String sortField) {
        directorDbStorage.getEntityById(directorId);
        List<Film> directorFilms = filmDbStorage
                .getDirectorFilms(directorId)
                .stream()
                .map(this::buildFilmWithDetails)
                .collect(Collectors.toList());
        if (sortField.equals("likes")) {
            directorFilms.sort(Comparator.comparingInt(Film::getLikesCount)
                    .reversed()
                    .thenComparing(Film::getId));
        }
        return directorFilms;
    }

    public List<Film> getRatedFilms(Integer count, Integer genreId, Integer releaseYear) {
        return filmDbStorage
                .getRatedFilms(count, genreId, releaseYear)
                .stream()
                .map(this::buildFilmWithDetails)
                .collect(Collectors.toList());
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        checkUsers(userId, friendId);
        return filmDbStorage.getCommonFilms(userId, friendId)
                .stream()
                .map(this::buildFilmWithDetails)
                .collect(Collectors.toList());
    }

    public List<Film> getRecommendations(int userId) {
        return filmDbStorage.getRecommendations(userId)
                .stream()
                .map(this::buildFilmWithDetails)
                .collect(Collectors.toList());
    }

    public List<Film> getFilmsByQueryFieldAndCategories(String queryField, List<String> queryCategories) {
        return filmDbStorage.getFilmsByQueryFieldAndCategories(queryField, queryCategories)
                .stream()
                .map(this::buildFilmWithDetails)
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed().thenComparing(Film::getId))
                .collect(Collectors.toList());
    }

    private Film buildFilmWithDetails(Film film) {
        film.setGenres(genreDbStorage.getAllGenresByFilmId(film.getId()));
        film.setDirectors(directorDbStorage.getAllDirectorsByFilmId(film.getId()));
        film.setLikes(likeDbStorage.getLikesByFilmId(film.getId()));
        return film;
    }

    private void checkUserAndFilm(int userId, int filmId) {
        userStorage.getEntityById(userId);
        getEntity(filmId);
    }

    private void checkUsers(int userId, int friendId) {
        userStorage.getEntityById(userId);
        userStorage.getEntityById(friendId);
    }

}
