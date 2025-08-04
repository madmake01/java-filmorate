package ru.yandex.practicum.filmorate.storage.sql;

public final class LikeSql {
    public static final  String BASE_QUERY_TO_GET_POPULAR_FILMS = """
                         SELECT
                          f.film_id       AS film_id,
                          f.name          AS film_name,
                          f.description   AS film_description,
                          f.release_date  AS film_release_date,
                          f.duration      AS film_duration,
                          f.rating_id     AS rating_id,
                          r.name          AS rating_name,
                          g.genre_id      AS genre_id,
                          g.name          AS genre_name,
                          d.id            AS director_id,
                          d.name          AS director_name,
                          COUNT(fl.user_id) AS like_count
                        FROM films f
                        JOIN ratings r ON f.rating_id = r.rating_id
                        LEFT JOIN film_genres fg ON f.film_id = fg.film_id
                        LEFT JOIN genres g ON fg.genre_id = g.genre_id
                        LEFT JOIN films_directors AS fd ON fd.film_id=f.film_id
                        LEFT JOIN directors AS d ON fd.director_id=d.id
                        LEFT JOIN film_likes AS fl ON fl.film_id=f.film_id
            """;

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

    public static final String SELECT_TOP_FILMS_BY_LIKES = BASE_QUERY_TO_GET_POPULAR_FILMS + """
                        GROUP BY f.film_id, g.genre_id
                        ORDER BY like_count DESC
                        LIMIT ?
            """;

    private LikeSql() {
    }
}
