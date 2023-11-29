package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {MpaDbStorage.class, GenreDbStorage.class, LikeDbStorage.class, FilmDbStorage.class,
        UserDbStorage.class, FriendDbStorage.class, FilmService.class, FeedDbStorage.class, UserService.class})
class FeedDbStorageTest {

    private final FilmService filmService;
    private final UserService userService;

    private Film film;
    private User newUser;
    private User friendUser;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Новый фильм")
                .description("Описание")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .mpa(Mpa.builder().id(1).build())
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();

        newUser = User.builder()
                .email("nomail@nomail.nan")
                .login("login")
                .name("newUser")
                .birthday(LocalDate.of(2000, 1, 1))
                .friends(new HashSet<>())
                .build();

        friendUser = User.builder()
                .email("friend@friend.nan")
                .login("friend")
                .name("friendUser")
                .birthday(LocalDate.of(1999, 1, 1))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    void shouldCreateNewEventWhenUserAddOrDeleteLikeToFilm() {
        film = filmService.create(film);
        newUser = userService.create(newUser);

        filmService.addLike(film.getId(), newUser.getId());
        System.out.println(film.getId());
        System.out.println(newUser.getId());

        List<Event> events = userService.getEvents(newUser.getId());

        System.out.println(events);
        assertThat(events).isNotNull();
        assertThat(events.get(0).getEventType()).isEqualTo(EventType.LIKE);
        assertThat(events.get(0).getUserId()).isEqualTo(newUser.getId());
        assertThat(events.get(0).getOperation()).isEqualTo(Operation.ADD);
        assertThat(events.get(0).getEntityId()).isEqualTo(film.getId());

        filmService.deleteLike(film.getId(), newUser.getId());

        events = userService.getEvents(newUser.getId());

        assertThat(events).isNotNull();
        assertThat(events.get(1).getEventType()).isEqualTo(EventType.LIKE);
        assertThat(events.get(1).getUserId()).isEqualTo(newUser.getId());
        assertThat(events.get(1).getOperation()).isEqualTo(Operation.REMOVE);
        assertThat(events.get(1).getEntityId()).isEqualTo(film.getId());
    }

    @Test
    void shouldCreateNewEventWhenUserAddFriend() {
        newUser = userService.create(newUser);
        friendUser = userService.create(friendUser);
        System.out.println(newUser.getId());
        userService.createFriendship(newUser.getId(), friendUser.getId());

        List<Event> events = userService.getEvents(newUser.getId());

        assertThat(events).isNotNull();
        assertThat(events.get(0).getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(events.get(0).getUserId()).isEqualTo(newUser.getId());
        assertThat(events.get(0).getOperation()).isEqualTo(Operation.ADD);
        assertThat(events.get(0).getEntityId()).isEqualTo(friendUser.getId());

        userService.deleteFriend(newUser.getId(), friendUser.getId());
        events = userService.getEvents(newUser.getId());

        assertThat(events).isNotNull();
        assertThat(events.get(1).getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(events.get(1).getUserId()).isEqualTo(newUser.getId());
        assertThat(events.get(1).getOperation()).isEqualTo(Operation.REMOVE);
        assertThat(events.get(1).getEntityId()).isEqualTo(friendUser.getId());
    }

}