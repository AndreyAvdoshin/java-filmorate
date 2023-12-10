package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable int id) {
        return service.getEntity(id);
    }

    @PostMapping()
    public Review create(@Valid @RequestBody Review review) {
        return service.create(review);
    }

    @PutMapping()
    public Review update(@Valid @RequestBody @NonNull Review review) {
        return service.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @GetMapping()
    public List<Review> getReviewsByFilmId(@RequestParam(required = false) Integer filmId,
                                          @RequestParam(defaultValue = "10") int count) {
        if (filmId == null) {
            return service.getReviewsWithQueryParams(count);
        } else if (filmId <= 0) {
            throw new IncorrectParameterException("filmId");
        }
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        return service.getReviewsWithQueryParams(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        if (id <= 0) {
            throw new IncorrectParameterException("id");
        } else if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        }
        service.addReaction(id, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    void addDislike(@PathVariable int id, @PathVariable int userId) {
        if (id <= 0) {
            throw new IncorrectParameterException("id");
        } else if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        }
        service.addReaction(id, userId, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    void deleteLike(@PathVariable int id, @PathVariable int userId) {
        if (id <= 0) {
            throw new IncorrectParameterException("id");
        } else if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        }
        service.deleteReaction(id, userId, true);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    void deleteDislike(@PathVariable int id, @PathVariable int userId) {
        if (id <= 0) {
            throw new IncorrectParameterException("id");
        } else if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        }
        service.deleteReaction(id, userId, false);
    }

}

