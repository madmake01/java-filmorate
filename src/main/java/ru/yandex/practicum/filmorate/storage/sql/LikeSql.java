package ru.yandex.practicum.filmorate.storage.sql;

public final class LikeSql {
    public static final String INSERT_LIKE = """
                INSERT INTO film_likes(user_id, film_id) VALUES (?, ?)
            """;
    public static final String DELETE_LIKE = """
                DELETE FROM film_likes WHERE user_id = ? AND film_id = ?
            """;
    public static final String SELECT_LIKE_COUNTS = """
                SELECT film_id, COUNT(user_id) AS like_count
                FROM film_likes
                GROUP BY film_id
            """;
    public static final String SELECT_TOP_FILMS_BY_LIKES = """
                SELECT
                            f.film_id,
                            f.name,
                            f.description,
                            f.release_date,
                            f.duration,
                            f.rating_id
                        FROM films f
                        LEFT JOIN (
                            SELECT film_id, COUNT(user_id) AS like_count
                            FROM film_likes
                            GROUP BY film_id
                        ) fl ON f.film_id = fl.film_id
                        ORDER BY fl.like_count DESC
                        LIMIT ?
            """;

    private LikeSql() {
    }
}
