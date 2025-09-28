package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmsExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {
        Map<Long, Film> films = new LinkedHashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            Film film = films.get(filmId);

            if (film == null) {
                film = createFilm(rs);
                films.put(filmId, film);
            }

            Genre genre = FilmMappingUtil.mapGenre(rs);
            if (genre != null) {
                film.getGenres().add(genre);
            }

            Director director = FilmMappingUtil.mapDirector(rs);
            if (director != null) {
                film.getDirectors().add(director);
            }
        }

        return new ArrayList<>(films.values());
    }

    private Film createFilm(ResultSet rs) throws SQLException {
        Film film = FilmMappingUtil.mapFilm(rs);
        film.setRating(FilmMappingUtil.mapRating(rs));
        film.setGenres(new ArrayList<>());
        film.setDirectors(new ArrayList<>());
        return film;
    }
}
