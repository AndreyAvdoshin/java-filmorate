package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ReviewsDbStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
public class ReviewsService extends BaseService<Review> {

    ReviewsDbStorage storage;
    Storage<User> userStorage;
    Storage<Film> filmStorage;

    public ReviewsService(ReviewsDbStorage storage, Storage<User> userStorage, Storage<Film> filmStorage) {
        super(storage);
        this.storage = storage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<Review> getAllReview(int count) {
        return storage.getAllReviewLimitCount(count);
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

    public Review getEntityById(int id) {
        return storage.getEntityById(id);
    }

    public List<Review> getReviewByFilmId(int id, int count) {
        filmStorage.getEntityById(id);
        return storage.getReviewByFilmId(id, count);
    }

    public void addLike(int id, int userId) {
        check(id, userId);
        storage.addLike(id, userId);
    }

    public void addDislike(int id, int userId) {
        check(id, userId);
        storage.addDislike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        check(id, userId);
        storage.deleteLike(id, userId);
    }

    public void deleteDislike(int id, int userId) {
        check(id, userId);
        storage.deleteDislike(id, userId);
    }

    private void check(int id, int userId) {
        storage.getEntityById(id);
        userStorage.getEntityById(userId);
    }
}
