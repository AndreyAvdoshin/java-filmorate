package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Slf4j
@Service
public class FilmService extends BaseService<Film> {

    Storage<User> userStorage;
    LikeDbStorage likeDbStorage;
    FilmDbStorage filmDbStorage;

    public FilmService(Storage<Film> storage, Storage<User> userStorage, LikeDbStorage likeDbStorage) {
        super(storage);
        this.userStorage = userStorage;
        this.likeDbStorage = likeDbStorage;
        this.filmDbStorage = (FilmDbStorage) storage;
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

    public List<Film> getRatedFilms(int count) {
        return likeDbStorage.getRatedFilms(count);
    }

    public List<Film> getCommonFilms(int id, int friendId) {
        userStorage.getEntityById(id);
        userStorage.getEntityById(friendId);
        return filmDbStorage.getCommonFilms(id, friendId);
    }

}
