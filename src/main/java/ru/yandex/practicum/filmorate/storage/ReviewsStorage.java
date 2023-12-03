package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewsStorage {

    List<Review> getAllReview(int count);

    Review create(Review entity);

    Review update(Review entity);

    Review getEntityById(int id);

    void delete(int id);

    List<Review> getReviewByFilmId(int id, int count);

    void addLike(int id);

    void addDislike(int id);

    void deleteLike(int id);

    void deleteDislike(int id);
}
