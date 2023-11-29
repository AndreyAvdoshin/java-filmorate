package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Event.builder()
                .timestamp(rs.getTimestamp("timestamp").toInstant().toEpochMilli())
                .userId(rs.getInt("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .eventId(rs.getInt("event_id"))
                .entityId(rs.getInt("entity_id"))
                .build();
    }
}
