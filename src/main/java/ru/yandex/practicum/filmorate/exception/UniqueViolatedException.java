package ru.yandex.practicum.filmorate.exception;

public class UniqueViolatedException extends RuntimeException {

    public UniqueViolatedException(String message) {
        super(message);
    }
}
