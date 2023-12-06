package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ReviewController reviewController;
    @Autowired
    UserController userController;
    @Autowired
    FilmController filmController;

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
    void shouldGetReviewsByFilmIdWithLimitAndGet200() throws Exception {
        userController.create(user1);
        userController.create(user2);
        filmController.create(film1);
        filmController.create(film2);
        reviewController.create(review1);
        reviewController.create(review2);
        reviewController.create(review3);

        mockMvc.perform(get("/reviews?filmId=2&count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].reviewId").value(2))
                .andExpect(jsonPath("$[0].content").value("Отзыв номер 2"))
                .andExpect(jsonPath("$[0].isPositive").value(false))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].filmId").value(2))
                .andExpect(jsonPath("$[0].useful").value(0));
    }

    @Test
    void shouldGetFirst10ReviewsByFilmIdWithoutLimitAndGet200() throws Exception {
        userController.create(user1);
        userController.create(user2);
        filmController.create(film1);
        filmController.create(film2);
        reviewController.create(review1);
        reviewController.create(review2);
        reviewController.create(review3);

        mockMvc.perform(get("/reviews?filmId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].reviewId").value(2))
                .andExpect(jsonPath("$[0].content").value("Отзыв номер 2"))
                .andExpect(jsonPath("$[0].isPositive").value(false))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].filmId").value(2))
                .andExpect(jsonPath("$[0].useful").value(0))
                .andExpect(jsonPath("$[1].reviewId").value(3))
                .andExpect(jsonPath("$[1].content").value("Отзыв номер 3"))
                .andExpect(jsonPath("$[1].isPositive").value(true))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].filmId").value(2))
                .andExpect(jsonPath("$[1].useful").value(0));
    }

    @Test
    void shouldGetAllReviewsWithoutFilmIdWithLimitAndGet200() throws Exception {
        userController.create(user1);
        userController.create(user2);
        filmController.create(film1);
        filmController.create(film2);
        reviewController.create(review1);
        reviewController.create(review2);
        reviewController.create(review3);

        mockMvc.perform(get("/reviews?count=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].reviewId").value(1))
                .andExpect(jsonPath("$[0].content").value("Отзыв номер 1"))
                .andExpect(jsonPath("$[0].isPositive").value(true))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].filmId").value(1))
                .andExpect(jsonPath("$[0].useful").value(0))
                .andExpect(jsonPath("$[1].reviewId").value(2))
                .andExpect(jsonPath("$[1].content").value("Отзыв номер 2"))
                .andExpect(jsonPath("$[1].isPositive").value(false))
                .andExpect(jsonPath("$[1].userId").value(1))
                .andExpect(jsonPath("$[1].filmId").value(2))
                .andExpect(jsonPath("$[1].useful").value(0));
    }

    @Test
    void shouldGetFirst10ReviewsWithoutFilmIdAndLimitAndGet200() throws Exception {
        userController.create(user1);
        userController.create(user2);
        filmController.create(film1);
        filmController.create(film2);
        reviewController.create(review1);
        reviewController.create(review2);
        reviewController.create(review3);

        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewId").value(1))
                .andExpect(jsonPath("$[0].content").value("Отзыв номер 1"))
                .andExpect(jsonPath("$[0].isPositive").value(true))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].filmId").value(1))
                .andExpect(jsonPath("$[0].useful").value(0))
                .andExpect(jsonPath("$[1].reviewId").value(2))
                .andExpect(jsonPath("$[1].content").value("Отзыв номер 2"))
                .andExpect(jsonPath("$[1].isPositive").value(false))
                .andExpect(jsonPath("$[1].userId").value(1))
                .andExpect(jsonPath("$[1].filmId").value(2))
                .andExpect(jsonPath("$[1].useful").value(0))
                .andExpect(jsonPath("$[2].reviewId").value(3))
                .andExpect(jsonPath("$[2].content").value("Отзыв номер 3"))
                .andExpect(jsonPath("$[2].isPositive").value(true))
                .andExpect(jsonPath("$[2].userId").value(2))
                .andExpect(jsonPath("$[2].filmId").value(2))
                .andExpect(jsonPath("$[2].useful").value(0));
    }

    @Test
    void shouldGet404WhenIncorrectFilmIdParamGetReviews() throws Exception {
        userController.create(user1);
        userController.create(user2);
        filmController.create(film1);
        filmController.create(film2);
        reviewController.create(review1);
        reviewController.create(review2);
        reviewController.create(review3);

        mockMvc.perform(get("/reviews&filmId=-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGet404WhenIncorrectCountParamGetReviews() throws Exception {
        userController.create(user1);
        userController.create(user2);
        filmController.create(film1);
        filmController.create(film2);
        reviewController.create(review1);
        reviewController.create(review2);
        reviewController.create(review3);

        mockMvc.perform(get("/reviews&count=-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateReviewAndGet200() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.content").value("Отзыв номер 1"))
                .andExpect(jsonPath("$.isPositive").value(true))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.filmId").value(1))
                .andExpect(jsonPath("$.useful").value(20));
    }

    @Test
    void shouldGet400WhenContentIsBlank() throws Exception {
        Review review = Review.builder()
                .content("")
                .isPositive(true)
                .userId(1)
                .filmId(1)
                .useful(20)
                .build();

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet400WhenIsPositiveIsNull() throws Exception {
        Review review = Review.builder()
                .content("Отзыв номер 1")
                .userId(1)
                .filmId(1)
                .useful(20)
                .build();

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateReviewAndGet200() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk());

        review1.setReviewId(1);
        review1.setContent("Обновленный отзыв номер 1");

        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1))
                .andExpect(jsonPath("$.content").value("Обновленный отзыв номер 1"))
                .andExpect(jsonPath("$.isPositive").value(true))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.filmId").value(1))
                .andExpect(jsonPath("$.useful").value(0));
    }

    @Test
    void shouldGet404WhenUpdateIncorrectId() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk());

        review1.setReviewId(9999);
        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGet200WhenDeleteReview() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGet404WhenDeleteIncorrectId() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/reviews/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGet200WhenAddLike() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/reviews/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGet400WhenIncorrectParameterAddLike() throws Exception {
        mockMvc.perform(put("/reviews/-1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/reviews/1/like/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet200WhenDeleteLike() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/reviews/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/reviews/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGet400WhenIncorrectParameterDeleteLike() throws Exception {
        mockMvc.perform(put("/reviews/-1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/reviews/1/like/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet200WhenAddDislike() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/reviews/1/dislike/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGet400WhenIncorrectParameterAddDislike() throws Exception {
        mockMvc.perform(put("/reviews/-1/dislike/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/reviews/1/dislike/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet200WhenDeleteDislike() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/reviews/1/dislike/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/reviews/1/dislike/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGet400WhenIncorrectParameterDeleteDislike() throws Exception {
        mockMvc.perform(put("/reviews/-1/dislike/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/reviews/1/dislike/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}