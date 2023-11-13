package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("GenreDbStorage")
public class GenreDbStorage extends Storage<Genre> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> get() {
        log.info("Запрос списка всех жанров");
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Genre.class));
    }

    @Override
    public Genre getEntityById(int id) {
        log.info("Запрос жанра по id - {}", id);
        String sql = "SELECT * FROM genres WHERE id = ?";
        Genre genre = jdbcTemplate.query(sql, new Object[]{id},
                        new BeanPropertyRowMapper<>(Genre.class))
                .stream()
                .findAny()
                .orElse(null);
        if (genre == null) {
            throw new NotFoundException("Жанр по id - " + id + " не найден");
        }
        return genre;
    }

    public Set<Genre> getAllGenresByFilmId(int id) {
        String sql = "SELECT genres.id, genres.name FROM film_genre " +
                "INNER JOIN genres ON film_genre.genre_id = genres.id " +
                "WHERE film_genre.film_id = ? ORDER BY genres.id";
        return new HashSet<>(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Genre.class), id));
    }

}
