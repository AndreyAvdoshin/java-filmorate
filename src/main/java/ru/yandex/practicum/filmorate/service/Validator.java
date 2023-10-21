package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

public class Validator {
    private Validator() {
    }

    public static <T extends Entity> void validate(T entity) {
        if (entity == null) {
            throw new ValidationException("Объект с указанным id не найден");
        } else if (entity.getClass() == Film.class && ((Film) entity).getReleaseDate().isBefore(
                LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не должда быть раньше 28 декабря 1895 года");
        }
    }
}
