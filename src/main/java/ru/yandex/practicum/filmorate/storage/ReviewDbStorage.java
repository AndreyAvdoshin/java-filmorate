package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UniqueViolatedException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Component
@Slf4j
public class ReviewDbStorage implements Storage<Review> {

    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> get() {
        log.info("Запрос всех отзывов");
        String sql = "SELECT r.*, " +
                "COUNT(*) FILTER (WHERE rl.is_like = true) - COUNT(*) FILTER (WHERE rl.is_like = false) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.ID = rl.REVIEW_ID " +
                "GROUP BY r.ID, r.content, r.is_positive, r.user_id, r.film_id ";
        return jdbcTemplate.query(sql, new ReviewMapper());
    }

    public List<Review> getAllReviewLimitCount(int count) {
        log.info("Запрос всех отзывов с лимитом - {}", count);
        String sql = "SELECT r.*, " +
                "COUNT(*) FILTER (WHERE rl.is_like = true) - COUNT(*) FILTER (WHERE rl.is_like = false) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.ID = rl.REVIEW_ID " +
                "GROUP BY r.ID, r.content, r.is_positive, r.user_id, r.film_id " +
                "ORDER BY useful DESC LIMIT ?";
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
        String sql = "SELECT r.*, " +
                "COUNT(*) FILTER (WHERE rl.is_like = true) - COUNT(*) FILTER (WHERE rl.is_like = false) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.ID = rl.REVIEW_ID " +
                "WHERE r.id = ?" +
                "GROUP BY r.ID, r.content, r.is_positive, r.user_id, r.film_id ";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ReviewMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Отзыв по id " + id + " не найден");
        }
    }

    @Override
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
        String sql = "SELECT r.*, " +
                "COUNT(*) FILTER (WHERE rl.is_like = true) - COUNT(*) FILTER (WHERE rl.is_like = false) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.ID = rl.REVIEW_ID " +
                "WHERE film_id = ? " +
                "GROUP BY r.ID, r.content, r.is_positive, r.user_id, r.film_id " +
                "ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, new ReviewMapper(), id, count);
    }

    public void addLike(int id, int userId) {
        log.info("Пользователь - {} добавляет лайк отзыву -  {}", userId, id);
        String sql = "INSERT INTO review_likes (user_id, review_id, is_like) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, userId, id, true);
        } catch (DuplicateKeyException e) {
            throw new UniqueViolatedException("Лайк от пользователя - " + userId + " уже поставлен отзыву - " + id);
        }
    }

    public void addDislike(int id, int userId) {
        log.info("Пользователь - {} добавляет дизлайк отзыву -  {}", userId, id);
        String sql = "INSERT INTO review_likes (user_id, review_id, is_like) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, userId, id, false);
        } catch (DuplicateKeyException e) {
            throw new UniqueViolatedException("Дизайк от пользователя - " + userId + " уже поставлен отзыву - " + id);
        }
    }

    public void deleteLike(int id, int userId) {
        log.info("Пользователь - {} удаляет лайк отзыву -  {}", userId, id);
        jdbcTemplate.update("DELETE review_likes WHERE user_id = ? AND review_id = ? AND is_like = ?",
                userId, id, true);
    }

    public void deleteDislike(int id, int userId) {
        log.info("Пользователь - {} удаляет дизлайк отзыву -  {}", userId, id);
        jdbcTemplate.update("DELETE review_likes WHERE user_id = ? AND review_id = ? AND is_like = ?",
                userId, id, false);
    }
}
