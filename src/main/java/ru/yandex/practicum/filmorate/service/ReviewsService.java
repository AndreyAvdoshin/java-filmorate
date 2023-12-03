package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
public class ReviewsService implements ReviewsStorage {

    ReviewsStorage storageReviews;
    Storage<User> userStorage;
    Storage<Film> filmStorage;


    @Autowired
    public ReviewsService(ReviewsStorage storageReviews, Storage<User> userStorage, Storage<Film> filmStorage) {
        this.storageReviews = storageReviews;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public List<Review> getAllReview(int count) {
        return storageReviews.getAllReview(count);
    }

    public Review create(Review entity) {
        User user = userStorage.getEntityById(entity.getUserId());
        Film film = filmStorage.getEntityById(entity.getFilmId());
        if (user != null && film != null) {
            return storageReviews.create(entity);
        }
        throw new NotFoundException("id не найден");
    }

    public Review update(Review entity) {
        User user = userStorage.getEntityById(entity.getUserId());
        Film film = filmStorage.getEntityById(entity.getFilmId());
        if (user != null && film != null) {
            return storageReviews.update(entity);
        }
        throw new NotFoundException("id не найден");
    }

    public Review getEntityById(int id) {
        if (storageReviews.getEntityById(id) != null) {
            return storageReviews.getEntityById(id);
        }
        throw new NotFoundException("id не найден");
    }

    public void delete(int id) {
        if (storageReviews.getEntityById(id) != null) {
            storageReviews.delete(id);
        } else {
            throw new NotFoundException("id не найден");
        }
    }

    public List<Review> getReviewByFilmId(int id, int count) {
        Film film = filmStorage.getEntityById(id);
        if (film != null) {
            return storageReviews.getReviewByFilmId(id, count);
        }
        throw new NotFoundException("id не найден");
    }

    public void addLike(int id) {
        if (storageReviews.getEntityById(id) != null) {
            storageReviews.addLike(id);
        } else {
            throw new NotFoundException("id не найден");
        }
    }

    public void addDislike(int id) {
        if (storageReviews.getEntityById(id) != null) {
            storageReviews.addDislike(id);
        } else {
            throw new NotFoundException("id не найден");
        }
    }

    public void deleteLike(int id) {
        if (storageReviews.getEntityById(id) != null) {
            storageReviews.deleteLike(id);
        } else {
            throw new NotFoundException("id не найден");
        }
    }

    public void deleteDislike(int id) {
        if (storageReviews.getEntityById(id) != null) {
            storageReviews.deleteDislike(id);
        } else {
            throw new NotFoundException("id не найден");
        }
    }
}
