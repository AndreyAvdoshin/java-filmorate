package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService extends BaseService<Film> {

    Storage<User> userStorage;
    LikeStorage likeStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") Storage<Film> storage,
                       @Qualifier("UserDbStorage") Storage<User> userStorage,
                       @Qualifier("LikeDbStorage") LikeStorage likeStorage) {
        super(storage);
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
    }

    public void addLike(int filmId, int userId) {
        getEntity(filmId);
        userStorage.getEntityById(userId);
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        getEntity(filmId);
        userStorage.getEntityById(userId);
        likeStorage.deleteLike(filmId, userId);
    }

    public List<Film> getRatedFilms(int count) {
        return likeStorage.getRatedFilms(count);
    }

}
