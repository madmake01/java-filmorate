package ru.yandex.practicum.filmorate.storage.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenreSql {
    public static final String BASE_SELECT = """
            SELECT genre_id, name
            FROM genres
            """;

    public static final String FIND_BY_ID = BASE_SELECT + " WHERE genre_id = ?";
    public static final String FIND_ALL = BASE_SELECT + " ORDER BY genre_id";
}
