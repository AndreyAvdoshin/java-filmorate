package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Slf4j
@Service
public class UserService extends BaseService<User> {

    private final FriendDbStorage friendDbStorage;

    public UserService(Storage<User> storage, FriendDbStorage friendDbStorage) {
        super(storage);
        this.friendDbStorage = friendDbStorage;
    }

    public void createFriendship(int firstUserId, int secondUserId) {
        checkUsers(firstUserId, secondUserId);
        friendDbStorage.createFriendship(firstUserId, secondUserId);
    }

    public void deleteFriend(int firstUserId, int secondUserId) {
        checkUsers(firstUserId, secondUserId);
        friendDbStorage.deleteFriend(firstUserId, secondUserId);
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
}
