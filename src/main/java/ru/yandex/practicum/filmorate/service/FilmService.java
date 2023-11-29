package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class FilmService extends BaseService<Film> {

    private final Storage<User> userStorage;
    private final LikeDbStorage likeDbStorage;
    private final FeedDbStorage feedDbStorage;

    public FilmService(Storage<Film> storage, Storage<User> userStorage, LikeDbStorage likeDbStorage,
                       FeedDbStorage feedDbStorage) {
        super(storage);
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
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

    public List<Film> getRatedFilms(int count) {
        return likeDbStorage.getRatedFilms(count);
    }

}
