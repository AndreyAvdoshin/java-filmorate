package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@Slf4j
@Service
public class UserService extends BaseService<User> {

    private final FriendDbStorage friendDbStorage;
    private final FilmDbStorage filmDbStorage;

    public UserService(Storage<User> storage, FriendDbStorage friendDbStorage, FilmDbStorage filmDbStorage) {
        super(storage);
        this.friendDbStorage = friendDbStorage;
        this.filmDbStorage = filmDbStorage;
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

    public List<Film> getRecommendations(int id) {
        return filmDbStorage.getRecommendations(id);
    }
}
