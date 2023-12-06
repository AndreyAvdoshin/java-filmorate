package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long timestamp;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private int eventId;
    private int entityId;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();

        values.put("timestamp", new Timestamp(timestamp));
        values.put("user_id", userId);
        values.put("event_type", eventType.toString());
        values.put("operation", operation.toString());
        values.put("entity_id", entityId);

        return values;
    }
}
