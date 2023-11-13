package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage extends Storage<Film> implements LikeStorage {

    public List<Film> getRatedFilms(int count) {
        List<Film> films = new ArrayList<>(entities.values());
        films.sort(Comparator.comparingInt(Film::getLikesCount).reversed());
        return films.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public void addLike(int filmId, int userId) {
        getEntityById(filmId).setLike(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        getEntityById(filmId).removeLike(userId);
    }

    @Override
    public Set<Integer> getLikesByFilmId(int id) {
        return getEntityById(id).getLikes();
    }
}
