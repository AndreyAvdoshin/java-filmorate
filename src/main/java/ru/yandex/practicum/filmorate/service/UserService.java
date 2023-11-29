package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class UserService extends BaseService<User> {

    private final FriendDbStorage friendDbStorage;
    private final FeedDbStorage feedDbStorage;

    public UserService(Storage<User> storage, FriendDbStorage friendDbStorage, FeedDbStorage feedDbStorage) {
        super(storage);
        this.friendDbStorage = friendDbStorage;
        this.feedDbStorage = feedDbStorage;
    }

    public void createFriendship(int firstUserId, int secondUserId) {
        checkUsers(firstUserId, secondUserId);
        friendDbStorage.createFriendship(firstUserId, secondUserId);
        feedDbStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(firstUserId)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .entityId(secondUserId).build());
    }

    public void deleteFriend(int firstUserId, int secondUserId) {
        checkUsers(firstUserId, secondUserId);
        friendDbStorage.deleteFriend(firstUserId, secondUserId);
        feedDbStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(firstUserId)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .entityId(secondUserId).build());
    }

    public List<User> getFriends(int id) {
        return friendDbStorage.getFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        checkUsers(id, otherId);

        return friendDbStorage.getCommonFriends(id, otherId);
    }

    public User checkName(@NonNull User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    public void checkUsers(int firstUserId, int secondUserId) {
        getEntity(firstUserId);
        getEntity(secondUserId);
    }

    public List<Event> getEvents(int userId) {
        getEntity(userId); // Проверка юзера
        return feedDbStorage.getEventsByUserId(userId);
    }
}
