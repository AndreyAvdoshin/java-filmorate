package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    FilmController filmController;
    @Autowired
    DirectorController directorController;

    Film film;
    Film film2;
    Film film3;
    User user;
    Director director1;
    Director director2;

    @BeforeEach
    public void setUp() {
        director1 = Director.builder().name("Новый режиссер").build();
        director2 = Director.builder().name("Старый режиссер").build();

        film = Film.builder()
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
                .directors(Set.of(director1, director2))
                .build();

        film3 = Film.builder()
                .name("Третий фильм")
                .description("Третье описание")
                .releaseDate(LocalDate.of(1998, 12, 30))
                .duration(90)
                .mpa(Mpa.builder().id(1).name("Боевик").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .directors(Set.of(director1))
                .build();

        user = User.builder()
                .email("aaa@bbb.ccc")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void shouldGetFilms() {
        filmController.create(film);

        Film film2 = Film.builder()
                .name("Другой фильм")
                .description("Другое описание")
                .releaseDate(LocalDate.of(2001, 2, 2))
                .duration(90)
                .mpa(Mpa.builder().id(1).name("Комедия").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .directors(new HashSet<>())
                .build();

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
    void shouldGet200WhenDeleteFilm() throws Exception {
        filmController.create(film);

        mockMvc.perform(delete("/films/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGet404WhenDeleteIncorrectId() throws Exception {
        filmController.create(film);

        mockMvc.perform(delete("/films/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGet400WhenNameIsNull() throws Exception {
        film = Film.builder()
                .name("")
                .description("Описание")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .mpa(Mpa.builder().id(1).name("Комедия").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet400WhenDescriptionIsNull() throws Exception {
        film = Film.builder()
                .name("Название")
                .description("")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .mpa(Mpa.builder().id(1).name("Комедия").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();

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
                .andExpect(status().isNotFound());
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
        film = Film.builder()
                .name("Неправильный фильм")
                .description("Описание фильма с неправильной датой")
                .releaseDate(LocalDate.of(1001, 2, 2))
                .duration(120)
                .mpa(Mpa.builder().id(1).name("Комедия").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet400WhenDurationIsNegative() throws Exception {
        film = Film.builder()
                .name("Неправильный фильм")
                .description("ООписание фильма с неправильной продолжительностью")
                .releaseDate(LocalDate.of(2001, 2, 2))
                .duration(-90)
                .mpa(Mpa.builder().id(1).name("Комедия").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddLikeFilmByUser() throws Exception {
        film.setId(1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films/1/like/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGet404WhenIncorrectParameterPutLike() throws Exception {
        mockMvc.perform(put("/films/-1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/films/1/like/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteLikeFilmByUser() throws Exception {
        film.setId(1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/films/1/like/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void shouldGetLikedFilm() throws Exception {
        film.setId(1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films/1/like/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Новый фильм"))
                .andExpect(jsonPath("$.description").value("Описание"))
                .andExpect(jsonPath("$.releaseDate").value("2000-01-01"))
                .andExpect(jsonPath("$.duration").value("100"))
                .andExpect(jsonPath("$.likesCount").value("1"));
    }

    @Test
    void shouldGetDirectorFilms() {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setDirectors(Set.of(director1));
        filmController.create(film);
        film2.setDirectors(Set.of(director1, director2));
        filmController.create(film2);
        film3.setDirectors(Set.of(director1));
        filmController.create(film3);

        Assertions.assertNotNull(filmController.getDirectorFilms(director1.getId(),"year"),
                "Список фильмов режиссера пустой");
        Assertions.assertEquals(3, filmController.getDirectorFilms(director1.getId(),"year").size(),
                "Запрос всех фильмов режиссера возвращается неверно");
    }

    @Test
    void shouldGetDirectorFilmsWithSortByYear() {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setDirectors(Set.of(director1));
        film = filmController.create(film);
        film2.setDirectors(Set.of(director1, director2));
        film2 = filmController.create(film2);
        film3.setDirectors(Set.of(director1));
        film3 = filmController.create(film3);

        List<Film> sortedFilmsByYear = Stream.of(film, film2, film3)
                .sorted(Comparator.comparing(Film::getReleaseDate).thenComparing(Film::getId))
                .collect(Collectors.toList());

        Assertions.assertNotNull(filmController.getDirectorFilms(director1.getId(),"year"),
                "Список фильмов режиссера пустой");
        Assertions.assertEquals(sortedFilmsByYear,
                filmController.getDirectorFilms(director1.getId(),"year"),
                "Список всех фильмов режиссера возвращает неверно отсортированный список по годам");
    }

    @Test
    void shouldGetDirectorFilmsWithSortByLikes() {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setDirectors(Set.of(director1));
        film = filmController.create(film);
        film2.setDirectors(Set.of(director1, director2));
        film2 = filmController.create(film2);
        film3.setDirectors(Set.of(director1));
        film3 = filmController.create(film3);

        List<Film> sortedFilmsByLikes = Stream.of(film, film2, film3)
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed().thenComparing(Film::getId))
                .collect(Collectors.toList());

        Assertions.assertNotNull(filmController.getDirectorFilms(director1.getId(),"likes"),
                "Список фильмов режиссера пустой");
        Assertions.assertEquals(sortedFilmsByLikes,
                filmController.getDirectorFilms(director1.getId(),"likes"),
                "Список всех фильмов режиссера возвращает неверно отсортированный список по лайкам");
    }

    @Test
    void shouldGet200WhenGetDirectorFilmsWithSortByYear() throws Exception {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setDirectors(Set.of(director1));
        filmController.create(film);
        film2.setDirectors(Set.of(director1, director2));
        filmController.create(film2);
        film3.setDirectors(Set.of(director1));
        filmController.create(film3);

        mockMvc.perform(get("/films/director/1")
                .queryParam("sortBy", "year")
        ).andExpect(status().isOk());
    }

    @Test
    void shouldGet200WhenGetDirectorFilmsWithSortByLikes() throws Exception {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setDirectors(Set.of(director1));
        filmController.create(film);
        film2.setDirectors(Set.of(director1, director2));
        filmController.create(film2);
        film3.setDirectors(Set.of(director1));
        filmController.create(film3);

        mockMvc.perform(get("/films/director/1")
                .queryParam("sortBy", "likes")
        ).andExpect(status().isOk());
    }

    @Test
    void shouldGet404WhenUnknownDirectorIdGetDirectorFilms() throws Exception {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setDirectors(Set.of(director1));
        filmController.create(film);
        film2.setDirectors(Set.of(director1, director2));
        filmController.create(film2);
        film3.setDirectors(Set.of(director1));
        filmController.create(film3);

        mockMvc.perform(get("/films/director/9999")).andExpect(status().isNotFound());
    }

    @Test
    void shouldGetFilmsByQueryFieldWithTitleAndDirectorCategoryAndGet200() throws Exception {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setName("Старый фильм 1");
        film.setDirectors(Set.of(director2));
        filmController.create(film);
        film2.setName("ОбНовленный фильм 2");
        film2.setDirectors(Set.of(director2));
        filmController.create(film2);
        film3.setName("Старый фильм 3");
        film3.setDirectors(Set.of(director1));
        filmController.create(film3);

        mockMvc.perform(get("/films/search?query=нов&by=director,title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("ОбНовленный фильм 2"))
                .andExpect(jsonPath("$[0].description").value("Другое описание"))
                .andExpect(jsonPath("$[0].releaseDate").value("2001-02-02"))
                .andExpect(jsonPath("$[0].duration").value(90))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].name").value("Старый фильм 3"))
                .andExpect(jsonPath("$[1].description").value("Третье описание"))
                .andExpect(jsonPath("$[1].releaseDate").value("1998-12-30"))
                .andExpect(jsonPath("$[1].duration").value(90));
    }

    @Test
    void shouldGetFilmsByQueryFieldWithTitleCategoryAndGet200() throws Exception {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setName("Новый фильм 1");
        film.setDirectors(Set.of(director1));
        filmController.create(film);
        film2.setName("Иной классный фильм 2");
        film2.setDirectors(Set.of(director1, director2));
        filmController.create(film2);
        film3.setName("ОбНовленный фильм 3");
        film3.setDirectors(Set.of(director1));
        filmController.create(film3);

        mockMvc.perform(get("/films/search?query=ной класс&by=title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Иной классный фильм 2"))
                .andExpect(jsonPath("$[0].description").value("Другое описание"))
                .andExpect(jsonPath("$[0].releaseDate").value("2001-02-02"))
                .andExpect(jsonPath("$[0].duration").value(90));
    }

    @Test
    void shouldGetFilmsByQueryFieldWithDirectorCategoryAndGet200() throws Exception {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setName("Новый фильм 1");
        film.setDirectors(Set.of(director1));
        filmController.create(film);
        film2.setName("Обновленный фильм 2");
        film2.setDirectors(Set.of(director1));
        filmController.create(film2);
        film3.setName("ОбНовленный фильм 3");
        film3.setDirectors(Set.of(director2));
        filmController.create(film3);

        mockMvc.perform(get("/films/search?query=тарый&by=director"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].name").value("ОбНовленный фильм 3"))
                .andExpect(jsonPath("$[0].description").value("Третье описание"))
                .andExpect(jsonPath("$[0].releaseDate").value("1998-12-30"))
                .andExpect(jsonPath("$[0].duration").value(90));
    }

    @Test
    void shouldGet400WhenIncorrectParameterGetFilmsByQueryFieldWithUnknownCategory() throws Exception {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setName("Новый фильм 1");
        film.setDirectors(Set.of(director1));
        filmController.create(film);
        film2.setName("Обновленный фильм 2");
        film2.setDirectors(Set.of(director1, director2));
        filmController.create(film2);
        film3.setName("ОбНовленный фильм 3");
        film3.setDirectors(Set.of(director1));
        filmController.create(film3);

        mockMvc.perform(get("/films/search?query=бнов&by=failed,title"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGet400WhenIncorrectParameterGetFilmsByQueryFieldWithForbiddenSymbol() throws Exception {
        director1 = directorController.create(director1);
        director2 = directorController.create(director2);

        film.setName("Новый фильм 1");
        film.setDirectors(Set.of(director1));
        filmController.create(film);
        film2.setName("Обновленный фильм 2");
        film2.setDirectors(Set.of(director1, director2));
        filmController.create(film2);
        film3.setName("ОбНовленный фильм 3");
        film3.setDirectors(Set.of(director1));
        filmController.create(film3);

        mockMvc.perform(get("/films/search?query=бно%в&by=title"))
                .andExpect(status().isBadRequest());
    }
}