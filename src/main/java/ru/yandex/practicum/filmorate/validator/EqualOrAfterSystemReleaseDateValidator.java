package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.annotation.EqualOrAfterSystemReleaseDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class EqualOrAfterSystemReleaseDateValidator implements ConstraintValidator<EqualOrAfterSystemReleaseDate,
        LocalDate> {

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate != null && (localDate.equals(LocalDate.of(1895, 12, 28))
                || localDate.isAfter(LocalDate.of(1895, 12, 28)));
    }
}
