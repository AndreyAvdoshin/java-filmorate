package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FriendStorage {

    void createFriendship(int firstUserId, int secondUserId);

    void deleteFriend(int firstUserId, int secondUserId);

    List<User> getFriends(int id);

    List<User> getCommonFriends(int id, int otherId);

    Set<Integer> getFriendsId(int id);
}
