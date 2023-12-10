package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.annotation.EqualOrAfterSystemReleaseDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Film extends Entity {

    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
    @EqualOrAfterSystemReleaseDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Mpa mpa;
    private Set<Integer> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private Set<Director> directors = new HashSet<>();

    public void setLike(int id) {
        likes.add(id);
    }

    public void removeLike(Integer id) {
        likes.remove(id);
    }

    public int getLikesCount() {
        return likes.size();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();

        values.put("name", name);
        values.put("description", description);
        values.put("release_Date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_Id", mpa.getId());
        values.put("created", LocalDateTime.now());

        return values;
    }
}
