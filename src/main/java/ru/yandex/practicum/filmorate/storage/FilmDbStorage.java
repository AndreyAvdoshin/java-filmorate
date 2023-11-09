package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Slf4j
@Component("FilmDBStorage")
public class FilmDbStorage extends Storage<Film> {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDBStorage mpaDBStorage;
    private final GenreDBStorage genreDBStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDBStorage mpaDBStorage, GenreDBStorage genreDBStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDBStorage = mpaDBStorage;
        this.genreDBStorage = genreDBStorage;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(film.toMap());

        film.setId(key.intValue());
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
        log.info("Обновлен фильм по id - {}", film.getId());
        return film;
    }

    @Override
    public Film getEntityById(int id) {
        log.info("Запрос фильма по id - {}", id);
        String sql = "SELECT * FROM films WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, this::mapRowToFilm);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм по id " + id + " не найден");
        }
    }

    public Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpaDBStorage.getEntityById(resultSet.getInt("mpa_id")))
                .genres(genreDBStorage.getAllGenresByFilmId(resultSet.getInt("id")))
                .likes(new HashSet<>())
                .build();
        film.setId(resultSet.getInt("id"));
        return film;
    }

}
