package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage extends Storage<User> implements FriendStorage {

    @Override
    public void createFriendship(int firstUserId, int secondUserId) {
        getEntityById(firstUserId).setFriend(secondUserId);
        getEntityById(secondUserId).setFriend(firstUserId);
    }

    @Override
    public void deleteFriend(int firstUserId, int secondUserId) {
        getEntityById(firstUserId).removeFriend(secondUserId);
        getEntityById(secondUserId).removeFriend(firstUserId);
    }

    @Override
    public List<User> getFriends(int id) {
        User user = getEntityById(id);

        return user.getFriends()
                .stream()
                .map(this::getEntityById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        List<User> commonFriends = new ArrayList<>();
        Set<Integer> firstFriendsList = getEntityById(id).getFriends();
        for (Integer i : getEntityById(otherId).getFriends()) {
            if (firstFriendsList.contains(i)) {
                commonFriends.add(getEntityById(i));
            }
        }
        return commonFriends;
    }

    @Override
    public Set<Integer> getFriendsId(int id) {
        return getEntityById(id).getFriends();
    }

}
