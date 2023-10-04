package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
    private int id;

    @NonNull
    @Email
    private final String email;

    @NonNull
    @NotBlank
    private final String login;

    private String name;

    @PastOrPresent(message = "Дата рождения пользователя не может быть в будущем")
    private final LocalDate birthday;
}
