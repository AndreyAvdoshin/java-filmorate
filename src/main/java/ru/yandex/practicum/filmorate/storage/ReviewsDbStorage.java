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
public class ReviewsDbStorage implements Storage<Review> {

    private final JdbcTemplate jdbcTemplate;

    public ReviewsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> get() {
        log.info("Запрос всех отзывов");
        String sql = "SELECT * FROM reviews";
        return jdbcTemplate.query(sql, new ReviewMapper());
    }

    public List<Review> getAllReviewLimitCount(int count) {
        log.info("Запрос на все отзывы");
        String sql = "SELECT * FROM reviews " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new ReviewMapper(), count);
    }

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(review.toMap());

        review.setReviewId(key.intValue());
        log.info("Создан отзыв {}", review);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
        int count = jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        if (count == 0) {
            throw new NotFoundException("Отзыв по id " + review.getReviewId() + " не найден");
        }
        log.info("Обновлен отзыв по id - {}", review.getReviewId());
        return getEntityById(review.getReviewId());
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
        String sql = "DELETE FROM reviews WHERE id = ?";
        int count = jdbcTemplate.update(sql, id);
        if (count == 0) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
        log.info("Удален отзыв по id - {}", id);
    }

    public List<Review> getReviewByFilmId(int id, int count) {
        log.info("Запрос отзывов по фильму");
        String sql = "SELECT * FROM reviews " +
                "WHERE film_id = ?" +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new ReviewMapper(), id, count);
    }

    public void addLike(int id) {
        log.info("Пользователь добавляет лайк отзыву -  {}", id);
        try {
            jdbcTemplate.update("UPDATE reviews SET useful = useful+1 where id = ?", id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
    }

    public void addDislike(int id) {
        log.info("Пользователь добавляет дизлайк отзыву -  {}", id);
        try {
            jdbcTemplate.update("UPDATE reviews SET useful = useful-1 where id = ?", id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
    }

    public void deleteLike(int id) {
        log.info("Пользователь удаляет лайк отзыву -  {}", id);
        try {
            jdbcTemplate.update("UPDATE reviews SET useful = useful-1 where id = ?", id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
    }

    public void deleteDislike(int id) {
        log.info("Пользователь удаляет дизлайк отзыву -  {}", id);
        try {
            jdbcTemplate.update("UPDATE reviews SET useful = useful+1 where id = ?", id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
    }
}
