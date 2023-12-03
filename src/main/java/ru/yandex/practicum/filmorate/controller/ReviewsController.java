package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewsController extends Controller<Review> {

    private final ReviewsService service;

    public ReviewsController(ReviewsService service) {
        super(service);
        this.service = service;
    }

    @Override
    @GetMapping("/all")
    public List<Review> get() {
        return service.get();
    }

//    @PostMapping
//    public Review create(@Valid @RequestBody @NonNull Review entity) {
//        return service.create(entity);
//    }

//    @PutMapping
//    public Review update(@Valid @RequestBody @NonNull Review entity) {
//        return service.update(entity);
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable int id) {
//        service.delete(id);
//    }
//

    @GetMapping
    public List<Review> getReviewByFilmId(@RequestParam(required = false) Integer filmId, @RequestParam(defaultValue = "10") int count) {
        if (filmId == null) {
            return service.getAllReview(count);
        }
        return service.getReviewByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id) {
        service.addLike(id);
    }

    @PutMapping("/{id}/dislike/{userId}")
    void addDislike(@PathVariable int id) {
        service.addDislike(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    void deleteLike(@PathVariable int id) {
        service.deleteLike(id);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    void deleteDislike(@PathVariable int id) {
        service.deleteDislike(id);
    }

}

