package ru.yandex.practicum.filmorate.mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class FilmMapper implements RowMapper<Film> {

    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final LikeStorage likeStorage;

    public FilmMapper(MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage, LikeStorage likeStorage) {
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.likeStorage = likeStorage;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaDbStorage.getEntityById(rs.getInt("mpa_id")))
                .genres(genreDbStorage.getAllGenresByFilmId(rs.getInt("id")))
                .likes(likeStorage.getLikesByFilmId(rs.getInt("id")))
                .build();
        film.setId(rs.getInt("id"));
        return film;
    }
}
