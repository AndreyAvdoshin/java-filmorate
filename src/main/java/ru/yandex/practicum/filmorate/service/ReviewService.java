package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.Instant;
import java.util.List;

@Service
public class ReviewService extends BaseService<Review> {

    private final ReviewDbStorage storage;
    private final Storage<User> userStorage;
    private final Storage<Film> filmStorage;
    private final FeedDbStorage feedStorage;

    public ReviewService(ReviewDbStorage storage, Storage<User> userStorage,
                         Storage<Film> filmStorage, FeedDbStorage feedStorage) {
        super(storage);
        this.storage = storage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.feedStorage = feedStorage;
    }

    @Override
    public Review create(Review review) {
        userStorage.getEntityById(review.getUserId());
        filmStorage.getEntityById(review.getFilmId());
        feedStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .entityId(review.getFilmId())
                .build());
        return storage.create(review);
    }

    @Override
    public Review update(Review review) {
        userStorage.getEntityById(review.getUserId());
        filmStorage.getEntityById(review.getFilmId());
        review = storage.update(review);
        feedStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .entityId(review.getFilmId())
                .build());
        return review;
    }

    @Override
    public void delete(int id) {
        feedStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(storage.getEntityById(id).getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .entityId(id)
                .build());
        storage.delete(id);
    }

    public List<Review> getReviewsWithQueryParams(int count) {
        return storage.getReviewsWithQueryParams(count);
    }

    public List<Review> getReviewsWithQueryParams(int filmId, int count) {
        filmStorage.getEntityById(filmId);
        return storage.getReviewsWithQueryParams(filmId, count);
    }

    public void addReaction(int id, int userId, boolean isLike) {
        checkReviewAndUser(id, userId);
        storage.addReaction(id, userId, isLike);
    }

    public void deleteReaction(int id, int userId, boolean isLike) {
        checkReviewAndUser(id, userId);
        storage.deleteReaction(id, userId, isLike);
    }

    private void checkReviewAndUser(int id, int userId) {
        storage.getEntityById(id);
        userStorage.getEntityById(userId);
    }
}
