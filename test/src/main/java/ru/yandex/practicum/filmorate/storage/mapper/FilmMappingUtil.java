package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMappingUtil {

    public static Film mapFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("film_description"));
        film.setReleaseDate(rs.getDate("film_release_date").toLocalDate());
        film.setDuration(rs.getInt("film_duration"));
        return film;
    }

    public static Rating mapRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getLong("rating_id"));
        rating.setName(rs.getString("rating_name"));
        return rating;
    }

    public static Genre mapGenre(ResultSet rs) throws SQLException {
        Long genreId = rs.getObject("genre_id", Long.class);
        if (genreId == null) {
            return null;
        }

        Genre genre = new Genre();
        genre.setId(genreId);
        genre.setName(rs.getString("genre_name"));
        return genre;
    }

    public static Director mapDirector(ResultSet rs) throws SQLException {
        long directorId = rs.getLong("director_id");
        String directorName = rs.getString("director_name");

        if (directorName == null || directorName.isEmpty()) {
            return null;
        }

        return new Director(directorId, directorName);
    }
}
