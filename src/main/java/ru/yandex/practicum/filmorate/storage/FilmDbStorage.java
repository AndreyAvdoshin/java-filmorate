package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmDbStorage implements Storage<Film> {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDBStorage;
    private final GenreDbStorage genreDBStorage;
    private final LikeDbStorage likeDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDBStorage, GenreDbStorage genreDBStorage, LikeDbStorage likeDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDBStorage = mpaDBStorage;
        this.genreDBStorage = genreDBStorage;
        this.likeDbStorage = likeDbStorage;
    }

    @Override
    public List<Film> get() {
        log.info("Запрос всех фильмов");
        String sql = "SELECT * FROM films, mpa WHERE films.mpa_id = mpa.id";
        return jdbcTemplate.query(sql, new FilmMapper(genreDBStorage, likeDbStorage));
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

        //Получение id всех жанров фильма
        List<Integer> genres = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, genres.get(i));
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });

        film.setMpa(mpaDBStorage.getEntityById(film.getMpa().getId()));
        film.setGenres(genreDBStorage.getAllGenresByFilmId(film.getId()));
        log.info("Обновлен фильм по id - {}", film.getId());
        return film;
    }

    @Override
    public Film getEntityById(int id) {
        log.info("Запрос фильма по id - {}", id);
        String sql = "SELECT * FROM films, mpa WHERE films.mpa_id = mpa.id AND films.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new FilmMapper(genreDBStorage, likeDbStorage));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Фильм по id " + id + " не найден");
        }
    }

}
