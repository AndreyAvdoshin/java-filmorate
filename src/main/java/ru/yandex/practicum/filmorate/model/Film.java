package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NonNull
    @NotBlank
    private final String name;

    @NonNull
    @NotBlank
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NonNull
    private final LocalDate releaseDate;

    @Positive
    private final int duration;
}
