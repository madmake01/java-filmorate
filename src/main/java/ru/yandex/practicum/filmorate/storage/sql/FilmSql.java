package ru.yandex.practicum.filmorate.storage.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmSql {
    public static final String BASE_FILM_SELECT = """
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
                  d.name          AS director_name
                FROM films f
                JOIN ratings r ON f.rating_id = r.rating_id
                LEFT JOIN film_genres fg ON f.film_id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.genre_id
                LEFT JOIN films_directors AS fd ON fd.film_id=f.film_id
                LEFT JOIN directors AS d ON fd.director_id=d.id
            """;

    public static final String FIND_FILM_SQL = BASE_FILM_SELECT + " WHERE f.film_id = ?;";
    public static final String FIND_ALL_FILMS_SQL = BASE_FILM_SELECT + " ORDER BY f.film_id;";
    public static final String UPDATE_FILM_SQL = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?
            WHERE film_id = ?;
            """;

    public static final String INSERT_FILM = """
            INSERT INTO films (name, description, release_date, duration, rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    public static final String FIND_COMMON_FILMS = """
            SELECT
                f.film_id,
                f.name          AS film_name,
                f.description   AS film_description,
                f.release_date  AS film_release_date,
                f.duration      AS film_duration,
                f.rating_id,
                r.name          AS rating_name,
                '[' || GROUP_CONCAT(
                        DISTINCT '{ "id": ' || g.genre_id || ', "name": "' || g.name || '" }'
                        ORDER BY g.genre_id SEPARATOR ', '
                     ) || ']'    AS genres,
                '[' || GROUP_CONCAT(
                        DISTINCT '{ "id": ' || d.id || ', "name": "' || d.name || '" }'
                        ORDER BY d.id SEPARATOR ', '
                     ) || ']'    AS directors,
                COUNT(DISTINCT fl_all.user_id)      AS like_count
            FROM films f
            JOIN ratings r                ON r.rating_id      = f.rating_id
            JOIN film_likes fl_filter
                       ON fl_filter.film_id = f.film_id
                      AND fl_filter.user_id IN (?, ?)
            LEFT JOIN film_likes fl_all   ON fl_all.film_id   = f.film_id
            
            LEFT JOIN film_genres     fg  ON fg.film_id       = f.film_id
            LEFT JOIN genres          g   ON g.genre_id       = fg.genre_id
            LEFT JOIN films_directors fd  ON fd.film_id       = f.film_id
            LEFT JOIN directors       d   ON d.id             = fd.director_id
            WHERE fl_filter.user_id IS NOT NULL
            GROUP BY
                f.film_id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                r.name
            HAVING COUNT(DISTINCT fl_filter.user_id) = 2
            ORDER BY like_count DESC;
            """;

    public static final String DELETE_FILM = """
            DELETE FROM films WHERE film_id = ?
            """;

    public static final String BASE_SORT_QUERY = """
            SELECT
                f.film_id,
                f.name         AS film_name,
                f.description  AS film_description,
                f.release_date AS film_release_date,
                f.duration     AS film_duration,
                f.rating_id,
                r.name         AS rating_name,
                '['
                  || GROUP_CONCAT(
                       DISTINCT
                       '{ "id": ' || g.genre_id || ', "name": "' || g.name || '" }'
                       ORDER BY g.genre_id
                       SEPARATOR ', '
                     )
                  || ']' AS genres,
                '['
                  || GROUP_CONCAT(
                       DISTINCT
                       '{ "id": ' || d.id || ', "name": "' || d.name || '" }'
                       ORDER BY d.id
                       SEPARATOR ', '
                     )
                  || ']' AS directors,
                COUNT(DISTINCT l.user_id) AS like_count
            FROM films AS f
            JOIN ratings AS r
              ON f.rating_id = r.rating_id
            LEFT JOIN film_genres AS fg
              ON f.film_id = fg.film_id
            LEFT JOIN genres AS g
              ON fg.genre_id = g.genre_id
            LEFT JOIN films_directors AS fd
              ON f.film_id = fd.film_id
            LEFT JOIN directors AS d
              ON fd.director_id = d.id
            LEFT JOIN film_likes AS l
              ON f.film_id = l.film_id
            WHERE %s
            GROUP BY
                f.film_id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                r.name
            ORDER BY
                like_count DESC;
            """;
}
