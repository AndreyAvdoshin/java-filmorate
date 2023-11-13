package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UniqueViolatedException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Primary
@Component
@Qualifier("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDBStorage;
    private final GenreDbStorage genreDBStorage;

    public LikeDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDBStorage, GenreDbStorage genreDBStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDBStorage = mpaDBStorage;
        this.genreDBStorage = genreDBStorage;
    }

    @Override
    public void addLike(int filmId, int userId) {
        log.info("Пользователь - {} добавляет лайк фильму -  {}", userId, filmId);
        String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, userId, filmId);
        } catch (DuplicateKeyException e) {
            throw new UniqueViolatedException("Лайк от пользователя - " + userId + " уже поставлен фильму - " + filmId);
        }
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        log.info("Пользователь - {} удаляет лайк у фильма -  {}", userId, filmId);
        String sql = "DELETE likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public List<Film> getRatedFilms(int count) {
        log.info("Запрос фильмов по рейтингу");
        String sql = "SELECT films.* FROM films LEFT JOIN likes ON films.id = likes.film_id " +
                "GROUP BY films.id ORDER BY COUNT(likes.user_id) DESC LIMIT ?";
        return new ArrayList<>(jdbcTemplate.query(sql, new FilmMapper(mpaDBStorage, genreDBStorage, this), count));
    }

    @Override
    public Set<Integer> getLikesByFilmId(int id) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), id));
    }
}
