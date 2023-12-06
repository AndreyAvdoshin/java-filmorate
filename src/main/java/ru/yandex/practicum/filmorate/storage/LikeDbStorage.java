package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UniqueViolatedException;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Primary
@Component
public class LikeDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDBStorage;
    private final DirectorDbStorage directorDbStorage;

    public LikeDbStorage(JdbcTemplate jdbcTemplate,
                         GenreDbStorage genreDBStorage,
                         DirectorDbStorage directorDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDBStorage = genreDBStorage;
        this.directorDbStorage = directorDbStorage;
    }

    public void addLike(int filmId, int userId) {
        log.info("Пользователь - {} добавляет лайк фильму -  {}", userId, filmId);
        String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, userId, filmId);
        } catch (DuplicateKeyException e) {
            throw new UniqueViolatedException("Лайк от пользователя - " + userId + " уже поставлен фильму - " + filmId);
        }
    }

    public void deleteLike(int filmId, int userId) {
        log.info("Пользователь - {} удаляет лайк у фильма -  {}", userId, filmId);
        String sql = "DELETE likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    public Set<Integer> getLikesByFilmId(int id) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), id));
    }
}
