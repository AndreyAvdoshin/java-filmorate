package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {MpaDbStorage.class, GenreDbStorage.class, LikeDbStorage.class, FilmDbStorage.class,
        UserDbStorage.class, FriendDbStorage.class, DirectorDbStorage.class})
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDBStorage;
    private final GenreDbStorage genreDBStorage;
    private final LikeDbStorage likeStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    Film film;
    Film updatedFilm;
    User user;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Новый фильм")
                .description("Описание")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .mpa(mpaDBStorage.getEntityById(1))
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .directors(new HashSet<>())
                .build();

        updatedFilm = Film.builder()
                .name("Другой фильм")
                .description("Другое описание")
                .releaseDate(LocalDate.of(2010, 1, 1))
                .duration(101)
                .mpa(mpaDBStorage.getEntityById(2))
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .directors(new HashSet<>())
                .build();

        user = User.builder()
                .email("aaa@bbb.ccc")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void shouldCreateFilm() {
        Film savedFilm = filmDbStorage.create(film);
        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    void shouldUpdateFilm() {
        film = filmDbStorage.create(film);
        updatedFilm.setId(film.getId());
        Film savedFilm = filmDbStorage.update(updatedFilm);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedFilm);
    }

    @Test
    void shouldAddLikeFilmByUser() {
        film = filmDbStorage.create(film);
        user = userDbStorage.create(user);

        likeStorage.addLike(film.getId(), user.getId());
        Set<Integer> likes = likeStorage.getLikesByFilmId(film.getId());

        assertThat(likes).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(likes);
    }

    @Test
    void shouldDeleteLikeFilmByUser() {
        film = filmDbStorage.create(film);
        user = userDbStorage.create(user);

        likeStorage.addLike(film.getId(), user.getId());
        likeStorage.deleteLike(film.getId(), user.getId());
        Set<Integer> likes = likeStorage.getLikesByFilmId(film.getId());

        assertThat(likes).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(new HashSet<>());
    }

    @Test
    void shouldGetRatedFilms() {
        User newUser = User.builder()
                .email("nnn@eee.www")
                .login("user")
                .name("user")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();

        film = filmDbStorage.create(film);
        updatedFilm = filmDbStorage.create(updatedFilm);
        user = userDbStorage.create(user);
        newUser = userDbStorage.create(newUser);

        List<Film> likes = likeStorage.getRatedFilms(10);
        assertThat(likes).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film, updatedFilm));

        likeStorage.addLike(updatedFilm.getId(), user.getId());
        likeStorage.addLike(updatedFilm.getId(), newUser.getId());
        updatedFilm.setLike(user.getId());
        updatedFilm.setLike(newUser.getId());

        likes = likeStorage.getRatedFilms(10);

        assertThat(likes).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(updatedFilm, film));
    }

    @Test
    void shouldUpdateMpa() {
        film = filmDbStorage.create(film);
        film.setMpa(mpaDBStorage.getEntityById(3));
        updatedFilm = filmDbStorage.update(film);

        assertThat(film).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedFilm);
    }

    @Test
    void shouldUpdateGenres() {
        film = filmDbStorage.create(film);
        film.setGenres(Set.of(genreDBStorage.getEntityById(1), genreDBStorage.getEntityById(2)));
        updatedFilm = filmDbStorage.update(film);

        assertThat(film).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedFilm);
    }

}
