package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmMapper implements RowMapper<Film> {

    private final GenreDbStorage genreDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final DirectorDbStorage directorDbStorage;

    public FilmMapper(GenreDbStorage genreDbStorage,
                      LikeDbStorage likeDbStorage,
                      DirectorDbStorage directorDbStorage) {
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.directorDbStorage = directorDbStorage;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa.id"), rs.getString("mpa.name")))
                .genres(genreDbStorage.getAllGenresByFilmId(rs.getInt("id")))
                .directors(directorDbStorage.getAllDirectorsByFilmId(rs.getInt("id")))
                .likes(likeDbStorage.getLikesByFilmId(rs.getInt("id")))
                .build();
        film.setId(rs.getInt("id"));
        return film;
    }
}
