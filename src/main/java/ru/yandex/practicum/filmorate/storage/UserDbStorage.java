package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@Component
public class UserDbStorage implements Storage<User> {

    private final JdbcTemplate jdbcTemplate;
    private final FriendDbStorage friendStorage;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendDbStorage friendStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendStorage = friendStorage;
    }

    @Override
    public List<User> get() {
        log.info("Запрос всех пользователей");
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserMapper(friendStorage));
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(user.toMap());

        user.setId(key.intValue());
        log.info("Сохранен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ? WHERE id = ?";
        int count = jdbcTemplate.update(sql, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        if (count == 0) {
            throw new NotFoundException("Пользователь по id " + user.getId() + " не найден");
        }
        log.info("Обновлен пользователь по id - {}", user.getId());
        return user;
    }

    @Override
    public void delete(int id) {
    }

    @Override
    public User getEntityById(int id) {
        log.info("Запрос пользователя по id - {}", id);
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserMapper(friendStorage));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Пользователь не найден по id - " + id);
        }
    }

}
