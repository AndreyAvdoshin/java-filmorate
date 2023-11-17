package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendDbStorage;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    private final FriendDbStorage friendDbStorage;

    public UserMapper(FriendDbStorage friendStorage) {
        this.friendDbStorage = friendStorage;
    }

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(friendDbStorage.getFriendsId(resultSet.getInt("id")))
                .build();
        user.setId(resultSet.getInt("id"));
        return user;
    }

}
