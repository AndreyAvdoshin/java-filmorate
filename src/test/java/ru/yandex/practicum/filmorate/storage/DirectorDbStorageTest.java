package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {MpaDbStorage.class, GenreDbStorage.class, LikeDbStorage.class, FilmDbStorage.class,
        UserDbStorage.class, FriendDbStorage.class, DirectorDbStorage.class})
public class DirectorDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorDbStorage directorDbStorage;
    private final FilmDbStorage filmDbStorage;
    Director director;
    Director updatedDirector;

    @BeforeEach
    void setUp() {
        director = Director.builder().name("Новый режиссер").build();
        updatedDirector = Director.builder().name("Обновленный режиссер").build();
    }

    @Test
    void shouldCreateDirector() {
        Director savedDirector = directorDbStorage.create(director);
        assertThat(savedDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(director);
    }

    @Test
    void shouldUpdateDirector() {
        director = directorDbStorage.create(director);
        updatedDirector.setId(director.getId());
        Director savedDirector = directorDbStorage.update(updatedDirector);

        assertThat(savedDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedDirector);
    }

    @Test
    void shouldDeleteDirector() {
        director = directorDbStorage.create(director);
        directorDbStorage.delete(director.getId());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> directorDbStorage.getEntityById(director.getId())
        );
    }

    @Test
    void shouldGetAllDirectorsByFilmId() {
        director = directorDbStorage.create(director);
        Director director2 = Director.builder().name("Старый режиссер").build();
        director2 = directorDbStorage.create(director2);

        Film film = Film.builder()
                .name("Другой фильм")
                .description("Другое описание")
                .releaseDate(LocalDate.of(2001, 2, 2))
                .duration(90)
                .mpa(Mpa.builder().id(1).name("Комедия").build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .directors(Set.of(director, director2))
                .build();

        film = filmDbStorage.create(film);
        Set<Director> directors = directorDbStorage.getAllDirectorsByFilmId(film.getId());

        assertThat(directors).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Set.of(director, director2));
    }
}
