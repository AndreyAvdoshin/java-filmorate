package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmDbStorage implements Storage<Film> {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDBStorage;
    private final GenreDbStorage genreDBStorage;
    private final DirectorDbStorage directorDbStorage;
    private final String getPopularGenreAndYear = "SELECT f.*, COUNT(l.film_id) as likes_count, mpa.* " +
            "FROM films f " +
            "JOIN film_genre fg ON f.id = fg.film_id " +
            "LEFT JOIN mpa ON f.mpa_id = mpa.id " +
            "JOIN genres g ON fg.genre_id = g.id " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "WHERE g.id  = ? AND EXTRACT(YEAR FROM f.release_date) = ? " +
            "GROUP BY f.id " +
            "ORDER BY likes_count DESC " +
            "LIMIT ?";

    private final String getPopularLike = "SELECT films.*, mpa.* " +
            "FROM films " +
            "LEFT JOIN mpa ON films.mpa_id = mpa.id " +
            "LEFT JOIN likes ON films.id = likes.film_id " +
            "GROUP BY films.id " +
            "ORDER BY COUNT(likes.user_id) DESC " +
            "LIMIT ?";

    private final String getPopularYear = "SELECT f.*, COUNT(l.film_id) as likes_count, mpa.*" +
            "FROM films f " +
            "LEFT JOIN mpa ON f.mpa_id = mpa.id " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
            "GROUP BY f.id " +
            "ORDER BY likes_count DESC " +
            "LIMIT ?";

    private final String getPopularGenre = "SELECT f.*, COUNT(l.film_id) as likes_count, mpa.*" +
            "FROM films f " +
            "LEFT JOIN mpa ON f.mpa_id = mpa.id " +
            "JOIN film_genre fg ON f.id = fg.film_id " +
            "JOIN genres g ON fg.genre_id = g.id " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "WHERE g.id  = ? " +
            "GROUP BY f.id " +
            "ORDER BY likes_count DESC " +
            "LIMIT ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         MpaDbStorage mpaDBStorage,
                         GenreDbStorage genreDBStorage,
                         DirectorDbStorage directorDbStorage
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDBStorage = mpaDBStorage;
        this.genreDBStorage = genreDBStorage;
        this.directorDbStorage = directorDbStorage;
    }

    @Override
    public List<Film> get() {
        log.info("Запрос всех фильмов");
        String sql = "SELECT * FROM films, mpa WHERE films.mpa_id = mpa.id";
        return jdbcTemplate.query(sql, new FilmMapper());
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(film.toMap());

        film.setId(key.intValue());

        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, key.intValue(), genre.getId());
        }
        sql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(sql, key.intValue(), director.getId());
        }
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
                    new FilmMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Фильм по id " + id + " не найден");
        }
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.*, mpa.* FROM likes l " +
                "JOIN films f ON f.id = l.film_id " +
                "JOIN mpa ON mpa.id = f.mpa_id " +
                "WHERE l.film_id IN (SELECT l.film_id FROM likes l WHERE l.user_id IN (?, ?) " +
                "GROUP BY l.film_id HAVING COUNT(l.film_id) > 1) " +
                "AND l.user_id = ? " +
                "ORDER BY (SELECT COUNT(*) FROM likes WHERE film_id = l.film_id) DESC";
        log.info("Запрос общих фильмов пользователей - {}, {}", userId, friendId);
        return jdbcTemplate.query(sql, new FilmMapper(),
                userId, friendId, userId);
    }

    public List<Film> getRecommendations(int userId) {
        log.info("Запрос рекомендационных фильмов для пользователя - {}", userId);
        String sql = "SELECT f.*, MPA.ID, mpa.NAME " +
                     "FROM likes l JOIN films f on f.id = l.film_id " + "JOIN mpa on mpa.id = f.mpa_id " +
                     "WHERE l.user_id = (SELECT l2.user_id " +
                                         "FROM likes l1 join likes l2 ON l1.film_id = l2.film_id " +
                                         "AND l1.user_id != l2.user_id " +
                                         "WHERE l1.user_id = ? " +
                                         "GROUP BY l1.user_id, l2.user_id " +
                                         "ORDER BY COUNT(*) DESC limit 1) " +
                     "AND l.film_id NOT IN (SELECT film_id " +
                                            "FROM likes " +
                                            "WHERE user_id = ?)";

        return jdbcTemplate.query(sql, new FilmMapper(),
                userId, userId);
    }

    public List<Film> getDirectorFilms(int directorId) {
        log.info("Запрос всех фильмов режиссера - {}", directorId);
        String sql = "SELECT films.*, mpa.* " +
                "FROM film_director fd " +
                "LEFT JOIN films ON fd.film_id = films.id " +
                "INNER JOIN mpa ON films.mpa_id = mpa.id " +
                "WHERE fd.director_id = ?" +
                "ORDER BY films.release_date ASC";
        return jdbcTemplate.query(sql, new FilmMapper(), directorId);
    }

    public List<Film> getRatedFilms(Integer count, Integer genreId, Integer releaseYear) {
        List<Film> films;
        log.info("Запрос фильмов по рейтингу");
        if (genreId != null || releaseYear != null) {
            if (genreId != null && releaseYear != null) {
                films = jdbcTemplate.query(getPopularGenreAndYear, new FilmMapper(), genreId, releaseYear, count);
            } else if (genreId != null) {
                    films = jdbcTemplate.query(getPopularGenre, new FilmMapper(), genreId, count);

                } else {
                    films = jdbcTemplate.query(getPopularYear, new FilmMapper(), releaseYear, count);
                }
        } else {
            films = jdbcTemplate.query(getPopularLike, new FilmMapper(), count);
        }
        return films;
    }

    public List<Film> getFilmsByQueryFieldAndCategories(String queryField, List<String> queryCategories) {
        log.info("Запрос фильмов по подстроке - {} и категориям - {}", queryField, queryCategories);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        boolean hasTitleCategory = queryCategories.contains("title");
        boolean hasDirectorCategory = queryCategories.contains("director");

        String sql = "SELECT DISTINCT films.*, mpa.* " +
                "FROM film_director fd " +
                "RIGHT JOIN films ON fd.film_id = films.id " +
                (hasDirectorCategory ? "LEFT JOIN directors ON fd.director_id = directors.id " : "") +
                "INNER JOIN mpa ON films.mpa_id = mpa.id " +
                "WHERE " +
                (hasTitleCategory ? "LOWER(films.name) LIKE :queryField " : "1<>1 ") +
                "OR " + (hasDirectorCategory ? "LOWER(directors.name) LIKE :queryField " : "1<>1 ") +
                "ORDER BY films.release_date ASC";
        return namedParameterJdbcTemplate.query(sql, Map.of("queryField", "%" + queryField.toLowerCase() + "%"),
                new FilmMapper());
    }
}
