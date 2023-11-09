package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends Entity {

    @NonNull
    @Email
    private String email;

    @NonNull
    @NotBlank
    private String login;

    @NonNull
    private String name;

    private Set<Integer> friends = new HashSet<>();

    @PastOrPresent(message = "Дата рождения пользователя не может быть в будущем")
    private LocalDate birthday;

    public void setFriend(Integer id) {
        friends.add(id);
    }

    public List<Integer> getFriends() {
        return new ArrayList<>(friends);
    }

    public void removeFriend(Integer id) {
        friends.remove(id);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();

        values.put("name", name);
        values.put("email", email);
        values.put("login", login);
        values.put("birthday", birthday);
        values.put("friends", getFriends());
        values.put("created", LocalDateTime.now());

        return  values;
    }
}
