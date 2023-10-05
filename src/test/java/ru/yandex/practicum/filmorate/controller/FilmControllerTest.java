package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Controller<Film> filmController;

    Film film;

    @BeforeEach
    public void setUp() {
        film = new Film("Новый фильм", "Описание",
                LocalDate.of(2000, 1, 1), 100);
    }

    @Test
    void shouldGetFilms() {
        filmController.create(film);

        Film film2 = new Film("Другой фильм", "Другое описание",
                LocalDate.of(2001, 2, 2), 90);

        filmController.create(film2);

        Assertions.assertNotNull(filmController.get(), "Список фильмов пустой");
        Assertions.assertEquals(2, filmController.get().size(), "Запрос всех фильмов возвращается неверно");
    }

    @Test
    void shouldCreateFilmAndGet200() throws Exception {
        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Новый фильм"))
                .andExpect(jsonPath("$.description").value("Описание"))
                .andExpect(jsonPath("$.releaseDate").value("2000-01-01"))
                .andExpect(jsonPath("$.duration").value("100"));
    }

    @Test
    void shouldUpdateFilmAndGet200() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        film.setId(1);
        film.setDescription("Иное описание фильма");

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Новый фильм"))
                .andExpect(jsonPath("$.description").value("Иное описание фильма"))
                .andExpect(jsonPath("$.releaseDate").value("2000-01-01"))
                .andExpect(jsonPath("$.duration").value("100"));
    }

    @Test
    void shouldGet400WhenNameIsNull() throws Exception {
        film = new Film("", "Описание",
                LocalDate.of(2000, 1, 1), 100);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet400WhenDescriptionIsNull() throws Exception {
        film = new Film("Название", "",
                LocalDate.of(2000, 1, 1), 100);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet500WhenUpdateIncorrectId() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        film.setId(9999);
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldGet400WhenDescriptionTooLong() throws Exception {
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. " +
                "о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet500WhenReleaseDateEarler1895() throws Exception {
        film = new Film("Неправильный фильм",
                "Описание фильма с неправильной датой",
                LocalDate.of(1001, 2, 2), 90);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldGet400WhenDurationIsNegative() throws Exception {
        film = new Film("Неправильный фильм",
                "Описание фильма с неправильной продолжительностью",
                LocalDate.of(2001, 2, 2), -90);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }
}