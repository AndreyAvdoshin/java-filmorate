package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends Entity {

    @NonNull
    @Email
    private final String email;

    @NonNull
    @NotBlank
    private final String login;

    @NonNull
    private String name;

    private final Set<Integer> friends = new HashSet<>();

    @PastOrPresent(message = "Дата рождения пользователя не может быть в будущем")
    private final LocalDate birthday;

    public void setFriend(Integer id) {
        friends.add(id);
    }

    public List<Integer> getFriends() {
        return new ArrayList<>(friends);
    }

    public void removeFriend(Integer id) {
        friends.remove(id);
    }
}
