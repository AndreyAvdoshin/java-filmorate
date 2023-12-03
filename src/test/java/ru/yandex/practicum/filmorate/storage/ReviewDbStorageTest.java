package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(classes = {ReviewDbStorage.class, MpaDbStorage.class, GenreDbStorage.class, LikeDbStorage.class,
        FilmDbStorage.class, UserDbStorage.class, FriendDbStorage.class, DirectorDbStorage.class})
class ReviewDbStorageTest {
    private final ReviewDbStorage reviewDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    Review review1;
    Review review2;
    Review review3;

    Film film1;
    Film film2;

    User user1;
    User user2;

    @BeforeEach
    void setUp() {
        film1 = Film.builder()
                .name("Новый фильм")
                .description("Описание")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .directors(new HashSet<>())
                .build();
        film2 = Film.builder()
                .name("Другой фильм")
                .description("Другое описание")
                .releaseDate(LocalDate.of(2001, 2, 2))
                .duration(90)
                .mpa(Mpa.builder().id(1).name("Комедия").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .directors(new HashSet<>())
                .build();

        user1 = User.builder()
                .email("aaa@bbb.ccc")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        user2 = User.builder()
                .email("aaa@bbb.eee")
                .login("PasLogin")
                .name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        review1 = Review.builder()
                .content("Отзыв номер 1")
                .isPositive(true)
                .userId(1)
                .filmId(1)
                .useful(20)
                .build();
        review2 = Review.builder()
                .content("Отзыв номер 2")
                .isPositive(false)
                .userId(1)
                .filmId(2)
                .useful(0)
                .build();
        review3 = Review.builder()
                .content("Отзыв номер 3")
                .isPositive(true)
                .userId(2)
                .filmId(2)
                .useful(-3)
                .build();
    }

    @Test
    void shouldGetAllReviews() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        review1 = reviewDbStorage.create(review1);
        review2 = reviewDbStorage.create(review2);
        review3 = reviewDbStorage.create(review3);

        review1.setUseful(0);
        review2.setUseful(0);
        review3.setUseful(0);

        assertThat(reviewDbStorage.get())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(review1, review2, review3));
    }

    @Test
    void shouldGetReviewById() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        review1 = reviewDbStorage.create(review1);
        review2 = reviewDbStorage.create(review2);
        review3 = reviewDbStorage.create(review3);

        assertThat(reviewDbStorage.getEntityById(2))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(review2);
    }

    @Test
    void shouldGetReviewsWithCount2() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        review1 = reviewDbStorage.create(review1);
        review2 = reviewDbStorage.create(review2);
        review3 = reviewDbStorage.create(review3);

        review1.setUseful(0);
        review2.setUseful(0);
        review3.setUseful(0);

        assertThat(reviewDbStorage.getReviewsWithQueryParams(2))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(review1, review2));
    }

    @Test
    void shouldGetReviewsByFilmId() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        review1 = reviewDbStorage.create(review1);
        review2 = reviewDbStorage.create(review2);
        review3 = reviewDbStorage.create(review3);

        review1.setUseful(0);
        review2.setUseful(0);
        review3.setUseful(0);

        assertThat(reviewDbStorage.getReviewsWithQueryParams(2, 10))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(review2, review3));
    }

    @Test
    void shouldCreateReview() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);

        Review savedReview = reviewDbStorage.create(review1);
        assertThat(savedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(review1);
    }

    @Test
    void shouldUpdateReview() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        review1 = reviewDbStorage.create(review1);

        review1.setContent("Обновленный отзыв номер 1");
        review1.setUseful(0);
        Review savedReview = reviewDbStorage.update(review1);

        assertThat(savedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(review1);
    }

    @Test
    void shouldDeleteReview() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        review1 = reviewDbStorage.create(review1);

        reviewDbStorage.delete(review1.getReviewId());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> reviewDbStorage.getEntityById(review1.getReviewId())
        );
    }

    @Test
    void shouldAddReactionLike() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        review1 = reviewDbStorage.create(review1);
        review2 = reviewDbStorage.create(review2);
        review3 = reviewDbStorage.create(review3);

        reviewDbStorage.addReaction(1, 1, true);
    }

    @Test
    void shouldAddReactionDislike() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        review1 = reviewDbStorage.create(review1);
        review2 = reviewDbStorage.create(review2);
        review3 = reviewDbStorage.create(review3);

        reviewDbStorage.addReaction(1, 1, false);
    }

    @Test
    void shouldDeleteReactionLike() {userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        review1 = reviewDbStorage.create(review1);
        review2 = reviewDbStorage.create(review2);
        review3 = reviewDbStorage.create(review3);

        reviewDbStorage.deleteReaction(1, 1, true);
    }

    @Test
    void shouldDeleteReactionDislike() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        review1 = reviewDbStorage.create(review1);
        review2 = reviewDbStorage.create(review2);
        review3 = reviewDbStorage.create(review3);

        reviewDbStorage.deleteReaction(1, 1, false);
    }
}