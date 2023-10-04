package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {


    @Test
    void shouldValidateFilmWithIncorrectReleaseDate() {
        Film film = new Film("Неправильный фильм", "Описание фильма с неправильной датой",
                LocalDate.of(1001, 2, 2), 90);
        ValidationException exception = assertThrows(ValidationException.class, () -> Validator.validate(film));

        assertEquals("Дата релиза не должда быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionWhenFilmNotFound() {
        ValidationException exception = assertThrows(ValidationException.class, () -> Validator.validate((Film) null));

        assertEquals("Фильм с указанным id не найден", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionWhenUserNotFound() {
        ValidationException exception = assertThrows(ValidationException.class, () -> Validator.validate((User) null));

        assertEquals("Пользователь с указанным id не найден", exception.getMessage());
    }
}