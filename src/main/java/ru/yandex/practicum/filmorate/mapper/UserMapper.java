package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.stream.Collectors;

public class UserMapper implements RowMapper<User> {

    private final FriendStorage friendStorage;

    public UserMapper(FriendStorage friendStorage) {
        this.friendStorage = friendStorage;
    }

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(friendStorage.getFriendsId(resultSet.getInt("id")))
//                .friends(friendStorage.getFriends(resultSet.getInt("id"))
//                        .stream()
//                        .map(Entity::getId)
//                        .collect(Collectors.toSet()))
                .build();
        user.setId(resultSet.getInt("id"));
        return user;
    }

}
