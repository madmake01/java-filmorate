package ru.yandex.practicum.filmorate.storage.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RatingSql {
    public static final String BASE_SELECT = """
            SELECT rating_id, name
            FROM ratings
            """;

    public static final String FIND_BY_ID = BASE_SELECT + " WHERE rating_id = ?";
    public static final String FIND_ALL = BASE_SELECT + " ORDER BY rating_id";
}
