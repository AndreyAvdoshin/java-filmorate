package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@EqualsAndHashCode(callSuper = true)
public class Film extends Entity {

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

    private int mpa;

    private Set<Integer> likes = new HashSet<>();

    private Set<Integer> genres = new HashSet<>();

    public void setLike(int id) {
        likes.add(id);
    }

    public void removeLike(Integer id) {
        likes.remove(id);
    }

    public int getLikesCount() {
        return likes.size();
    }
}
