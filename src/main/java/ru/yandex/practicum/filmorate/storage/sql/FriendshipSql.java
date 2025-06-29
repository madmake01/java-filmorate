package ru.yandex.practicum.filmorate.storage.sql;

public final class FriendshipSql {
    private FriendshipSql() {
    }

    public static final String INSERT = """
                INSERT INTO friendships(requester_user_id, addressee_user_id)
                VALUES (?, ?)
            """;

    public static final String DELETE = """
                DELETE FROM friendships
                WHERE requester_user_id = ? AND addressee_user_id = ?
            """;

    public static final String SELECT_FRIEND_IDS = """
                SELECT addressee_user_id
                FROM friendships
                WHERE requester_user_id = ?
            """;

    public static final String SELECT_COMMON_FRIENDS = """
                SELECT u.user_id, u.email, u.login, u.name, u.birthday
                FROM friendships f1
                JOIN friendships f2 ON f1.addressee_user_id = f2.addressee_user_id
                JOIN users u ON u.user_id = f1.addressee_user_id
                WHERE f1.requester_user_id = ? AND f2.requester_user_id = ?
            """;

    public static final String SELECT_FRIENDS = """
                SELECT u.user_id, u.email, u.login, u.name, u.birthday
                FROM friendships f
                JOIN users u ON u.user_id = f.addressee_user_id
                WHERE f.requester_user_id = ?
            """;
}
