package ru.yandex.practicum.filmorate.storage.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSql {
    public static final String BASE_USER_SELECT = """
            SELECT user_id, email, login, name, birthday
            FROM users
            """;

    public static final String FIND_USER_SQL = BASE_USER_SELECT + " WHERE user_id = ?";
    public static final String FIND_ALL_USERS_SQL = BASE_USER_SELECT;
    public static final String UPDATE_USER_SQL = """
            UPDATE users
            SET email = ?, login = ?, name = ?, birthday = ?
            WHERE user_id = ?
            """;
    public static final String INSERT_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

}
