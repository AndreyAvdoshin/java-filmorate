package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService extends BaseService<Film> {

    private final Storage<User> userStorage;
    private final LikeDbStorage likeDbStorage;
    private final FeedDbStorage feedDbStorage;
    private final Storage<Director> directorDbStorage;
    private final FilmDbStorage filmDbStorage;

    public FilmService(FilmDbStorage storage,
                       Storage<User> userStorage,
                       LikeDbStorage likeDbStorage,
                       FeedDbStorage feedDbStorage,
                       Storage<Director> directorDbStorage) {
        super(storage);
        this.filmDbStorage = storage;
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.directorDbStorage = directorDbStorage;
        this.feedDbStorage = feedDbStorage;
    }

    public void addLike(int filmId, int userId) {
        getEntity(filmId);
        userStorage.getEntityById(userId);
        likeDbStorage.addLike(filmId, userId);
        feedDbStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .entityId(filmId).build());
    }

    public void deleteLike(int filmId, int userId) {
        getEntity(filmId);
        userStorage.getEntityById(userId);
        likeDbStorage.deleteLike(filmId, userId);
        feedDbStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .entityId(filmId).build());
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

    public List<Film> getRatedFilms(int count) {
        return likeDbStorage.getRatedFilms(count);
    }

    public List<Film> getCommonFilms(int id, int friendId) {
        userStorage.getEntityById(id);
        userStorage.getEntityById(friendId);
        return filmDbStorage.getCommonFilms(id, friendId);
    }

}
