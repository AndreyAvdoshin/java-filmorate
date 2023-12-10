package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Slf4j
@Component
public class FeedDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addEvent(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feed")
                .usingGeneratedKeyColumns("event_id");
        simpleJdbcInsert.execute(event.toMap());

        log.info("Сохранено событие: {}", event);
    }

    public List<Event> getEventsByUserId(int userId) {
        log.info("Запрос ленты пользователя id - {}", userId);
        String sql = "SELECT * FROM feed WHERE user_id = ?";
        return jdbcTemplate.query(sql, new EventMapper(), userId);
        }

}
