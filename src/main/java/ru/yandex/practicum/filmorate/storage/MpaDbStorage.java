package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Component
public class MpaDbStorage implements Storage<Mpa> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> get() {
        log.info("Запрос списка рейтингов");
        return jdbcTemplate.query("SELECT * FROM mpa", new BeanPropertyRowMapper<>(Mpa.class));
    }

    @Override
    public Mpa create(Mpa mpa) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genres")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(mpa.toMap());

        mpa.setId(key.intValue());
        log.info("Сохранен рейтинг: {}", mpa);
        return mpa;
    }

    @Override
    public Mpa update(Mpa mpa) {
        String sql = "UPDATE mpa SET name = ? WHERE id = ?";
        int count = jdbcTemplate.update(sql, mpa.getName(), mpa.getId());
        if (count == 0) {
            throw new NotFoundException("Рейтинг по id " + mpa.getId() + " не найден");
        }
        log.info("Обновлен рейтинг по id - {}", mpa.getId());
        return mpa;
    }

    @Override
    public Mpa getEntityById(int id) {
        log.info("Запрос рейтинга по id - {}", id);
        String sql = "SELECT * FROM mpa WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(Mpa.class));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Рейтинг не найден по id - " + id);
        }
    }

}
