package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class GenreDbStorage implements Storage<Genre> {

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
    public Genre create(Genre genre) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genres")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(genre.toMap());

        genre.setId(key.intValue());
        log.info("Сохранен жанр: {}", genre);
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        String sql = "UPDATE genres SET name = ? WHERE id = ?";
        int count = jdbcTemplate.update(sql, genre.getName());
        if (count == 0) {
            throw new NotFoundException("Жанр по id " + genre.getId() + " не найден");
        }
        log.info("Обновлен жанр по id - {}", genre.getId());
        return genre;
    }

    @Override
    public Genre getEntityById(int id) {
        log.info("Запрос жанра по id - {}", id);
        String sql = "SELECT * FROM genres WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(Genre.class));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Жанр не найден по id - " + id);
        }
    }

    public Set<Genre> getAllGenresByFilmId(int id) {
        String sql = "SELECT genres.id, genres.name FROM film_genre " +
                "INNER JOIN genres ON film_genre.genre_id = genres.id " +
                "WHERE film_genre.film_id = ? ORDER BY genres.id";
        return new HashSet<>(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Genre.class), id));
    }

}
