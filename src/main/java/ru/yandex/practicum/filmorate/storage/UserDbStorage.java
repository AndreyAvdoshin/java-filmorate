package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Slf4j
@Component("UserDBStorage")
public class UserDbStorage extends Storage<User> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(user.toMap());

        user.setId(key.intValue());
        log.info("Сохранен пользоваатель: {}", user);
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
    public User getEntityById(int id) {
        log.info("Запрос пользоваателя по id - {}", id);
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, this::mapRowToUser);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User не найден по id - " + id);
        }
    }

    public User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(new HashSet<>())
                .build();
        user.setId(resultSet.getInt("id"));
        return user;
    }
}
