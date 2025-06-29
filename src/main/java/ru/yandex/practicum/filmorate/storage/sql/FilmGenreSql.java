package ru.yandex.practicum.filmorate.storage.sql;

public final class FilmGenreSql {
    private FilmGenreSql() {
    }

    public static final String INSERT_FILM_GENRES = """
            INSERT INTO film_genres(film_id, genre_id)
            VALUES (?, ?)
            """;

    public static final String DELETE_FILM_GENRES_BY_FILM_ID = """
            DELETE FROM film_genres
            WHERE film_id = ?
            """;
}
