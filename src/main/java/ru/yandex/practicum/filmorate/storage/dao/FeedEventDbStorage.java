package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.feedevent.FeedEvent;
import ru.yandex.practicum.filmorate.storage.FeedEventStorage;
import ru.yandex.practicum.filmorate.storage.sql.FeedEventSql;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedEventDbStorage implements FeedEventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<FeedEvent> feedEventRowMapper;

    @Override
    public FeedEvent persist(FeedEvent feedEvent) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(FeedEventSql.INSERT_EVENT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setTimestamp(1, Timestamp.from(feedEvent.getTimestamp()));
            preparedStatement.setLong(2, feedEvent.getUserId());
            preparedStatement.setString(3, feedEvent.getEventType().name());
            preparedStatement.setString(4, feedEvent.getOperation().name());
            preparedStatement.setLong(5, feedEvent.getEntityId());
            return preparedStatement;
        }, keyHolder);

        feedEvent.setId(requireGeneratedId(keyHolder));
        return feedEvent;
    }

    @Override
    public List<FeedEvent> findByUserId(Long userId) {
        return jdbcTemplate.query(FeedEventSql.FIND_USER_EVENTS, feedEventRowMapper, userId);
    }

    private Long requireGeneratedId(KeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated id");
        }
        return key.longValue();
    }
}
