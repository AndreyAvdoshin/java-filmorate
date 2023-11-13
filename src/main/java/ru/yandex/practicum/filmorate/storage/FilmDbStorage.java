package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("FilmDbStorage")
public class FilmDbStorage extends Storage<Film> {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDBStorage;
    private final GenreDbStorage genreDBStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDBStorage, GenreDbStorage genreDBStorage, LikeStorage likeStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDBStorage = mpaDBStorage;
        this.genreDBStorage = genreDBStorage;
        this.likeStorage = likeStorage;
    }

    @Override
    public List<Film> get() {
        log.info("Запрос всех фильмов");
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, new FilmMapper(mpaDBStorage, genreDBStorage, likeStorage));
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(film.toMap());

        film.setId(key.intValue());
        film.setMpa(mpaDBStorage.getEntityById(film.getMpa().getId()));

        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, key.intValue(), genre.getId());
        }


        film.setGenres(genreDBStorage.getAllGenresByFilmId(film.getId()));
        log.info("Сохранен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        int count = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        if (count == 0) {
            throw new NotFoundException("Фильм по id " + film.getId() + " не найден");
        }
        // Перезаписываем жанры
        sql = "DELETE film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());

        sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }

        film.setMpa(mpaDBStorage.getEntityById(film.getMpa().getId()));
        film.setGenres(genreDBStorage.getAllGenresByFilmId(film.getId()));
        log.info("Обновлен фильм по id - {}", film.getId());
        return film;
    }

    @Override
    public Film getEntityById(int id) {
        log.info("Запрос фильма по id - {}", id);
        String sql = "SELECT * FROM films WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new FilmMapper(mpaDBStorage, genreDBStorage, likeStorage));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм по id " + id + " не найден");
        }
    }

}
