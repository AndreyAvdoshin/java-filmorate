package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void shouldValidateFilmWithIncorrectReleaseDate() {
        Film film = Film.builder()
                .name("Неправильный фильм")
                .description("Описание фильма с неправильной датой")
                .releaseDate(LocalDate.of(1001, 2, 2))
                .duration(90)
                .build();
        ValidationException exception = assertThrows(ValidationException.class, () -> Validator.validate(film));

        assertEquals("Дата релиза не должда быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionWhenFilmNotFound() {
        ValidationException exception = assertThrows(ValidationException.class, () -> Validator.validate((Film) null));

        assertEquals("Объект с указанным id не найден", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionWhenUserNotFound() {
        ValidationException exception = assertThrows(ValidationException.class, () -> Validator.validate((User) null));

        assertEquals("Объект с указанным id не найден", exception.getMessage());
    }
}