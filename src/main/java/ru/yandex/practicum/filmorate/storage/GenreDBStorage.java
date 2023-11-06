package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Component("GenreDBStorage")
public class GenreDBStorage extends Storage<Genre> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> get() {
        log.info("Запрос списка всех жанров");
        return jdbcTemplate.query("SELECT * FROM genres", new BeanPropertyRowMapper<>(Genre.class));
    }

    @Override
    public Genre getEntityById(int id) {
        log.info("Запрос жанра по id - {}", id);
        Genre genre = jdbcTemplate.query("SELECT * FROM genres WHERE id = ?", new Object[]{id},
                        new BeanPropertyRowMapper<>(Genre.class))
                .stream()
                .findAny()
                .orElse(null);
        if (genre == null) {
            throw new NotFoundException("Не найден жанр по id - " + id);
        }
        return genre;
    }

}
