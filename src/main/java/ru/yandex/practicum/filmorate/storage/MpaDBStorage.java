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
@Component("MpaDBStorage")
public class MpaDBStorage extends Storage<Mpa> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> get() {
        log.info("Запрос списка рейтингов");
        return jdbcTemplate.query("SELECT * FROM mpa", new BeanPropertyRowMapper<>(Mpa.class));
    }

    @Override
    public Mpa getEntityById(int id) {
        log.info("Запрос рейтинга по id - {}", id);
        Mpa mpa = jdbcTemplate.query("SELECT * FROM mpa WHERE id = ?", new Object[]{id},
                new BeanPropertyRowMapper<>(Mpa.class))
                .stream()
                .findAny()
                .orElse(null);
        if (mpa == null) {
            throw new NotFoundException("Не найден рейтинг по id - " + id);
        }
        return mpa;
    }

}
