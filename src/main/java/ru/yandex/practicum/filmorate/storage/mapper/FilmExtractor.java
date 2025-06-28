package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmExtractor implements ResultSetExtractor<Film> {

    @Override
    public Film extractData(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return null;
        }
        return mapFilmWithGenres(rs);
    }

    private Film mapFilmWithGenres(ResultSet rs) throws SQLException {
        Film film = FilmMappingUtil.mapFilm(rs);
        film.setRating(FilmMappingUtil.mapRating(rs));
        film.setGenres(collectGenres(rs));
        return film;
    }

    private List<Genre> collectGenres(ResultSet rs) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        do {
            Genre genre = FilmMappingUtil.mapGenre(rs);
            if (genre != null) {
                genres.add(genre);
            }
        } while (rs.next());
        return genres;
    }
}
