package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class DirectorDbStorage implements Storage<Director> {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> get() {
        log.info("Запрос всех режиссеров");
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Director.class));
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(director.toMap());

        director.setId(key.intValue());
        log.info("Сохранен режиссер: {}", director);
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        int count = jdbcTemplate.update(sql, director.getName());
        if (count == 0) {
            throw new NotFoundException("Режиссер по id " + director.getId() + " не найден");
        }
        log.info("Обновлен режиссер по id - {}", director.getId());
        return director;
    }

    public void delete(int id) {
        String sql = "DELETE FROM directors WHERE id = ?";
        int count = jdbcTemplate.update(sql, id);
        if (count == 0) {
            throw new NotFoundException("Режиссер по id " + id + " не найден");
        }
        log.info("Удален режиссер по id - {}", id);
    }

    @Override
    public Director getEntityById(int id) {
        log.info("Запрос режиссера по id - {}", id);
        String sql = "SELECT * FROM directors WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(Director.class));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Режиссер не найден по id - " + id);
        }
    }

    public Set<Director> getAllDirectorsByFilmId(int id) {
        String sql = "SELECT g.id, g.name " +
                "FROM film_director fd " +
                "LEFT JOIN films f ON fd.film_id = f.id " +
                "RIGHT JOIN directors d ON fd.director_id = d.id " +
                "WHERE f.id = ? " +
                "ORDER BY d.id";
        return new HashSet<>(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Director.class), id));
    }
}
