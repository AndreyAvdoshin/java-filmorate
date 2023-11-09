package ru.yandex.practicum.filmorate.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService extends BaseService<User> {

    @Autowired
    public UserService(@Qualifier("UserDBStorage") Storage<User> storage) {
        super(storage);
    }

//    public User getUser(int id) {
//        User user = storage.getEntityById(id);
//        if (user == null) {
//            log.debug("Не найден пользователь по id - {}", id);
//            throw new NotFoundException("Пользователь с id " + id + " не найден");
//        }
//        return user;
//    }

    public void createFriendship(int firstUserId, int secondUserId) {
        checkUsers(firstUserId, secondUserId);

        getEntity(firstUserId).setFriend(secondUserId);
        getEntity(secondUserId).setFriend(firstUserId);
    }

    public void deleteFriend(int firstUserId, int secondUserId) {
        checkUsers(firstUserId, secondUserId);

        getEntity(firstUserId).removeFriend(secondUserId);
        getEntity(secondUserId).removeFriend(firstUserId);
    }

    public List<User> getFriends(int id) {
        User user = getEntity(id);

        if (user == null) {
            log.debug("Не найден пользователь по id - {}", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }

        return user.getFriends()
                .stream()
                .map(storage::getEntityById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        checkUsers(id, otherId);

        List<User> commonFriends = new ArrayList<>();
        List<Integer> firstFriendsList = storage.getEntityById(id).getFriends();
        for (Integer i : storage.getEntityById(otherId).getFriends()) {
            if (firstFriendsList.contains(i)) {
                commonFriends.add(getEntity(i));
            }
        }
        return commonFriends;
    }

    public User checkName(@NonNull User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    public void checkUsers(int firstUserId, int secondUserId) {
        User firstUser = getEntity(firstUserId);
        User secondUser = getEntity(secondUserId);

        if (firstUser == null) {
            log.debug("Не найден первый друг по id - {}", firstUserId);
            throw new NotFoundException("Пользователь с id " + firstUserId + " не найден");
        } else if (secondUser == null) {
            log.debug("Не найден второй друг по id - {}", firstUserId);
            throw new NotFoundException("Пользователь с id " + secondUserId + " не найден");
        }
    }

    @Override
    public User getEntity(int id) {
        User user = storage.getEntityById(id);
        if (user == null) {
            log.debug("Не найден пользователь по id - {}", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }
}
