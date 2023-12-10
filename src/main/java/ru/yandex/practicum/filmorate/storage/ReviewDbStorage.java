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

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        Number key = simpleJdbcInsert.executeAndReturnKey(review.toMap());

        review.setId(key.intValue());
        log.info("Создан отзыв {}", review);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
        int count = jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getId());
        if (count == 0) {
            throw new NotFoundException("Отзыв по id " + review.getId() + " не найден");
        }
        log.info("Обновлен отзыв по id - {}", review.getId());
        return getEntityById(review.getId());
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

    public List<Review> getReviewsWithQueryParams(int count) {
        log.info("Запрос всех отзывов с лимитом - {}", count);
        String sql = "SELECT r.*, " +
                "COUNT(*) FILTER (WHERE rl.is_like = true) - COUNT(*) FILTER (WHERE rl.is_like = false) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.ID = rl.REVIEW_ID " +
                "GROUP BY r.ID, r.content, r.is_positive, r.user_id, r.film_id " +
                "ORDER BY useful DESC, id ASC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new ReviewMapper(), count);
    }

    public List<Review> getReviewsWithQueryParams(int filmId, int count) {
        log.info("Запрос отзывов по фильму - {} с лимитом - {}", filmId, count);
        String sql = "SELECT r.*, " +
                "COUNT(*) FILTER (WHERE rl.is_like = true) - COUNT(*) FILTER (WHERE rl.is_like = false) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_likes rl ON r.ID = rl.REVIEW_ID " +
                "WHERE film_id = ? " +
                "GROUP BY r.ID, r.content, r.is_positive, r.user_id, r.film_id " +
                "ORDER BY useful DESC, id ASC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new ReviewMapper(), filmId, count);
    }

    public void addReaction(int id, int userId, boolean isLike) {
        log.info("Пользователь - {} добавляет реакцию - {} на отзыв - {}", userId, isLike ? "лайк" : "дизлайк", id);
        String sql = "INSERT INTO review_likes (user_id, review_id, is_like) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, userId, id, isLike);
        } catch (DuplicateKeyException e) {
            throw new UniqueViolatedException(String.format(
                    "Реакция - %s от пользователя - %d уже проставлена отзыву - %d",
                    isLike ? "лайк" : "дизлайк", userId, id));
        }
    }

    public void deleteReaction(int id, int userId, boolean isLike) {
        log.info("Пользователь - {} удаляет реакцию - {} на отзыв - {}", userId, isLike ? "лайк" : "дизлайк", id);
        jdbcTemplate.update("DELETE review_likes WHERE user_id = ? AND review_id = ? AND is_like = ?",
                userId, id, isLike);
    }
}
