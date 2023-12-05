package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
public class ReviewService extends BaseService<Review> {

    ReviewDbStorage storage;
    Storage<User> userStorage;
    Storage<Film> filmStorage;

    public ReviewService(ReviewDbStorage storage, Storage<User> userStorage, Storage<Film> filmStorage) {
        super(storage);
        this.storage = storage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public Review create(Review review) {
        userStorage.getEntityById(review.getUserId());
        filmStorage.getEntityById(review.getFilmId());
        return storage.create(review);
    }

    @Override
    public Review update(Review review) {
        userStorage.getEntityById(review.getUserId());
        filmStorage.getEntityById(review.getFilmId());
        return storage.update(review);
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
