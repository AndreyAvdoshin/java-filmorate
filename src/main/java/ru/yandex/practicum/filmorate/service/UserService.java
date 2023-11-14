package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Slf4j
@Service
public class UserService extends BaseService<User> {

    final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") Storage<User> storage, @Qualifier("FriendDbStorage") FriendStorage friendStorage) {
        super(storage);
        this.friendStorage = friendStorage;
    }

    public void createFriendship(int firstUserId, int secondUserId) {
        checkUsers(firstUserId, secondUserId);
        friendStorage.createFriendship(firstUserId, secondUserId);
    }

    public void deleteFriend(int firstUserId, int secondUserId) {
        checkUsers(firstUserId, secondUserId);
        friendStorage.deleteFriend(firstUserId, secondUserId);
    }

    public List<User> getFriends(int id) {
        return friendStorage.getFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        checkUsers(id, otherId);

        return friendStorage.getCommonFriends(id, otherId);
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
