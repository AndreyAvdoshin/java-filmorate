package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;

public class Validator {
    private Validator() {
    }

    public static void validate(Film film) {
        if (film == null) {
            throw new ValidationException("Фильм с указанным id не найден");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не должда быть раньше 28 декабря 1895 года");
        }
    }

    public static void validate(User user) {
        if (user == null) {
            throw new ValidationException("Пользователь с указанным id не найден");
        }
    }
}
