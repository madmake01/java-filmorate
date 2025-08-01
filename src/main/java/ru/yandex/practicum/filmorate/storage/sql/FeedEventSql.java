package ru.yandex.practicum.filmorate.storage.sql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedEventSql {

    public static final String INSERT_EVENT = """
            INSERT INTO feed_events (timestamp, user_id, event_type, operation, entity_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    public static final String FIND_USER_EVENTS = """
            SELECT event_id, timestamp, user_id, event_type, operation, entity_id
            FROM feed_events
            WHERE user_id = ?
            """;
}
