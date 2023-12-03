package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ReviewsDbStorage;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;
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

    public Review create(Review review) {
        userStorage.getEntityById(review.getUserId());
        filmStorage.getEntityById(review.getFilmId());
        return storage.create(review);
    }

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

    public void addLike(int id) {
        storage.addLike(id);
    }

    public void addDislike(int id) {
        storage.addDislike(id);
    }

    public void deleteLike(int id) {
        storage.deleteLike(id);
    }

    public void deleteDislike(int id) {
        storage.deleteDislike(id);
    }
}
