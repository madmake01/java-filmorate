package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
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
        Film film = null;
        List<Genre> genres = new ArrayList<>();
        List<Director> directors = new ArrayList<>();

        while (rs.next()) {
            if (film == null) {
                film = FilmMappingUtil.mapFilm(rs);
                film.setRating(FilmMappingUtil.mapRating(rs));
            }

            Genre genre = FilmMappingUtil.mapGenre(rs);
            if (genre != null) {
                genres.add(genre);
            }

            Director director = FilmMappingUtil.mapDirector(rs);
            if (director != null) {
                directors.add(director);
            }
        }

        if (film != null) {
            film.setGenres(genres);
            film.setDirectors(directors);
        }

        return film;
    }
}
