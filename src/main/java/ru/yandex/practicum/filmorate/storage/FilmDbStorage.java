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
import ru.yandex.practicum.filmorate.model.Director;
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
    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         MpaDbStorage mpaDBStorage,
                         GenreDbStorage genreDBStorage,
                         LikeDbStorage likeDbStorage,
                         DirectorDbStorage directorDbStorage
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDBStorage = mpaDBStorage;
        this.genreDBStorage = genreDBStorage;
        this.likeDbStorage = likeDbStorage;
        this.directorDbStorage = directorDbStorage;
    }

    @Override
    public List<Film> get() {
        log.info("Запрос всех фильмов");
        String sql = "SELECT * FROM films, mpa WHERE films.mpa_id = mpa.id";
        return jdbcTemplate.query(sql, new FilmMapper(
                genreDBStorage,
                likeDbStorage,
                directorDbStorage));
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
        sql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(sql, key.intValue(), director.getId());
        }

        film.setGenres(genreDBStorage.getAllGenresByFilmId(film.getId()));
        film.setDirectors(directorDbStorage.getAllDirectorsByFilmId(film.getId()));
        log.info("Сохранен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";
        int count = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (count == 0) {
            throw new NotFoundException("Фильм по id " + film.getId() + " не найден");
        }
        // Перезаписываем жанры и режиссеров
        sql = "DELETE film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
        sql = "DELETE film_director WHERE film_id = ?";
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

        //Получение id всех режиссеров фильма
        List<Integer> directors = film.getDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toList());
        sql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, directors.get(i));
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });

        film.setMpa(mpaDBStorage.getEntityById(film.getMpa().getId()));
        film.setGenres(genreDBStorage.getAllGenresByFilmId(film.getId()));
        film.setDirectors(directorDbStorage.getAllDirectorsByFilmId(film.getId()));
        log.info("Обновлен фильм по id - {}", film.getId());
        return film;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM films WHERE id = ?";
        int count = jdbcTemplate.update(sql, id);
        if (count == 0) {
            throw new NotFoundException("Фильм по id " + id + " не найден");
        }
        log.info("Удален фильм по id - {}", id);
    }

    @Override
    public Film getEntityById(int id) {
        log.info("Запрос фильма по id - {}", id);
        String sql = "SELECT * FROM films, mpa WHERE films.mpa_id = mpa.id AND films.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{id},
                    new FilmMapper(genreDBStorage,
                            likeDbStorage,
                            directorDbStorage));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Фильм по id " + id + " не найден");
        }
    }

    public List<Film> getDirectorFilms(int directorId) {
        log.info("Запрос всех фильмов режиссера - {}", directorId);
        String sql = "SELECT films.*, mpa.* " +
                "FROM film_director fd " +
                "LEFT JOIN films ON fd.film_id = films.id " +
                "INNER JOIN mpa ON films.mpa_id = mpa.id " +
                "WHERE fd.director_id = ?" +
                "ORDER BY films.release_date ASC";
        return jdbcTemplate.query(sql, new FilmMapper(genreDBStorage, likeDbStorage, directorDbStorage), directorId);
    }

}
