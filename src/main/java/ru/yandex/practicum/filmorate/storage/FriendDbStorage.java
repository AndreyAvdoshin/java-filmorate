package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UniqueViolatedException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Primary
@Component
public class FriendDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createFriendship(int firstUserId, int secondUserId) {
        log.info("Пользователь - {} добавляем в друзья пользователя - {}", firstUserId, secondUserId);
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, firstUserId, secondUserId);
        } catch (DuplicateKeyException e) {
            throw new UniqueViolatedException("Пользователь - " + firstUserId +
                    " уже дружит с пользователем - " + secondUserId);
        }
    }

    public void deleteFriend(int firstUserId, int secondUserId) {
        log.info("Пользователь - {} удаляет из друзей пользователя - {}", firstUserId, secondUserId);
        String sql = "DELETE friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, firstUserId, secondUserId);
    }

    public List<User> getFriends(int id) {
        log.info("Запрос друзей пользователя - {}", id);
        String sql = "SELECT * FROM users WHERE id IN (SELECT friend_id FROM friendship WHERE user_id = ?)";
        return jdbcTemplate.query(sql, new UserMapper(), id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        log.info("Запрос общих друзей у пользователей {} и {}", id, otherId);
        String sql = "SELECT * FROM USERS u WHERE ID IN (" +
                "SELECT f.FRIEND_ID FROM FRIENDSHIP f " +
                "INNER JOIN FRIENDSHIP f2 ON f.FRIEND_ID = f2.FRIEND_ID " +
                "WHERE f.USER_ID = ? AND f2.USER_ID = ?)";
        try {
            return jdbcTemplate.query(sql, new UserMapper(), id, otherId);
        } catch (IncorrectResultSizeDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Set<Integer> getFriendsId(int id) {
        String sql = "SELECT friend_id FROM friendship WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), id));
    }
}
