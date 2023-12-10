package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
class UserDbStorageTest {
    private final FriendDbStorage friendDbStorage;
    private final UserDbStorage userDbStorage;

    User newUser;
    User friendUser;
    User savedUser;

    @Autowired
    public UserDbStorageTest(JdbcTemplate jdbcTemplate) {
        this.friendDbStorage = new FriendDbStorage(jdbcTemplate);
        this.userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @BeforeEach
    public void setUp() {
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
    public void shouldCreateNewUser() {
        savedUser = userDbStorage.create(newUser);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void shouldUpdateUser() {
        User updatedUser = User.builder()
                .email("newmail@newmail.nan")
                .login("log")
                .name("updatedUser")
                .birthday(LocalDate.of(2010, 1, 1))
                .friends(new HashSet<>())
                .build();

        newUser = userDbStorage.create(newUser);
        updatedUser.setId(newUser.getId());
        User savedUser = userDbStorage.update(updatedUser);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);
    }

    @Test
    void shouldDeleteUser() {
        newUser = userDbStorage.create(newUser);
        userDbStorage.delete(newUser.getId());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userDbStorage.getEntityById(newUser.getId())
        );
    }

    @Test
    public void shouldGetFriendshipByUsers() {

        newUser = userDbStorage.create(newUser);
        friendUser = userDbStorage.create(friendUser);

        friendDbStorage.createFriendship(newUser.getId(), friendUser.getId());

        List<User> friends = friendDbStorage.getFriends(newUser.getId());

        assertThat(friends).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(friendUser));
    }

    @Test
    public void shouldDeleteFriendshipByUsers() {

        newUser = userDbStorage.create(newUser);
        friendUser = userDbStorage.create(friendUser);

        friendDbStorage.createFriendship(newUser.getId(), friendUser.getId());
        friendDbStorage.deleteFriend(newUser.getId(), friendUser.getId());

        List<User> friends = friendDbStorage.getFriends(newUser.getId());

        assertThat(friends)
                .isNotNull()
                .isEqualTo(new ArrayList<>());
    }

    @Test
    public void shouldGetCommonFriends() {

        User commonUser = User.builder()
                .email("friend@common.ru")
                .login("common")
                .name("common")
                .birthday(LocalDate.of(2008, 8, 1))
                .friends(new HashSet<>())
                .build();

        newUser = userDbStorage.create(newUser);
        friendUser = userDbStorage.create(friendUser);
        commonUser = userDbStorage.create(commonUser);

        friendDbStorage.createFriendship(newUser.getId(), commonUser.getId());
        friendDbStorage.createFriendship(friendUser.getId(), commonUser.getId());

        List<User> commonFriends = friendDbStorage.getCommonFriends(newUser.getId(), friendUser.getId());

        assertThat(commonFriends)
                .isNotNull()
                .isEqualTo(List.of(commonUser));
    }

    @Test
    public void shouldGetFriendsIds() {

        newUser = userDbStorage.create(newUser);
        friendUser = userDbStorage.create(friendUser);

        friendDbStorage.createFriendship(newUser.getId(), friendUser.getId());

        Set<Integer> friends = friendDbStorage.getFriendsId(newUser.getId());

        assertThat(friends)
                .isNotNull()
                .isEqualTo(Set.of(friendUser.getId()));
    }
}
