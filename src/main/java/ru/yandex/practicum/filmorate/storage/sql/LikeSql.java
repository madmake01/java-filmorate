package ru.yandex.practicum.filmorate.storage.sql;

public final class LikeSql {
    public static final String BASE_QUERY_TO_GET_POPULAR_FILMS = """
            SELECT
                f.film_id,
                f.name          AS film_name,
                f.description   AS film_description,
                f.release_date  AS film_release_date,
                f.duration      AS film_duration,
                f.rating_id,
                r.name          AS rating_name,
            
                '['
                  || GROUP_CONCAT(
                       DISTINCT
                       '{ "id": ' || g.genre_id || ', "name": "' || g.name || '" }'
                       ORDER BY g.genre_id SEPARATOR ', '
                     )
                  || ']' AS genres,
            
                '['
                  || GROUP_CONCAT(
                       DISTINCT
                       '{ "id": ' || d.id || ', "name": "' || d.name || '" }'
                       ORDER BY d.id SEPARATOR ', '
                     )
                  || ']' AS directors,
            
                COUNT(DISTINCT l.user_id) AS like_count
            
            FROM films AS f
            JOIN ratings          AS r  ON r.rating_id = f.rating_id
            
            /* основной JOIN для сбора всех жанров */
            LEFT JOIN film_genres    AS fg ON fg.film_id = f.film_id
            LEFT JOIN genres         AS g  ON g.genre_id = fg.genre_id
            
            LEFT JOIN films_directors AS fd ON fd.film_id = f.film_id
            LEFT JOIN directors      AS d  ON d.id = fd.director_id
            
            LEFT JOIN film_likes     AS l  ON l.film_id = f.film_id
            
            /* сюда подставляем дополнительный JOIN для фильтрации по жанру */
            %s
            
            /* тут — только фильтр по году (жанр уже отфильтрован JOIN-ом выше) */
            WHERE %s
            
            GROUP BY
                f.film_id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                r.name
            
            ORDER BY like_count DESC
            LIMIT ?
            """;

    public static final String INSERT_LIKE = """
                MERGE INTO film_likes(user_id, film_id) VALUES (?, ?)
            """;
    public static final String DELETE_LIKE = """
                DELETE FROM film_likes WHERE user_id = ? AND film_id = ?
            """;

    public static final String SELECT_TOP_FILMS_BY_LIKES = BASE_QUERY_TO_GET_POPULAR_FILMS + """
                        GROUP BY f.film_id, g.genre_id
                        ORDER BY like_count DESC
                        LIMIT ?
            """;

    private LikeSql() {
    }
}
