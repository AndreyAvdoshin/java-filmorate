package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ReviewsDbStorage implements ReviewsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> getAllReview(int count) {
        log.info("Запрос на все отзывы");
        String sql = "SELECT * FROM reviews " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return new ArrayList<>(jdbcTemplate.query(sql, new ReviewMapper(), count));
    }

    @Override
    public Review create(Review entity) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(entity.toMap());

        entity.setReviewId(key.intValue());
        log.info("Создан отзыв {}", entity);
        return entity;
    }

    @Override
    public Review update(Review entity) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
        int count = jdbcTemplate.update(sql, entity.getContent(), entity.getIsPositive(), entity.getReviewId());
        if (count == 0) {
            throw new NotFoundException("Отзыв по id " + entity.getReviewId() + " не найден");
        }
        log.info("Обновлен отзыв по id - {}", entity.getReviewId());
        return getEntityById(entity.getReviewId());
    }

    @Override
    public Review getEntityById(int id) {
        log.info("Запрос отзыва по id - {}", id);
        String sql = "SELECT * FROM reviews WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ReviewMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
    }

    public void delete(int id) {
        log.info("Пользователь удаляет отзыв -  {} у фильма", id);
        jdbcTemplate.update("DELETE reviews WHERE id = ?", id);
    }

    @Override
    public List<Review> getReviewByFilmId(int id, int count) {
        log.info("Запрос отзывов по фильму");
        String sql = "SELECT * FROM reviews " +
                "WHERE film_id = ?" +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return new ArrayList<>(jdbcTemplate.query(sql, new ReviewMapper(), id, count));
    }

    @Override
    public void addLike(int id) {
        log.info("Пользователь добавляет лайк отзыву -  {}", id);
        try {
            jdbcTemplate.update("UPDATE reviews SET useful = useful+1 where id = ?", id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
    }

    @Override
    public void addDislike(int id) {
        log.info("Пользователь добавляет дизлайк отзыву -  {}", id);
        try {
            jdbcTemplate.update("UPDATE reviews SET useful = useful-1 where id = ?", id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
    }

    @Override
    public void deleteLike(int id) {
        log.info("Пользователь удаляет лайк отзыву -  {}", id);
        try {
            jdbcTemplate.update("UPDATE reviews SET useful = useful-1 where id = ?", id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
    }

    @Override
    public void deleteDislike(int id) {
        log.info("Пользователь удаляет дизлайк отзыву -  {}", id);
        try {
            jdbcTemplate.update("UPDATE reviews SET useful = useful+1 where id = ?", id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
    }
}
